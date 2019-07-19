package com.aimir.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 검색 조건을 담는 클래스
 * @author YeonKyoung Park(goodjob)
 *
 */
public class SQLWrapper {

	public String[] SQL = {"SUBSTR", "CONCAT"};

    public String[] SQLServer = {"SUBSTRING","+"};
    
    public String[] Oracle10g = {"SUBSTR","||"};
    
    public String[] Derby= {"SUBSTR","||"};
    
	
    Log logger = LogFactory.getLog(SQLWrapper.class);
    
    
    private String getDialrect() {
		Properties prop = new Properties();
		try {
			
			//try{
			    //ApplicationContext ctx 
			    //= new ClassPathXmlApplicationContext(new String[]{"applicationContext-hibernate.xml"});
		    
			    //ResourceBundle resource = (ResourceBundle) ctx.getResource("hibernateProperties");
			    //logger.debug("RESOURCE DIALRECT="+resource.getString("hibernate.dialect"));
			    
			//}catch(Exception e) {
				
			//}

			//prop.load(getClass().getResourceAsStream(
			//		"src/main/resources/jdbc.properties"));
			//FileInputStream fis = new FileInputStream ("/webapp/WEB-INF/config/spring.xml"); 
			//FileInputStream fis = new FileInputStream ("src/main/resources/applicationContext-hibernate.xml");

			prop.load(getClass().getClassLoader()
					.getResourceAsStream("command.properties"));
			return prop.getProperty("hibernate.dialect");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "DerbyDialect";
    }
    
    public SQLWrapper(){
    	
    }
    
    public String getQuery(String queryStr) {
    	String query = "";
    	query = queryStr;
    	logger.debug("Dialrect="+getDialrect());
    	if(getDialrect().endsWith("SQLServerDialect")){
    		
    		for (int i=0; i < SQL.length; i++){

			    query = query.replaceAll(SQL[i], SQLServer[i]);
			    query = query.replaceAll(SQL[i].toLowerCase(), SQLServer[i]);
    		}
    	}
    	if(getDialrect().endsWith("DerbyDialect")){
    		
    		for (int i=0; i < SQL.length; i++){

			    query = query.replaceAll(SQL[i], Derby[i]);
			    query = query.replaceAll(SQL[i].toLowerCase(), Derby[i]);
    		}
    	}
    	if(getDialrect().endsWith("Oracle10gDialect") || getDialrect().endsWith("Oracle11gDialect")){
    		
    		for (int i=0; i < SQL.length; i++){

			    query = query.replaceAll(SQL[i], Oracle10g[i]);
			    query = query.replaceAll(SQL[i].toLowerCase(), Oracle10g[i]);
    		}
    	}
    	logger.debug(query);
    	
    	return query;

    }

}
