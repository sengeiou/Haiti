package com.aimir.schedule.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.dao.device.MeterDao;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Operator;
import com.aimir.schedule.command.CmdOperationUtil;

public class RemotePowerControlJob extends AimirJob
{
    private static Log log = LogFactory.getLog(RemotePowerControlJob.class);

    private static final String description = "scheduler.job.RemotePowerControlJob";
    private static final String[] paramList = {"targetType","targetId","operator","command[remotePowerOff,remotePowerOn,remoteGetStatus]"};
    private static final String[] paramListDescription = { "aimir.target.type",
                                                           "aimir.target.id",
                                                           "aimir.operator",
                                                           "aimir.remotePowerControl[remotePowerOn:aimir.remote.powerOn,remotePowerOff:aimir.remote.powerOff,remoteGetStatus:aimir.remote.getStatus]"};
    private static final boolean[] paramListRequired = {true,true,true,true};
    private static final String[] paramListDefault = {"","","","remoteGetStatus"};
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
        String jobName = context.getJobDetail().getDescription();
        log.info("Executing job: " + jobName + " executing at " + new Date());

        JobDataMap data = context.getMergedJobDataMap();

        String targetType = data.getString("targetType");
        String targetId = data.getString("targetId");
        String operator = data.getString("operator");
        String command = data.getString("command");
        int resultType = Integer.parseInt(data.getString("resultType"));
        String[] targetInst = targetId.split("[|]");

        String resultTarget = null;
        try
        {
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<targetInst.length;i++)
            {
                String temp = remoteControl(command,targetInst[i],operator);
                if(temp!=null && temp.length()>0)
                    sb.append(temp);
            }
            if(sb.length()<=0)
                sb.append("");
            //resultTarget = SchedulerResultMgmt.writeResult(context.getJobDetail()
            //           .getName(), context.getTrigger().getName(), sb
            //            .toString(), data);
            //SchedulerResultMgmt.logResult(context, resultType, resultTarget, data, 0,null);

        }catch (Exception e)
        {
            //SchedulerResultMgmt.logResult(context, resultType, resultTarget, data, 1,e.getMessage());
            log.error("Executing job: " + jobName + " Exception "
                      + e.getMessage(),e);
            throw new JobExecutionException(e.getMessage());
        }

        log.info("Executing job: " + jobName + " ending at " + new Date());

    }
    private String remoteControl(String cmd, String meterId, String operator)
    {
        String html =null;
        try
        {
        	
        	/*
            MOINSTANCE[] users = IUtil.getMIO().EnumerateInstances(AimirModel.MI_PERSON_USER,
                                                "id like 'mtr%' and userid ='"+operator+"'",null,0,true);
            UserData userData = new UserData();
            userData.setInstanceName(users[0].getName());
            userData.setId(users[0].getPropertyValueString("id"));
            userData.setUserId(users[0].getPropertyValueString("userId"));
            userData.setUserName(users[0].getPropertyValueString("name"));
            userData.setServiceId(CUtil.parseInt(users[0]
                    .getPropertyValueString("serviceType")));
                    
                    */
        	
        	Operator userData = null;
            EnergyMeter meter = null;
            if (meterId != null && meterId.length() > 0)
            {
                meter = (EnergyMeter)meterDao.get(meterId);
            }

            String result = null;
            if(cmd != null && cmd.equals("remotePowerOff")){
                result = remotePowerOff(meter,userData);
                log.debug("remotePowerOff:"+meter.getMdsId());
            }else if(cmd != null && cmd.equals("remotePowerOn")){
                result = remotePowerOn(meter,userData);
                log.debug("remotePowerOn:"+meter.getMdsId());
            }else if(cmd != null && cmd.equals("remotePowerActivate")){
                result = remotePowerActivate(meter,userData);
                log.debug("remoteGetStatus:"+meter.getMdsId());
            }else if(cmd != null && cmd.equals("remoteGetStatus")){
                result = remoteGetStatus(meter,userData);
                log.debug("remoteGetStatus:"+meter.getMdsId());
            }

            html = result;
        }
        catch (Exception e)
        {
            html =  "<html>";
            html += "<link href=/mtr/css/style.css rel=stylesheet type=text/css>";
            html += "<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100%>";
            html += "<tr>";
            html += "<td align='left'class='tdhead1' width='50%'>Fail</td>";
            html += "<td align='right'class='tdblue' width='50%'>"+e.getMessage()+"</td>";
            html += "</tr>";
            html += "</table>";
            html += "</html>";
        }

        return html;
    }

    private String remotePowerOff(EnergyMeter meter, Operator userData) throws Exception
    {

        //UoMgrDelegate mgr = new UoMgrDelegate();

        DeviceModel model =  meter.getModel();
        List result = null;
        if(model.getCode() == MeterModel.GE_SM110.getCode()) {
            //result = (List)mgr.cmdRelaySwitch(meter.getMdsId(), "3", userData);
        } else if(model.getCode() == MeterModel.AIDON_5530.getCode()) {
            //result = (List)mgr.cmdAidonMccb(meter.getMdsId(),"Disable Use - Disconnect",userData);
        } else if(model.getCode() == MeterModel.KAMSTRUP_382.getCode() || 
        			model.getCode() == MeterModel.KAMSTRUP_162.getCode()) {
            //result = (List)mgr.cmdKamstrupCID(meter.getMdsId(), new String[]{KamstrupCIDMeta.CID.SetCutOffState.getCommand(), KamstrupCIDMeta.CID.SetCutOffState.getArgs()[0][0]}, userData);  // Disconnect relays
        }

        return cmdOperationUtil.getRemoteControlHTML("remotePowerOff", meter);
    }
    private String remotePowerOn(EnergyMeter meter,Operator userData) throws Exception
    {
        //UoMgrDelegate mgr = new UoMgrDelegate();

        DeviceModel model =  meter.getModel();
        List result = null;
        if(model.getCode() == MeterModel.GE_SM110.getCode()) {
            //result = (List)mgr.cmdRelayActivate(meter.getMdsId(),"1",userData); //Activate On
            //result = (List)mgr.cmdRelaySwitch(meter.getMdsId(),"2",userData);   //Switch On
        } else if(model.getCode() == MeterModel.AIDON_5530.getCode()) {
            //result = (List)mgr.cmdAidonMccb(meter.getMdsId(),"Enable Use - Connect Now",userData);
        } else if(model.getCode() == MeterModel.KAMSTRUP_382.getCode() || 
        			model.getCode() == MeterModel.KAMSTRUP_162.getCode()) {
            //result = (List)mgr.cmdKamstrupCID(meter.getMdsId(), new String[]{KamstrupCIDMeta.CID.SetCutOffState.getCommand(), KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0]}, userData);  // Reconnect relays possible
            //result = (List)mgr.cmdKamstrupCID(meter.getMdsId(), new String[]{KamstrupCIDMeta.CID.SetCutOffState.getCommand(), KamstrupCIDMeta.CID.SetCutOffState.getArgs()[2][0]}, userData);  // Reconnect relays
        }
        return cmdOperationUtil.getRemoteControlHTML("remotePowerOn",  meter);
    }
    private String remotePowerActivate(EnergyMeter meter,Operator userData) throws Exception
    {
        //UoMgrDelegate mgr = new UoMgrDelegate();

        DeviceModel model =  meter.getModel();
        List result = null;
        if(model.getCode() == MeterModel.GE_SM110.getCode()) {
            //result = (List)mgr.cmdRelayActivate(meter.getMdsId(), "1", userData); // Activate On
        } else if(model.getCode() == MeterModel.AIDON_5530.getCode()) {
            //result = (List)mgr.cmdAidonMccb(meter.getMdsId(), Mccb.MSG_REQ[3], userData);  // Enable Use - Disconnected
        } else if(model.getCode() == MeterModel.KAMSTRUP_382.getCode() || 
        			model.getCode() == MeterModel.KAMSTRUP_162.getCode()) {
            //result = (List)mgr.cmdKamstrupCID(meter.getMdsId(), new String[]{KamstrupCIDMeta.CID.SetCutOffState.getCommand(), KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0]}, userData);
        }
        return cmdOperationUtil.getRemoteControlHTML("remotePowerActivate",  meter);
    }
    private String remoteGetStatus(EnergyMeter meter,Operator userData) throws Exception
    {
        //UoMgrDelegate mgr = new UoMgrDelegate();

        DeviceModel model =  meter.getModel();
        List result = null;
        if(model.getCode() == MeterModel.GE_SM110.getCode() || model.getCode() == MeterModel.GE_I210.getCode()) {
            //result = (List) mgr.getRelayStatus(meter.getMdsId(), userData);
        } else if(model.getCode() == MeterModel.AIDON_5530.getCode()) {
            //result = (List) mgr.cmdAidonMccb(meter.getMdsId(),
            //        "Get Phase Status", userData);
        } else if(model.getCode() == MeterModel.KAMSTRUP_382.getCode() ||
        			model.getCode() == MeterModel.KAMSTRUP_162.getCode()) {
            //result = (List) mgr.cmdKamstrupCID(meter.getMdsId(),
            //        new String[] { KamstrupCIDMeta.CID.GetCutOffState
            //                .getCommand() }, userData); // GetCutOffState
        }
        return cmdOperationUtil.getRemoteControlHTML("remoteGetStatus",  meter);
    }

    public String getDescription()
    {
        return description;
    }

    public String[] getParamList()
    {
        return paramList;
    }

    public String[] getParamListDefault()
    {
        return paramListDefault;
    }

    public String[] getParamListDescription()
    {
        return paramListDescription;
    }

    public boolean[] getParamListRequired()
    {
        return paramListRequired;
    }
}