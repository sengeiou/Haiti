package com.aimir.init;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * Init Data Tool
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */

//TODO Location Constraint가 name, supplier 두개라서
// ReferenceBy시 2개의 조건이 들어가야 하는데
// 현재 기초데이터 관리에서는 1개의 조건만 넣을 수 있음
// 이부분 보완 필요
public class InitData {
	private static Log log = LogFactory.getLog(InitData.class);

	private ApplicationContext ctx;

	public InitData() {

		//ctx = new FileSystemXmlApplicationContext("src/main/resources/applicationContext.xml");
		ctx = new FileSystemXmlApplicationContext("src/main/resources/applicationContext-hibernate5.xml");
	}

	public void initData(String dataDir) throws Exception {
		TransactionStatus txstatus = null;
		HibernateTransactionManager txmanager = (HibernateTransactionManager) ctx.getBean("transactionManager");

		try {
			txstatus = txmanager.getTransaction(null);

			FileReader readFile = new FileReader(dataDir);
			List<ITableIterator> list = readFile.getTables();

			StringBuffer columnName = null;
			for (int i = 0; i < list.size(); i++) {
				ITableIterator itableIt = list.get(i);

				while (itableIt.next()) {
					ITable itable = itableIt.getTable();
					String tableName = itable.getTableMetaData().getTableName();
					Column[] columns = itable.getTableMetaData().getColumns();
					int rowCount = itable.getRowCount();
					columnName = new StringBuffer();
					for (int j = 0; j < columns.length; j++) {
						columnName.append(columns[j].getColumnName() + " ");
					}
					log.info("====================================================================================");
					log.info("INIT Data Table[" + tableName + "] Column[" + columnName.toString() + "] Count[" + rowCount + "]");
					int r = 0;
					try {
						for (r = 0; r < rowCount; r++) {
							log.info("#" + r + "=================================================================================");
							save(r, tableName, columns, itable, dataDir);
						}
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e);
					}

					log.info("END Data Table[" + tableName + "] Count[" + (r + 1) + "]");
				}
			}
			txmanager.commit(txstatus);
		} catch (Exception e) {
			log.error("InitData error - " + e.getMessage(), e);
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void save(int row, String tableName, Column[] columns, ITable itable, String lang) throws Exception {

		String pkgName = "com.aimir.model.";
		Class instance = Class.forName(pkgName + tableName + "");
		// 위 코드는 실행하는 환경에 따라서 다르게 동작하기 때문에
		// 컨텍스트의 클래스 로더를 사용하는 환경에서는 어플리케이션이 자신의 클래스를 로드하기 위해 사용해야하는 클래스 로드를 제공
		// 아래 코드가 올바른 코드

		// ClassLoader cl = Thread.currentThread().getContextClassLoader();
		// if(cl == null) cl = getClass().getClassLoader();//fallback
		// Class instance = cl.loadClass(pkgName+tableName+"");

		tableName = getTableName(tableName);
		Object target = instance.newInstance();
		// Method[] method = instance.getDeclaredMethods();
		Method[] method = instance.getMethods();
		ObjectRelations orm = new ObjectRelations(instance);

		String columnName = null;
		Object val = null;
		try {
			for (int c = 0; c < columns.length; c++) {

				columnName = columns[c].getColumnName();
				val = itable.getValue(row, columnName);

				log.info("Table[" + tableName + "] Column[" + columnName + "] Value[" + val + "]");
				// columnName에 lang이 포함되어 있는지 검사하여 있으면 삭제한다.
				// model의 속성에는 lang이 없기 때문에 삭제해야만 한다.
				/*
				if (lang != null && !"".equals(lang)) {
				    if (columnName.endsWith("_" + lang)) {
				        columnName = columnName.substring(0,
				                columnName.lastIndexOf("_" + lang));
				    }
				}
				 */

				// 모델 클래스의 속섬영과 동일하게 선언되어야 한다. 틀리면 method 기준으로 찾는다.
				if (orm.checkRelationOrObject(columnName)) {
					Class assInstance = orm.getRelationClass(columnName);
					String searchName = orm.getReferencedBy(columnName);
					String refClassName = getTableName(assInstance.getName());
					Object assTarget = null;
					Class<?> expectedType = getParameterTypes(searchName, assInstance);

					if (val != null) {
						val = TypeCast.stringTocast(expectedType, val.toString());
					}

					if (searchName != null && !"".equals(searchName)) {
						Object[] param = new Object[] { searchName, val };
						assTarget = InvokeUtil.daoGetter(ctx, refClassName.toLowerCase(), param, "findbycondition");
					}

					if (assTarget != null) {
						InvokeUtil.objectSetter(target, assTarget, method, columnName);
					}

				} else {
					InvokeUtil.objectSetter(ctx, orm, target, val, method, columnName);
				}
			}
			InvokeUtil.daoSetter(ctx, tableName, target);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Table[" + tableName + "] Column[" + columnName + "] Value[" + val + "]", e.getCause());
		}
	}

	public Class<?> getParameterTypes(String attrName, Class<?> checkClass) {

		try {
			final Field field = checkClass.getDeclaredField(attrName);
			if (field != null) {
				Class<?> fieldType = field.getType();
				return fieldType;
			}
		} catch (SecurityException e) {
			log.error(e);
		} catch (NoSuchFieldException e) {
			if (checkClass.getSuperclass() == null) {
				return null;
			} else {
				final Class<?> superfieldType = getParameterTypes(attrName, checkClass.getSuperclass());
				return superfieldType;
			}
		}
		return null;
	}

	private String getTableName(String tableName) {
		return tableName.substring(tableName.lastIndexOf(".") + 1);
	}

	public static void main(String[] args) {
		log.info("== Init Data ==");
		Log log = LogFactory.getLog(InitData.class);
		try {
			InitData init = new InitData();
			if (args.length == 1) {
				log.info("DataFile[" + args[0] + "]");
				init.initData(args[0]);
			} else {
				log.info("Usage : mvn antrun:run -Dfile.encoding=CHAR_SET -DdataFile=Directory or Filename");
				System.exit(1);
			}
		} catch (Exception ex) {
			log.info(" InitData error : " + ex.getMessage());
			ex.printStackTrace();
			log.error(ex, ex);
		}
	}

}
