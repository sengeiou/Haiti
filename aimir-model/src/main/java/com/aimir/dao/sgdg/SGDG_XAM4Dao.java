package com.aimir.dao.sgdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.aimir.model.sgdg.SGDG_XAM4;
import com.aimir.util.StringUtil;

@Component
public class SGDG_XAM4Dao {
	private String driver;
	private String url;
	private String id;
	private String pw;
	
    protected static Log log = LogFactory.getLog(SGDG_XAM4Dao.class);
	
    private enum XAM4 {
		AR_ID("AR_ID", 1, Types.VARCHAR),
		BILL_APLY_YM("BILL_APLY_YM", 2, Types.VARCHAR),
		WDV_FLAG("WDV_FLAG", 3, Types.VARCHAR),
		CKM_YMD("CKM_YMD", 4, Types.VARCHAR),
		REAL_CKM_YMD("REAL_CKM_YMD", 5, Types.VARCHAR),
		GAUG_NO("GAUG_NO", 6, Types.VARCHAR),
		MXLD_NDL_VAL("MXLD_NDL_VAL", 7, Types.FLOAT),
		MDLD_NDL_VAL("MDLD_NDL_VAL", 8, Types.FLOAT),
		MDLD_NDL_VAL_2("MDLD_NDL_VAL_2", 9, Types.FLOAT),
		MDLD_NDL_VAL_S("MDLD_NDL_VAL_S", 10, Types.FLOAT),
		MDLD_NDL_VAL_R("MDLD_NDL_VAL_R", 11, Types.FLOAT),
		MNLD_NDL_VAL("MNLD_NDL_VAL", 12, Types.FLOAT),
		CKM_MTHD_CD("CKM_MTHD_CD", 13, Types.VARCHAR),
		CKM_RSLT_CD("CKM_RSLT_CD", 14, Types.VARCHAR),
		CKM_REG_DT("CKM_REG_DT", 15, Types.VARCHAR),
		CKM_UPDT_DT("CKM_UPDT_DT", 16, Types.VARCHAR),
		GNR_AVG_PF("GNR_AVG_PF", 17, Types.FLOAT),
		NGT_AVG_PF("NGT_AVG_PF", 18, Types.FLOAT),
		ADD_DT("ADD_DT", 19, Types.VARCHAR),
		ADD_ID("ADD_ID", 20, Types.VARCHAR);
		
		private String column;
		private int order;
		private int type;
		
		XAM4(String column, int order, int type) {
			this.column = column;
			this.order = order;
			this.type = type;
		}
		
		public String getColumn() {
			return this.column;
		}
		
		public int getOrder() {
			return this.order;
		}
		
		public int getType() {
			return this.type;
		}
		
		public static String getColumnsListString() {
			String sep = "";
			StringBuilder result = new StringBuilder("");
			XAM4[] list = XAM4.values();
			
			for ( XAM4 xam : list ) {				
				result.append(sep).append(xam.getColumn());
				sep =", ";
			}
			return result.toString();
		}
	}
    
    public void setDataSource(Map<String, String> source) {
    	this.driver = source.get("driver");
    	this.url = source.get("url");
    	this.id = source.get("id");
    	this.pw = source.get("pw");
    }
	
    @SuppressWarnings("unchecked")
	public boolean excuteSQL(Map<String, Object> condition) {
		boolean isSuccess = true;
		String action = StringUtil.nullToBlank(condition.get("action"));
		
        String driver = this.driver;
        String url = this.url;
        String id = this.id;
        String pw = this.pw;
        log.info(
        		"\n driver: " + driver +
        		"\n url: " + url + 
        		"\n id: " + id +
        		"\n pw: " + pw + 
        		"\n action: " + action
        		);
        try {
            Class.forName(driver);
            log.debug("driver loading success");
        } catch (ClassNotFoundException e) {
            log.error(e, e);
            return isSuccess;
        }
        
        Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
            con = DriverManager.getConnection(url, id, pw);
            con.setAutoCommit(false);
            log.debug("connection success");
            
            if ( action.equals("insert") ) {
            	List<SGDG_XAM4> dataList = (List<SGDG_XAM4>) condition.get("parameter");
            	batchInsert(con, dataList);
            } else if ( action.equals("delete") ){
            	String date = StringUtil.nullToBlank(condition.get("parameter"));
            	delete(con, date);
            }
            
        } catch (Exception e) {
            log.error(e, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException e1) {
                log.error(e, e);
            }
            isSuccess = false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch(Exception e) {}
            try {
                if (con != null) {
                    con.close();
                }
            } catch(Exception e) {}
        }
		return isSuccess;
	}
    
    private String getInsertSQL() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("INSERT INTO SGDG_EAI.SGDG_XAM4 (");
    	sb.append( XAM4.getColumnsListString() );
    	sb.append(") values (");
    	
    	String sep = "";
    	for ( int i = 0 ; i < XAM4.values().length ; i++ ) {
    		sb.append(sep +"?");
    		sep = ", ";
    	}    	
    	sb.append(")");
    	log.info(sb.toString());
    	return sb.toString();
    }
    
    private String getDeleteSQL() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("DELETE FROM SGDG_EAI.SGDG_XAM4 " +
    			"WHERE BILL_APLY_YM = ?");
    	log.info(sb.toString());
    	return sb.toString();
    }
    
    private void setParameters(SGDG_XAM4 xam4, PreparedStatement pstmt) 
    	throws Exception {
    	setValue(xam4.getContractId(), XAM4.AR_ID, pstmt); 
    	setValue(xam4.getBillDate(), XAM4.BILL_APLY_YM, pstmt); 
    	setValue(xam4.getMeteringFlag(), XAM4.WDV_FLAG, pstmt); 
    	setValue(xam4.getMeteringDate(), XAM4.CKM_YMD, pstmt); 
    	setValue(xam4.getRealMeteringDate(), XAM4.REAL_CKM_YMD, pstmt); 
    	setValue(xam4.getGaugeNumber(), XAM4.GAUG_NO, pstmt); 
    	setValue(xam4.getMaxLD(), XAM4.MXLD_NDL_VAL, pstmt);  
    	setValue(xam4.getMiddleLD(), XAM4.MDLD_NDL_VAL, pstmt); 
    	setValue(xam4.getMiddleLD2(), XAM4.MDLD_NDL_VAL_2, pstmt);
    	setValue(xam4.getMiddleLDS(), XAM4.MDLD_NDL_VAL_S, pstmt);
    	setValue(xam4.getMiddleLDR(), XAM4.MDLD_NDL_VAL_R, pstmt);
    	setValue(xam4.getMiniLD(), XAM4.MNLD_NDL_VAL, pstmt); 
    	setValue(xam4.getMeteringMethodCode(), XAM4.CKM_MTHD_CD, pstmt); 
    	setValue(xam4.getMeteringResultCode(), XAM4.CKM_RSLT_CD, pstmt); 
    	setValue(xam4.getMeteringRegDate(), XAM4.CKM_REG_DT, pstmt); 
    	setValue(xam4.getMeteringUpdateDate(), XAM4.CKM_UPDT_DT, pstmt); 
    	setValue(xam4.getGnrAverage(), XAM4.GNR_AVG_PF, pstmt); 
    	setValue(xam4.getNgtAverage(), XAM4.NGT_AVG_PF, pstmt); 
    	setValue(xam4.getGenDate(), XAM4.ADD_DT, pstmt); 
    	setValue(xam4.getGenId(), XAM4.ADD_ID, pstmt); 
    }  
    
    private void setValue(Object value, XAM4 xam4, PreparedStatement pstmt) throws Exception {
    	if ( value != null) {
    		pstmt.setObject(xam4.getOrder(), value, xam4.getType());
    	} else {
    		pstmt.setNull(xam4.getOrder(), xam4.getType());
    	}
    }
    
    private void batchInsert(Connection con, List<SGDG_XAM4> dataList) {
    	PreparedStatement pstmt = null;
    	try {
			pstmt = con.prepareStatement(getInsertSQL());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        int count = 0;
        int maxBatchSize = 1000;
        
        try {
			for ( SGDG_XAM4 xam4 : dataList ) {
				log.debug(xam4.toJSONString());
				setParameters(xam4, pstmt);
				pstmt.addBatch();
				pstmt.clearParameters();
				
				if ( (++count) % maxBatchSize == 0 ) {
					log.debug("execute bactch and clear!!");
					pstmt.executeBatch();
					pstmt.clearBatch();
				}
			}
			
			if (count % maxBatchSize != 0) {
				log.debug("execute batch final!!");
				pstmt.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        log.info("\n ###### batch successs ######");
    }
    
    private void delete(Connection con, String date) {
    	log.debug("\n rollback " + date + " for xam4");
    	PreparedStatement pstmt = null;
    	try {
			pstmt = con.prepareStatement(getDeleteSQL());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}    	
    	try {
			pstmt.setString(1, date);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	log.info("\n ###### rollback success ######");
    }
}
