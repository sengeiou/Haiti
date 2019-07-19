package com.aimir.schedule.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class EquipStatJob extends AimirJob
{
    private static Log log = LogFactory.getLog(EquipStatJob.class);

    private static final String description = "scheduler.job.EquipStatJob";
    private static final String[] paramList = {"runEM","runWM","runGM","runHM","runMCU"};
    private static final String[] paramListDescription = {"scheduler.job.MeteringStat.runEM",
                                                   "scheduler.job.MeteringStat.runWM",
                                                   "scheduler.job.MeteringStat.runGM",
                                                   "scheduler.job.MeteringStat.runHM",
                                                   "scheduler.job.MeteringStat.runVC",
                                                   "scheduler.job.MeteringStat.runMCU"};
    private static final boolean[] paramListRequired = {false,false,false,false,false,false};
    private static final String[] paramListDefault = {"false","false","false","false","false","false"};

    private static boolean isRun = false;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
        String jobName = context.getJobDetail().getDescription();
        log.info("Executing job: " + jobName + " executing at " + new Date());

        if(!isRun)
        {
            isRun = true;
    
            JobDataMap data = context.getMergedJobDataMap();
    
            boolean runEM = false;
            if (data.get("runEM") != null)
                runEM = new Boolean(data.getString("runEM")).booleanValue();
            boolean runWM = false;
            if (data.get("runWM") != null)
                runWM = new Boolean(data.getString("runWM")).booleanValue();
            boolean runGM = false;
            if (data.get("runGM") != null)
                runGM = new Boolean(data.getString("runGM")).booleanValue();
            boolean runHM = false;
            if (data.get("runHM") != null)
                runHM = new Boolean(data.getString("runHM")).booleanValue();
            boolean runVC = false;
            if (data.get("runVC") != null)
                runHM = new Boolean(data.getString("runVC")).booleanValue();
            boolean runMCU = false;
            if (data.get("runMCU") != null)
                runMCU = new Boolean(data.getString("runMCU")).booleanValue();
    

            /*
            Calendar sDate = Calendar.getInstance();
            sDate.add(Calendar.HOUR, -2);
            
            String date = DateTimeUtil.getDateString(sDate.getTime()).substring(0,8);
            String hour = DateTimeUtil.getDateString(sDate.getTime()).substring(8, 10);
    
            if(runEM)
                checkEquipmentState(AimirModel.MI_ENERGY_METER, date, hour);
            if(runWM)
                checkEquipmentState(AimirModel.MI_WATER_METER, date, hour);
            if(runGM)
                checkEquipmentState(AimirModel.MI_GAS_METER, date, hour);
            if(runHM)
                checkEquipmentState(AimirModel.MI_HEAT_METER, date, hour);
            if(runVC)
                checkEquipmentState(AimirModel.MI_VOLUMECORRECTOR, date, hour);
            if(runMCU)
                checkEquipmentStateMCU();
            */
            
            try {
            	//updateAbnormal();
            }
            catch (Exception e) {
            	throw new JobExecutionException(e);
            }
            isRun = false;
        }else
        {
            log.debug("Already "+jobName+" Prcessing..");
        }

        log.info("Executing job: " + jobName + " ending at " + new Date());
    }
    
    
    /*
    private void checkEquipmentStateMCU()
    {
        String checkMCUType = SchedulerProperty.getProperty("checkEquipmentState.mcu.type");
        int checkMCUPort = Integer.parseInt(SchedulerProperty.getProperty("checkEquipmentState.mcu.port"));
        int checkMCUPintTimeout = Integer.parseInt(SchedulerProperty.getProperty("checkEquipmentState.mcu.ping.timeout"));
        
        try
        {
            if(checkMCUType.equals("PORT"))
            {
                checkMCUPortOpen(checkMCUPort);
            }else if(checkMCUType.equals("PING"))
            {
                checkMCUPing(checkMCUPintTimeout);
            }
            
        }catch (Exception e)
        {
            log.error("checkEquipmentStateMCU Exception occured.. " + e.getMessage());
        }

    }
    private void checkMCUPortOpen(int port) throws Exception
    {
        MCUMgrDelegate mcuMgr = new MCUMgrDelegate();
        List<MCUData> listMCU = mcuMgr.getMCUList(null, 1, 65547, null);
        for(int i=0; listMCU!=null && i<listMCU.size();i++)
        {
            MCUData mcu = (MCUData) listMCU.get(i);
            if(mcu.getIpAddr()==null)
                continue;
            try{
                Socket s = new Socket(mcu.getIpAddr(),port);
        
                IUtil.setPropertyValue(mcu.getInstanceName(), "networkStatus", "1");
                log.debug("checkMCUPortOpen mcuId["+mcu.getId() + "] ipAddr["+mcu.getIpAddr()+"] Normal");
            }catch(Exception e)
            {
                IUtil.setPropertyValue(mcu.getInstanceName(), "networkStatus", "0");
                log.debug("checkMCUPortOpen mcuId["+mcu.getId() + "] ipAddr["+mcu.getIpAddr()+"] Abnormal");
                log.error("checkMCUPortOpen Exception occured.. " + e.getMessage());
            }
        }
    }
    private void checkMCUPing(int timeout) throws Exception
    {
        MCUMgrDelegate mcuMgr = new MCUMgrDelegate();
        List<MCUData> listMCU = mcuMgr.getMCUList(null, 1, 65547, null);
        for(int i=0; listMCU!=null && i<listMCU.size();i++)
        {
            MCUData mcu = (MCUData) listMCU.get(i);
            if(mcu.getIpAddr()==null)
                continue;
            try{
                boolean pingStatus = InetAddress.getByName(mcu.getIpAddr()).isReachable(timeout);
                if(pingStatus){
                    IUtil.setPropertyValue(mcu.getInstanceName(), "networkStatus", "1");
                    log.debug("checkMCUPing mcuId["+mcu.getId() + "] ipAddr["+mcu.getIpAddr()+"] Normal");
                }else{
                    IUtil.setPropertyValue(mcu.getInstanceName(), "networkStatus", "0");
                    log.debug("checkMCUPing mcuId["+mcu.getId() + "] ipAddr["+mcu.getIpAddr()+"] Abnormal");
                }
            }catch(Exception e)
            {
                log.error("checkMCUPing Exception occured.. " + e.getMessage());
            }
        }
    }

    private void checkEquipmentState(String meterType, String date, String hour)
    {
        log.debug("checkEquipmentState[" + meterType + "] date=" + date + " hour=" + hour);
        try
        {
            boolean isMsSql = NASProperty.getProperty("mapping").toLowerCase()
                    .equals("mssql");

            String condition = null;
            if (isMsSql)
            {

                condition = " YYYYMMDD='" + date + "' ";
            }else{

                condition = " YYYYMMDD='" + date + "' ";
            }
            int count = 0;
            if (meterType.equals(AimirModel.MI_ENERGY_METER))
                count = getCount("CURRENT_EM", condition);
            else if (meterType.equals(AimirModel.MI_WATER_METER))
                count = getCount("CURRENT_WM", condition);
            else if (meterType.equals(AimirModel.MI_GAS_METER))
                count = getCount("CURRENT_GM", condition);
            else if (meterType.equals(AimirModel.MI_HEAT_METER))
                count = getCount("CURRENT_HM", condition);
            else if (meterType.equals(AimirModel.MI_VOLUMECORRECTOR))
                count = getCount("CURRENT_VC", condition);

            if (count == 0)
            {
                updateAbnormalMeter(meterType);
                updateAbnormalSensor(meterType);
                return;
            }
            ArrayList list = selectAbnormalEquipID(meterType, date, hour);
            if (list != null)
            {
                log.debug("Total number of Abnormal Status of Equipment: ["
                          + list.size() + "]");
                if (list.size()!=0)
                {
                    for(Iterator iter = list.iterator();iter.hasNext();)
                    {
                        String id = (String)iter.next();
                        updateAbnormalMeter(meterType, id);
                        updateAbnormalSensor(meterType, id);
                    }
                }
            }
            list = selectNormalEquipID(meterType, date, hour);
            if (list != null)
            {
                log.debug("Total number of Normal Status of Equipment: ["
                          + list.size() + "]");
                if (list.size()!=0)
                {
                    for(Iterator iter = list.iterator();iter.hasNext();)
                    {
                        String id = (String)iter.next();
                        updateNormalMeter(meterType, id);
                        updateNormalSensor(meterType, id);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("checkEquipmentState[" + meterType
                      + "] Exception occured.. " + e.getMessage(), e);
        }

    }


    private void updateAbnormal() throws Exception
    {
        // resource declaration
        Connection con = null;
        Statement stmt = null;

        try
        {
        	con = JDBCUtil.getConnection();
        	
        	StringBuffer sql = new StringBuffer();
        	sql.append("update mi_mcu set networkStatus=0");
        	stmt = con.createStatement();
            stmt.execute(sql.toString());
            
        	for (int i = 0; i < AimirModel.SENSOR_TABLE_LIST.length; i++) {
	        	sql.setLength(0);
	            sql.append("update " + AimirModel.SENSOR_TABLE_LIST[i] + " set COMMSTATE=0");
	            stmt.execute(sql.toString());
        	}
        	
        	for (int i = 0; i < AimirModel.METER_TABLE_LIST.length; i++) {
	            sql.setLength(0);
	            sql.append("update " + AimirModel.METER_TABLE_LIST[i] + " set networkStatus=0");
	            stmt.execute(sql.toString());
        	}
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            if (stmt != null) {
            	try {
            		stmt.close();
            	}
            	catch (Exception e) {}
            }
            if (con != null) {
            	try {
            		con.close();
            	}
            	catch (Exception e) {}
            }
        }        
    }
    

    private void updateNormalSensor(String meterType, String id) throws Exception
    {
        String zbStr = null;
        if (meterType.equals(AimirModel.MI_ENERGY_METER))
            zbStr = AimirModel.MI_ZRU;
        else if (meterType.equals(AimirModel.MI_WATER_METER)
                 || meterType.equals(AimirModel.MI_GAS_METER)
                 || meterType.equals(AimirModel.MI_VOLUMECORRECTOR))
            zbStr = AimirModel.MI_ZEUPLS;
        else if (meterType.equals(AimirModel.MI_HEAT_METER))
           zbStr = AimirModel.MI_IEIU;

        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n UPDATE MI_").append(zbStr).append(" SET COMMSTATE=1");
        sql.append("\n WHERE INSTANCENAME IN ");
        sql.append("\n       ( ");
        sql.append("\n           SELECT  SECONDINSTANCE ");
        sql.append("\n           FROM    MI_ATTACHEDSENSOR ");
        sql.append("\n           WHERE   FIRSTINSTANCE IN ");
        sql.append("\n                   ( ");
        sql.append("\n                       SELECT  INSTANCENAME ");
        sql.append("\n                       FROM    MI_").append(meterType).append(" ");
        sql.append("\n                       WHERE   ID='").append(id).append("' ");
        sql.append("\n                   ) ");
        sql.append("\n       ) ");

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            pstmt.executeUpdate();
            log.debug("Equipment=[" + meterType + " " + id + " " + zbStr
                      + " Sensor] status is changed as 'Normal'");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }        
    }

    private void updateNormalMeter(String meterType, String id) throws Exception
    {
        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n UPDATE MI_").append(meterType).append(" SET NETWORKSTATUS=1 ");
        sql.append("\n WHERE ID='").append(id).append("' ");

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            pstmt.executeUpdate();
            log.debug("Equipment=[" + meterType + " " + id
                      + "] status is changed as 'Normal'");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }    }

    private ArrayList selectNormalEquipID(String meterType, String date, String hour) throws Exception
    {
        ArrayList list = null;

        String table = "";
        if(meterType.equals(AimirModel.MI_ENERGY_METER))
            table = "DAY_EM";
        else if(meterType.equals(AimirModel.MI_WATER_METER))
            table = "DAY_WM";
        else if(meterType.equals(AimirModel.MI_GAS_METER))
            table = "DAY_GM";
        else if(meterType.equals(AimirModel.MI_HEAT_METER))
            table = "DAY_HM";
        else if(meterType.equals(AimirModel.MI_VOLUMECORRECTOR))
            table = "DAY_VC";
        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n SELECT DISTINCT METER_ID ");
        sql.append("\n FROM ").append(table).append(" ");
        sql.append("\n WHERE YYYYMMDD='").append(date).append("' ");

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();
            list = new ArrayList();
            while(rs.next())
            {
                list.add(rs.getString("ID"));
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }

        return list;
    }

    private void updateAbnormalSensor(String meterType, String id) throws Exception
    {
        String zbStr = null;
        if (meterType.equals(AimirModel.MI_ENERGY_METER))
            zbStr = AimirModel.MI_ZRU;
        else if (meterType.equals(AimirModel.MI_WATER_METER)
                 || meterType.equals(AimirModel.MI_GAS_METER)
                 || meterType.equals(AimirModel.MI_VOLUMECORRECTOR))
            zbStr = AimirModel.MI_ZEUPLS;
        else if (meterType.equals(AimirModel.MI_HEAT_METER))
           zbStr = AimirModel.MI_IEIU;

        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n UPDATE MI_").append(zbStr).append(" SET COMMSTATE=0 ");
        sql.append("\n WHERE INSTANCENAME IN ");
        sql.append("\n       ( ");
        sql.append("\n           SELECT  SECONDINSTANCE ");
        sql.append("\n           FROM    MI_ATTACHEDSENSOR ");
        sql.append("\n           WHERE   FIRSTINSTANCE IN ");
        sql.append("\n                   ( ");
        sql.append("\n                       SELECT  INSTANCENAME ");
        sql.append("\n                       FROM    MI_").append(meterType).append(" ");
        sql.append("\n                       WHERE   ID='").append(id).append("' ");
        sql.append("\n                   ) ");
        sql.append("\n       ) ");

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            pstmt.executeUpdate();
            log.debug("Equipment=[" + meterType + " " + id + " " + zbStr
                      + " Sensor] status is changed as 'Abnormal'");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }        
    }

    private void updateAbnormalMeter(String meterType, String id) throws Exception
    {
        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n UPDATE MI_").append(meterType).append(" SET NETWORKSTATUS=0 ");
        sql.append("\n WHERE ID='").append(id).append("' ");

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            pstmt.executeUpdate();
            log.debug("Equipment=[" + meterType + " " + id
                      + "] status is changed as 'Abnormal'");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }
     }

    private ArrayList selectAbnormalEquipID(String meterType, String date, String hour) throws Exception
    {
        ArrayList list = null;

        String table = "";
        if(meterType.equals(AimirModel.MI_ENERGY_METER))
            table = "DAY_EM";
        else if(meterType.equals(AimirModel.MI_WATER_METER))
            table = "DAY_WM";
        else if(meterType.equals(AimirModel.MI_GAS_METER))
            table = "DAY_GM";
        else if(meterType.equals(AimirModel.MI_HEAT_METER))
            table = "DAY_HM";
        else if(meterType.equals(AimirModel.MI_VOLUMECORRECTOR))
            table = "DAY_VC";
        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n SELECT ID FROM MI_").append(meterType).append(" ");
        sql.append("\n WHERE ID NOT IN ");
        sql.append("\n ( ");
        sql.append("\n     SELECT DISTINCT METER_ID ");
        sql.append("\n     FROM ").append(table).append(" ");
        sql.append("\n     WHERE YYYYMMDD='").append(date).append("' ");
        //sql.append("\n       AND SUBSTR(HHMMSS,0,2) > '").append(hour).append("' ");
        sql.append("\n ) ");

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();
            list = new ArrayList();
            while(rs.next())
            {
                list.add(rs.getString("ID"));
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }

        return list;
    }

    private void updateAbnormalSensor(String meterType) throws Exception
    {
        String zbStr = null;
        if (meterType.equals(AimirModel.MI_ENERGY_METER))
            zbStr = AimirModel.MI_ZRU;
        else if (meterType.equals(AimirModel.MI_WATER_METER)
                 || meterType.equals(AimirModel.MI_GAS_METER)
                 || meterType.equals(AimirModel.MI_VOLUMECORRECTOR))
            zbStr = AimirModel.MI_ZEUPLS;
        else if (meterType.equals(AimirModel.MI_HEAT_METER))
           zbStr = AimirModel.MI_IEIU;

        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n UPDATE MI_").append(zbStr).append(" SET COMMSTATE=0 ");
        sql.append("\n WHERE INSTANCENAME IN ");
        sql.append("\n       ( ");
        sql.append("\n           SELECT  SECONDINSTANCE ");
        sql.append("\n           FROM    MI_ATTACHEDSENSOR ");
        sql.append("\n           WHERE   FIRSTINSTANCE IN ");
        sql.append("\n                   ( ");
        sql.append("\n                       SELECT  INSTANCENAME ");
        sql.append("\n                       FROM    MI_").append(meterType).append(" ");
        sql.append("\n                   ) ");
        sql.append("\n       ) ");

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            pstmt.executeUpdate();
            log.debug("EquipmentState: " + meterType + " " + zbStr
                     + " Sensor All Update => Abnormal");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }
    }

    private void updateAbnormalMeter(String meterType) throws Exception
    {
        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n UPDATE MI_").append(meterType).append(" SET NETWORKSTATUS=0 ");

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            pstmt.executeUpdate();
            log.debug("EquipmentState: " + meterType
                     + " All Update => Abnormal");

        }
        catch (Exception ex)
        {
            throw ex;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }
    }

    private int getCount(String table, String condition)
    {
        // resource declaration
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("\n SELECT COUNT(*) C ");
        sql.append("\n FROM ").append(table).append(" ");
        sql.append("\n WHERE ").append(condition);

        //log.debug("main sql: " + sql.toString());

        try
        {
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            if (rs.next())
                return rs.getInt("C");
            else
                return 0;
        }
        catch (Exception ex)
        {
            return 0;
        }
        finally
        {
            JDBCUtil.close(rs, pstmt, con);
        }
    }
    
        */
    public String getDescription()
    {
        return description;
    }

    public String[] getParamList()
    {
        return paramList;
    }

    public String[] getParamListDescription()
    {
        return paramListDescription;
    }

    public boolean[] getParamListRequired()
    {
        return paramListRequired;
    }

    public String[] getParamListDefault()
    {
        return paramListDefault;
    }
}