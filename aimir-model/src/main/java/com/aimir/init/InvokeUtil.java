package com.aimir.init;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.NameNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * Model,Dao Method Invoke Util
 * @author YeonKyoung Park(goodjob)
 *
 */
public class InvokeUtil {
    private static Log log = LogFactory.getLog(InvokeUtil.class);

    public static void objectSetter(Object target, Object val, Method[] method, String columnName)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{

        for(int i = 0; i < method.length; i++){
            if(method[i].getName().toLowerCase().equals("set"+columnName.toLowerCase())){
                method[i].setAccessible(true);
                Class<?> expectedType = method[i].getParameterTypes()[0];
                method[i].invoke(target, TypeCast.cast(expectedType,val));
            }
        }
    }

    public static String getTableName(String tableName) {
        return tableName.substring(tableName.lastIndexOf(".")+1);
    }

    @SuppressWarnings("unchecked")
    public static void objectSetter(ApplicationContext ctx, ObjectRelations orm, Object target, Object val, Method[] method, String columnName)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NameNotFoundException {

    for(int i = 0; i < method.length; i++){
        if(method[i].getName().toLowerCase().equals("set"+columnName.toLowerCase())){
            method[i].setAccessible(true);
            Class<?> expectedType = method[i].getParameterTypes()[0];
            String searchName = orm.getReferencedByPk(method[i],expectedType);
            if(searchName != null){

                log.info("searchName="+searchName);
                Class assInstance = expectedType;
                log.info("AssName="+assInstance.getName());
                String refClassName = getTableName(assInstance.getName());
                Object assTarget = null;
                if(searchName != null && !"".equals(searchName)){
                    Object[] param = new Object[]{searchName,val};
                    assTarget = daoGetter(ctx, refClassName.toLowerCase(),param, "findbycondition");
                }

                if(assTarget != null){
                    objectSetter(target, assTarget, method, columnName) ;
                }

            }else{
                if(val != null){
                    method[i].invoke(target, TypeCast.cast(expectedType,val));
                }

            }

        }
    }
}

    public static void daoSetter(ApplicationContext ctx, String tableName, Object target)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NameNotFoundException
    {

        String beanName = null;
        for (String name : ctx.getBeanDefinitionNames()) {
            if (name.toLowerCase().equals(tableName.toLowerCase()+"dao")) {
                beanName = name;
                break;
            }
        }

        log.info("BeanName[" + beanName + "]");

        if (beanName == null)
            throw new NameNotFoundException("BeanName[" + tableName + "] not found exception");

        Object dao = ctx.getBean(beanName);
        Method[] daoMethod = dao.getClass().getMethods();
        boolean invoked = false;
        try {
            if(dao != null){
                for(int i = 0; i < daoMethod.length; i++){
                    if(daoMethod[i].getName().equals("add"))
                    {
                        daoMethod[i].setAccessible(true);
                        daoMethod[i].invoke(dao,target);
                        invoked = true;
                        log.info("save:add[" + daoMethod[i].getName()+ "]");
                    }
                }
            }
        }
        catch (Exception e) {}

        /*
         * initData 배치파일 설정 뒤에 -Dupdate=true 만 붙여주면 저장되거나 업데이트 되고 저 설정을 지우면 기존 방법대로 무조건 insert 됨.
         */
        if(!invoked){
            daoMethod = null;
            daoMethod = dao.getClass().getSuperclass().getDeclaredMethods();
            for(int i = 0; i < daoMethod.length; i++){
                if((daoMethod[i].getName().equals("update") && System.getProperty("update") != null)) {
                    daoMethod[i].setAccessible(true);
                    daoMethod[i].invoke(dao,target);
                    invoked = true;
                    log.info("save:add[" + daoMethod[i].getName()+ "]");
                }
            }
        }

    }

    public static Object daoGetter(ApplicationContext ctx, String tableName,
            Object[] param, String methodName)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NameNotFoundException
    {

        String beanName = null;
        for (String name : ctx.getBeanDefinitionNames()) {
            if (name.toLowerCase().equals(tableName.toLowerCase()+"dao")) {
                beanName = name;
                break;
            }
        }
        if (beanName == null)
            throw new NameNotFoundException("BeanName[" + tableName + "] not found exception");

        Object value = null;
        Object dao = ctx.getBean(beanName);
        Method[] daoMethod = dao.getClass().getDeclaredMethods();
        boolean invoked = false;
        if(dao != null){
            for(int i = 0; i < daoMethod.length; i++){
                if(daoMethod[i].getName().toLowerCase().equals(methodName)
                 && daoMethod[i].getParameterTypes().length == param.length)
                {
                    daoMethod[i].setAccessible(true);
                    value = daoMethod[i].invoke(dao,param);
                    invoked = true;
                }
            }
        }

        if(!invoked){
            daoMethod = null;
            daoMethod = dao.getClass().getSuperclass().getDeclaredMethods();
            for(int i = 0; i < daoMethod.length; i++){
                if(daoMethod[i].getName().toLowerCase().equals(methodName)
                 && daoMethod[i].getParameterTypes().length == param.length){
                    daoMethod[i].setAccessible(true);
                    value = daoMethod[i].invoke(dao,param);
                    invoked = true;
                }
            }
        }

        return value;

    }
}
