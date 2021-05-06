package com.aimir.schedule.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.fep.util.DataUtil;

@Service
public class HaitiRevertBillingTask extends ScheduleTask {
	protected static Log log = LogFactory.getLog(HaitiRevertBillingTask.class);
	
	@Resource(name = "transactionManager")
	private HibernateTransactionManager txmanager;
	
	@Autowired
	private BillingBlockTariffDao billingBlockTariffDao;
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-public.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        HaitiRevertBillingTask task = ctx.getBean(HaitiRevertBillingTask.class);
        task.execute(ctx);
        System.exit(0);
	}

	@Override
	public void execute(JobExecutionContext context) { }
	

	private void execute(ApplicationContext ctx) {
		log.info("########### START HaitiRevertBillingTask Task ###############");
		
		Map<Integer, LinkedList<RevertBill>> listData = getDataList(ctx);
		
		log.info("########### END HaitiRevertBillingTask Task ###############");
	}
	
	private Map<Integer, LinkedList<RevertBill>> getDataList(ApplicationContext ctx) {
		Map<Integer , LinkedList<RevertBill>> map = new HashMap<Integer , LinkedList<RevertBill>>();
		
		List<Map<String, Object>> queryList = billingBlockTariffDao.getRevertBillingList();
		if(queryList == null || queryList.size() == 0) {
			log.debug("map is empty!");
		}
		
		for(Map<String, Object> m : queryList) {
			
			String tabletype = String.valueOf(m.get("tabletype"));
			String mdev_id = String.valueOf(m.get("mdev_id"));
			String yyyymmdd = String.valueOf(m.get("yyyymmdd"));
			String hhmmss = String.valueOf(m.get("hhmmss"));
			String writedate = String.valueOf(m.get("writedate"));
			int contract_id = Integer.parseInt(m.get("contract_id").toString());
			long id = Long.parseLong(m.get("id").toString());
			double chargedcredit = Double.parseDouble(m.get("chargedcredit").toString());			
			double bill = Double.parseDouble(m.get("bill").toString());
			double pre_balance = Double.parseDouble(m.get("pre_balance").toString());
			double balance = Double.parseDouble(m.get("balance").toString());
			
			RevertBill r = new RevertBill();
			r.setTabletype(tabletype);
			r.setMdevId(mdev_id);
			r.setYyyymmdd(yyyymmdd);
			r.setHhmmss(hhmmss);
			r.setWritedate(writedate);
			r.setContractId(contract_id);
			r.setId(id);
			r.setChargedcredit(chargedcredit);
			r.setBill(bill);
			r.setPre_balance(pre_balance);
			r.setBalance(balance);
			
			if(map.containsKey(contract_id)) {
				LinkedList<RevertBill> li = map.get(contract_id);
				li.add(r);
				
				map.put(contract_id, li);
			}else {
				LinkedList<RevertBill> li = new LinkedList<RevertBill>();
				li.add(r);
				
				map.put(contract_id, li);
			}
		}
		
		log.debug("total Map Count : " + map.size());
		
		return null;
		
	}
	
	
	private Map<Integer, LinkedList<RevertBill>> getDataListOld(ApplicationContext ctx) {
		Map<Integer , LinkedList<RevertBill>> map = new HashMap<Integer , LinkedList<RevertBill>>();
		
		Statement sql = null;
		Connection con = null;
		ResultSet rs = null;
		
		try {
			DataSource ds = ctx.getBean(DataSource.class);
			
			con = ds.getConnection();
			sql = con.createStatement();
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(" truncate table TEMP_BILLGIN ");
			rs = sql.executeQuery(buffer.toString());
			
			buffer = new StringBuffer();
			buffer.append(" INSERT INTO TEMP_BILLGIN ");
			buffer.append(" SELECT *  FROM  ");
			buffer.append(" ( ");
			buffer.append("     WITH TPREPAY AS  ");
			buffer.append("     ( ");
			buffer.append("         SELECT  ");
			buffer.append("             A.CONTRACT_ID, A.LASTTOKENDATE ");
			buffer.append("         FROM  ");
			buffer.append("         ( ");
			buffer.append("             SELECT  ");
			buffer.append("                 p.*, ");
			buffer.append("                 row_number() OVER (PARTITION BY p.CONTRACT_ID ORDER BY p.LASTTOKENDATE asc) AS row_idx ");
			buffer.append("             FROM  ");
			buffer.append("                 PREPAYMENTLOG_HSW p ");
			buffer.append("             WHERE ");
			buffer.append("                 1 = 1 ");
			buffer.append("                 AND p.CANCEL_DATE IS NULL  ");
			buffer.append("         )A WHERE  ");
			buffer.append("             row_idx = 1 ");
			buffer.append("     ), ");
			buffer.append("     TDAYBILLING AS  ");
			buffer.append("     ( ");
			buffer.append("         SELECT ");
			buffer.append("             * ");
			buffer.append("         FROM  ");
			buffer.append("         ( ");
			buffer.append("             SELECT  ");
			buffer.append("                 bbt.*, ");
			buffer.append("                 row_number() OVER (PARTITION BY bbt.CONTRACT_ID ORDER BY bbt.WRITEDATE desc) AS row_idx ");
			buffer.append("             FROM  ");
			buffer.append("                 BILLING_BLOCK_TARIFF_HSW bbt, TPREPAY tp ");
			buffer.append("             WHERE ");
			buffer.append("                bbt.CONTRACT_ID = tp.CONTRACT_ID ");
			buffer.append("            and bbt.WRITEDATE < tp.LASTTOKENDATE ");
			buffer.append("         ) where ");
			buffer.append("             row_idx = 1 ");
			buffer.append("     ) ");
			buffer.append("     select * from TDAYBILLING ");
			buffer.append(" ) ");
			
			rs = sql.executeQuery(buffer.toString());
			
			buffer = new StringBuffer();
			buffer.append(" select  ");
			buffer.append("    a.* ");
			buffer.append(" from  ");
			buffer.append(" ( ");
			buffer.append("     SELECT 'BBT' AS TABLETYPE, 0 AS ID, bbt.CONTRACT_ID, 0 AS CHARGEDCREDIT,  bbt.MDEV_ID , bbt.BILL, 0 as PRE_BALANCE, bbt.BALANCE, bbt.YYYYMMDD,  bbt.HHMMSS , bbt.WRITEDATE FROM BILLING_BLOCK_TARIFF_HSW bbt ");
			buffer.append("     UNION ALL ");
			buffer.append("     SELECT 'PREPAY' AS TABLETYPE, ph.ID, ph.CONTRACT_ID, ph.CHARGEDCREDIT, '' AS MDEV_ID , 0 AS BILL, ph.PRE_BALANCE, ph.BALANCE, '' AS YYYYMMDD, '' AS HHMMSS, ph.LASTTOKENDATE AS WRITEDATE FROM PREPAYMENTLOG_HSW ph where ph.CANCEL_DATE IS null ");
			buffer.append(" )a, TEMP_BILLGIN tb ");
			buffer.append(" where ");
			buffer.append("     a.contract_id = tb.contract_id ");
			buffer.append("     and a.writedate >= tb.writedate ");
			buffer.append(" order by ");
			buffer.append("     a.contract_id, a.writedate asc ");

			rs = sql.executeQuery(buffer.toString());
			while (rs.next()) {
				String tabletype = rs.getString("tabletype");
				String mdev_id = rs.getString("mdev_id");
				String yyyymmdd = rs.getString("yyyymmdd");
				String hhmmss = rs.getString("hhmmss");
				String writedate = rs.getString("writedate");
				int contract_id = rs.getInt("contract_id");
				long id = rs.getLong("id");
				double chargedcredit = rs.getDouble("chargedcredit");			
				double bill =rs.getDouble("bill");
				double pre_balance =rs.getDouble("pre_balance");
				double balance =rs.getDouble("balance");
				
				RevertBill r = new RevertBill();
				r.setTabletype(tabletype);
				r.setMdevId(mdev_id);
				r.setYyyymmdd(yyyymmdd);
				r.setHhmmss(hhmmss);
				r.setWritedate(writedate);
				r.setContractId(contract_id);
				r.setId(id);
				r.setChargedcredit(chargedcredit);
				r.setBill(bill);
				r.setPre_balance(pre_balance);
				r.setBalance(balance);
				
				if(map.containsKey(contract_id)) {
					LinkedList<RevertBill> li = map.get(contract_id);
					li.add(r);
					
					map.put(contract_id, li);
				}else {
					LinkedList<RevertBill> li = new LinkedList<RevertBill>();
					li.add(r);
					
					map.put(contract_id, li);
				}
			}
			
			log.debug("total Map Count : " + map.size());
		}catch(Exception e) {
			log.error(e,e);
		}finally {
			try {
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			} catch (Exception e) {
			}
			try {
				if (sql != null && !sql.isClosed()) {
					sql.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		
		return map;
	}
	
	
}

class RevertBill {
	private String tabletype;
	private String mdevId;
	private Long id;
	private int contractId;
	private double chargedcredit;
	private double bill;
	private double pre_balance;
	private double balance;
	private String yyyymmdd;
	private String hhmmss;
	private String writedate;
	
	public String getTabletype() {
		return tabletype;
	}
	public void setTabletype(String tabletype) {
		this.tabletype = tabletype;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getContractId() {
		return contractId;
	}
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	public double getChargedcredit() {
		return chargedcredit;
	}
	public void setChargedcredit(double chargedcredit) {
		this.chargedcredit = chargedcredit;
	}
	public double getBill() {
		return bill;
	}
	public void setBill(double bill) {
		this.bill = bill;
	}
	public double getPre_balance() {
		return pre_balance;
	}
	public void setPre_balance(double pre_balance) {
		this.pre_balance = pre_balance;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public String getHhmmss() {
		return hhmmss;
	}
	public void setHhmmss(String hhmmss) {
		this.hhmmss = hhmmss;
	}
	public String getWritedate() {
		return writedate;
	}
	public void setWritedate(String writedate) {
		this.writedate = writedate;
	}
	public String getMdevId() {
		return mdevId;
	}
	public void setMdevId(String mdevId) {
		this.mdevId = mdevId;
	}
	
}
