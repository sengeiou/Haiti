package com.aimir.schedule.job;
//라이브러리 이용
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MCU {
    boolean isMCUFWUpgraded = false;
    boolean isMCUFWUploaded = false;
    boolean isCoordiFWUpgraded = false;
    boolean isCoordiFWUploaded = false;
    boolean isInit = false;
    String MCUID = "";
    String MCUIP = "";
    String MCUType = "";
    String MCUSWVer = "";
    String MCUHWVer = "";
    String coordiEUI="";
    String coordiHWVer = "";
    String coordiFWVer = "";
    String channel= "";
    String panID = "";
    String RFPower = "";
    String permitTime = "";
    String sensorLimit = "";
    String autoChannel = "";
    String resetKind = "";
    String totalCnt="";
    String abnCnt="";
    String joiningRatio="";

    String latestMCUVer="";
    String latestCoordiVer="";
    String diffName="";
    String diffSize="";
    String coordiName="";
    String coordiSize="";
    Hashtable diffTable = new Hashtable();
    Hashtable coordiTable = new Hashtable();
    Hashtable sensorTable = new Hashtable();
    String nFWRetry="";
    String nCoordiRetry="";
    String server="";
    String descr = "";
    String date="";

    public MCU(String latestMCUVer, String latestCoordiVer) {
        setLatestMCUVer(latestMCUVer);      
        setLatestCoordiVer(latestCoordiVer);                        
        
        //Test
        diffTable.put("NZC2-O312_1759M_TO_1763M.diff", "111924");
        diffTable.put("NZC2-O312_1763M_TO_1759M.diff", "111781");
        diffTable.put("NZC2-O312_1759M_TO_1595.diff", "111781");        
        coordiTable.put("NZM106_2.1_B6.ebl", "105600");
        coordiTable.put("NZM106_2.0_B14.ebl", "103616");
        
        //Sweden 1.6 -> 1.8
        diffTable.put("NZC2-I211_1401_TO_1491.diff", "178561");
        diffTable.put("NZC2-O312_1401_TO_1491.diff", "178586");
                
        //Sweden 1.6 -> 1.9
        diffTable.put("NZC2-I211_1401_TO_1595.diff", "246956");
        diffTable.put("NZC2-O312_1401_TO_1595.diff", "246887");
                
        //Sweden 1.8 -> 1.9
        diffTable.put("NZC2-I211_1491_TO_1595.diff", "235371");
        diffTable.put("NZC2-O312_1491_TO_1595.diff", "235348");                 
        
        
        //sweden -> 1820
        diffTable.put("NZC1_I_1401_TO_1820.diff", "416181");
        diffTable.put("NZC1_I_1491_TO_1820.diff", "442543");
        diffTable.put("NZC1_O_1401_TO_1820.diff", "416336");
        diffTable.put("NZC1_O_1491_TO_1820.diff", "442524");
        diffTable.put("NZC2_I_1491_TO_1820.diff", "443829");
        diffTable.put("NZC2_I_1595_TO_1820.diff", "433921");
        diffTable.put("NZC2_I_1703_TO_1820.diff", "229319");
        diffTable.put("NZC2_O_1491_TO_1820.diff", "443758");
        diffTable.put("NZC2_O_1595_TO_1820.diff", "433735");
        diffTable.put("NZC2_O_1703_TO_1820.diff", "229573");
        //Sweden NZM106
        coordiTable.put("NZM106_2.1_B5.ebl", "104832");
        coordiTable.put("NZM106_2.1_B8.ebl", "105600");
        //Sweden NZM108
        coordiTable.put("NZM108_2.1_B5.ebl", "104832");
        coordiTable.put("NZM108_2.1_B8.ebl", "105600");        
    }

    /**
     * @return the autoChannel
     */
    public String getAutoChannel() {
        return autoChannel;
    }

    /**
     * @param autoChannel
     *            the autoChannel to set
     */
    public void setAutoChannel(String autoChannel) {
        this.autoChannel = autoChannel;
    }

    /**
     * @return the resetKind
     */
    public String getResetKind() {
        return resetKind;
    }

    /**
     * @param resetKind
     *            the resetKind to set
     */
    public void setResetKind(String resetKind) {
        this.resetKind = resetKind;
    }

    

    /**
     * @return the mCUID
     */
    public String getMCUID() {
        return MCUID;
    }

    /**
     * @param mcuid
     *            the mCUID to set
     */
    public void setMCUID(String mcuid) {
        MCUID = mcuid;
    }

    /**
     * @return the mCUIP
     */
    public String getMCUIP() {
        return MCUIP;
    }

    /**
     * @param mcuip
     *            the mCUIP to set
     */
    public void setMCUIP(String mcuip) {
        MCUIP = mcuip;
    }

    /**
     * @return the mCUType
     */
    public String getMCUType() {
        return MCUType;
    }

    /**
     * @param type
     *            the mCUType to set
     */
    public void setMCUType(String type) {
        MCUType = type;
    }

    /**
     * @return the mCUSWVer
     */
    public String getMCUSWVer() {
        return MCUSWVer;
    }

    /**
     * @param ver
     *            the mCUSWVer to set
     */
    public void setMCUSWVer(String ver) {
        MCUSWVer = ver;
    }

    /**
     * @return the mCUHWVer
     */
    public String getMCUHWVer() {
        return MCUHWVer;
    }

    /**
     * @param ver
     *            the mCUHWVer to set
     */
    public void setMCUHWVer(String ver) {
        MCUHWVer = ver;
    }

    /**
     * @return the cordiHWVer
     */
    public String getCoordiHWVer() {
        return coordiHWVer;
    }

    /**
     * @param cordiHWVer
     *            the cordiHWVer to set
     */
    public void setCoordiHWVer(String cordiHWVer) {
        this.coordiHWVer = cordiHWVer;
    }

    /**
     * @return the cordiFWVer
     */
    public String getCoordiFWVer() {
        return coordiFWVer;
    }

    /**
     * @param cordiFWVer
     *            the cordiFWVer to set
     */
    public void setCoordiFWVer(String cordiFWVer) {
        this.coordiFWVer = cordiFWVer;
    }

    /**
     * @return the rFPower
     */
    public String getRFPower() {
        return RFPower;
    }

    /**
     * @param power
     *            the rFPower to set
     */
    public void setRFPower(String power) {
        RFPower = power;
    }

    /**
     * @return the sensorLimit
     */
    public String getSensorLimit() {
        return sensorLimit;
    }

    /**
     * @param sensorLimit
     *            the sensorLimit to set
     */
    public void setSensorLimit(String sensorLimit) {
        this.sensorLimit = sensorLimit;
    }

    public String toString() {
        StringBuffer info = new StringBuffer();

        info.append(" isMCUFWUpgraded : ").append(isMCUFWUpgraded).append("\n")
        .append(" isMCUFWUploaded : ").append(isMCUFWUploaded).append("\n")
        .append(" isCoordiFWUpgraded : ").append(isCoordiFWUpgraded).append("\n")
        .append(" isCoordiFWUploaded : ").append(isCoordiFWUploaded).append("\n")
        .append(" isInit : ").append(isInit).append("\n")
        .append(" MCUID : ").append(MCUID).append("\n")
        .append(" MCUIP : ").append(MCUIP).append("\n")
        .append(" MCUType : ").append(MCUType).append("\n")
        .append(" MCUSWVer : ").append(MCUSWVer).append("\n")
        .append(" MCUHWVer : ").append(MCUHWVer).append("\n")
        .append(" coordiEUI : ").append(coordiEUI).append("\n")
        .append(" coordiHWVer : ").append(coordiHWVer).append("\n")
        .append(" coordiFWVer : ").append(coordiFWVer).append("\n")
        .append(" channel : ").append(channel).append("\n")
        .append(" panID : ").append(panID).append("\n")
        .append(" RFPower : ").append(RFPower).append("\n")
        .append(" permitTime : ").append(permitTime).append("\n")
        .append(" totalCnt : ").append(totalCnt).append("\n")
        .append(" abnCnt : ").append(abnCnt).append("\n")
        .append(" joiningRatio : ").append(joiningRatio).append("\n")
        .append(" autoChannel : ").append(autoChannel).append("\n")     
        .append(" resetKind : ").append(resetKind).append("\n")
        .append(" sensorLimit : ").append(sensorLimit).append("\n")
        .append(" server : ").append(server).append("\n")
        .append(" diffName : ").append(diffName).append("\n")
        .append(" diffSize : ").append(diffSize).append("\n")
        .append(" coordiName : ").append(coordiName).append("\n")
        .append(" coordiSize : ").append(coordiSize).append("\n")
        .append(" latestMCUVer : ").append(latestMCUVer).append("\n")
        .append(" latestCoordiVer : ").append(latestCoordiVer).append("\n")
        .append(" descr : ").append(descr).append("\n")
        .append(" date : ").append(date).append("\n");

        return info.toString();
    }

    /**
     * @return the permitTime
     */
    public String getPermitTime() {
        return permitTime;
    }

    /**
     * @param permitTime
     *            the permitTime to set
     */
    public void setPermitTime(String permitTime) {
        this.permitTime = permitTime;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr
     *            the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * @return the isInit
     */
    public boolean getIsInit() {
        return isInit;
    }

    /**
     * @param isInit the isInit to set
     */
    public void setIsInit(boolean isInit) {
        this.isInit = isInit;
    }

    /**
     * @return the diffName
     */
    public String getDiffName() {
        return diffName;
    }

    /**
     * @param diffName the diffName to set
     */
    public void setDiffName(String diffName) {
        this.diffName = diffName;
    }

    /**
     * @return the diffSize
     */
    public String getDiffSize() {
        return diffSize;
    }

    /**
     * @param diffSize the diffSize to set
     */
    public void setDiffSize(String diffSize) {
        this.diffSize = diffSize;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the diffTable
     */
    public Hashtable getDiffTable() {
        return diffTable;
    }

    /**
     * @param diffTable the diffTable to set
     */
    public void setDiffTable(Hashtable diffTable) {
        this.diffTable = diffTable;
    }

    /**
     * @return the coordiEUI
     */
    public String getCoordiEUI() {
        return coordiEUI;
    }

    /**
     * @param coordiEUI the coordiEUI to set
     */
    public void setCoordiEUI(String coordiEUI) {
        this.coordiEUI = coordiEUI;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the sensorTable
     */
    public Hashtable getSensorTable() {
        return sensorTable;
    }

    /**
     * @param sensorTable the sensorTable to set
     */
    public void setSensorTable(Hashtable sensorTable) {
        this.sensorTable = sensorTable;
    }

    /**
     * @return the totalCnt
     */
    public String getTotalCnt() {
        return totalCnt;
    }

    /**
     * @param totalCnt the totalCnt to set
     */
    public void setTotalCnt(String totalCnt) {
        this.totalCnt = totalCnt;
    }

    /**
     * @return the abnCnt
     */
    public String getAbnCnt() {
        return abnCnt;
    }

    /**
     * @param abnCnt the abnCnt to set
     */
    public void setAbnCnt(String abnCnt) {
        this.abnCnt = abnCnt;
    }

    /**
     * @return the isMCUFWUpgraded
     */
    public boolean isMCUFWUpgraded()
    {
        return isMCUFWUpgraded;
    }

    /**
     * @param isMCUFWUpgraded the isMCUFWUpgraded to set
     */
    public void setMCUFWUpgraded(boolean isMCUFWUpgraded)
    {
        this.isMCUFWUpgraded = isMCUFWUpgraded;
    }

    /**
     * @return the isMCUFWUploaded
     */
    public boolean isMCUFWUploaded()
    {
        return isMCUFWUploaded;
    }

    /**
     * @param isMCUFWUploaded the isMCUFWUploaded to set
     */
    public void setMCUFWUploaded(boolean isMCUFWUploaded)
    {
        this.isMCUFWUploaded = isMCUFWUploaded;
    }

    /**
     * @return the isCoordiFWUpgraded
     */
    public boolean isCoordiFWUpgraded()
    {
        return isCoordiFWUpgraded;
    }

    /**
     * @param isCoordiFWUpgraded the isCoordiFWUpgraded to set
     */
    public void setCoordiFWUpgraded(boolean isCoordiFWUpgraded)
    {
        this.isCoordiFWUpgraded = isCoordiFWUpgraded;
    }

    /**
     * @return the isCoordiFWUploaded
     */
    public boolean isCoordiFWUploaded()
    {
        return isCoordiFWUploaded;
    }

    /**
     * @param isCoordiFWUploaded the isCoordiFWUploaded to set
     */
    public void setCoordiFWUploaded(boolean isCoordiFWUploaded)
    {
        this.isCoordiFWUploaded = isCoordiFWUploaded;
    }

    /**
     * @return the latestMCUVer
     */
    public String getLatestMCUVer()
    {
        return latestMCUVer;
    }

    /**
     * @param latestMCUVer the latestMCUVer to set
     */
    public void setLatestMCUVer(String latestMCUVer)
    {
        this.latestMCUVer = latestMCUVer;
    }

    /**
     * @return the latestCoordiVer
     */
    public String getLatestCoordiVer()
    {
        return latestCoordiVer;
    }

    /**
     * @param latestCoordiVer the latestCoordiVer to set
     */
    public void setLatestCoordiVer(String latestCoordiVer)
    {
        this.latestCoordiVer = latestCoordiVer;
    }

    /**
     * @return the channel
     */
    public String getChannel()
    {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    /**
     * @return the panID
     */
    public String getPanID()
    {
        return panID;
    }

    /**
     * @param panID the panID to set
     */
    public void setPanID(String panID)
    {
        this.panID = panID;
    }

    /**
     * @return the coordiName
     */
    public String getCoordiName()
    {
        return coordiName;
    }

    /**
     * @param coordiName the coordiName to set
     */
    public void setCoordiName(String coordiName)
    {
        this.coordiName = coordiName;
    }

    /**
     * @return the coordiSize
     */
    public String getCoordiSize()
    {
        return coordiSize;
    }

    /**
     * @param coordiSize the coordiSize to set
     */
    public void setCoordiSize(String coordiSize)
    {
        this.coordiSize = coordiSize;
    }

    /**
     * @return the coordiTable
     */
    public Hashtable getCoordiTable()
    {
        return coordiTable;
    }

    /**
     * @param coordiTable the coordiTable to set
     */
    public void setCoordiTable(Hashtable coordiTable)
    {
        this.coordiTable = coordiTable;
    }

    /**
     * @return the meteringRatio
     */
    public String getJoiningRatio()
    {
        if(totalCnt.length()>0 && abnCnt.length()>0 && !totalCnt.equals("0")){
            joiningRatio = ((Integer.parseInt(totalCnt)-Integer.parseInt(abnCnt))/Integer.parseInt(totalCnt))*100+"%";
        }
        return joiningRatio;
    }

    /**
     * @param meteringRatio the meteringRatio to set
     */
    public void setjoiningRatio(String joiningRatio)
    {
        this.joiningRatio = joiningRatio;
    }

    /**
     * @return the nFWRetry
     */
    public String getNFWRetry()
    {
        return nFWRetry;
    }

    /**
     * @param retry the nFWRetry to set
     */
    public void setNFWRetry(String retry)
    {
        nFWRetry = retry;
    }

    /**
     * @return the nCoordiRetry
     */
    public String getNCoordiRetry()
    {
        return nCoordiRetry;
    }

    /**
     * @param coordiRetry the nCoordiRetry to set
     */
    public void setNCoordiRetry(String coordiRetry)
    {
        nCoordiRetry = coordiRetry;
    }
}

class Stream {
    private static Log _log = LogFactory.getLog(Stream.class);
    MgrFile mgrFile = new MgrFile();
    BufferedReader src = null;
    BufferedWriter dist = null;
    MCU targetMCU = null;
    String log;
    String host;
    String logTarget = "file";
    int cntTimeOut=0;
    boolean isTimeOut=false;
    // Constructor 입출력 스트림을 받아서 처리한다.
    public Stream(BufferedReader src, BufferedWriter dist, MgrFile mgrFile, String host, String log, MCU targetMCU) {
        this.src = src;
        this.dist = dist;
        this.mgrFile=mgrFile;
        this.log=log;
        this.host=host;
        this.targetMCU=targetMCU;
    }

    /**
     * @param packet
     */
    synchronized void sendPacket(String packet) throws Exception{
        // 스트링 패킷을 바이너리 데이터로 변형후 접속 서버로 전송해주는 메소드
        dist.write(packet+"\r\n");
        dist.flush();
    }
    public String getPacket() throws Exception{
        String str="temp";
        int cnt=0;
        while(str.length()>0){
            cnt++;
            if(cnt>500){
                break;
            }
            str=getLine();            
        }
        return "temp";
    }
    /**
     * @param packet
     */
    synchronized void sendPacketNoReturn(String packet) throws Exception{
        dist.write(packet);
        dist.flush();
    }

    public void initMCU(){
        targetMCU.setIsInit(false);
        
        targetMCU.setMCUID("");
        targetMCU.setMCUIP("");
        targetMCU.setMCUType("");
        targetMCU.setMCUSWVer("");
        targetMCU.setMCUHWVer("");
        targetMCU.setCoordiEUI("");
        targetMCU.setCoordiHWVer("");
        targetMCU.setCoordiFWVer("");
        targetMCU.setRFPower("");
        targetMCU.setPermitTime("");
        targetMCU.setSensorLimit("");
        targetMCU.setAutoChannel("");
        targetMCU.setResetKind("");
        targetMCU.setServer("");        
        targetMCU.setDate(getTime());                   
    }
    
    public String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        String date = formatter.format(currentTime);
        return date;
    }

    public String getLine() throws Exception{       
        String str="";
        try {           
            str=src.readLine();         
            if(logTarget.equals("screen")){
                _log.debug(str);
            }else{
                mgrFile.fileAppend(log, str+"\r\n");
            }
            if(str.contains("Invalid account or password.")){                
                throw new Exception("CLI Login Error");
            }
            setTimeOut(false);
        } catch (IOException e) {
            //e.printStackTrace();
            if(isTimeOut()){
                cntTimeOut++;
            }else{
                cntTimeOut=1;
            }           
            if(cntTimeOut>10){
                throw new Exception("TimeOut Error");
            }
            setTimeOut(true);
            
            if(targetMCU.getMCUIP().length()>0){
                _log.debug("[Log] TimeOut["+cntTimeOut+"] Get Line: "+targetMCU.getMCUIP());
                mgrFile.fileAppend(log,"[Log] TimeOut["+cntTimeOut+"] Get Line: "+targetMCU.getMCUIP()+"\r\n");
            }else{
                _log.debug("[Log] TimeOut Get["+cntTimeOut+"] Line (MCU IP Is Null): "+host);
                mgrFile.fileAppend(log,"[Log] TimeOut["+cntTimeOut+"] Get Line (MCU IP Is Null): "+host+"\r\n");                
            }
        }
        return str;
    }
    public void getLine(int cnt) throws Exception{
        String str="";
        for(int i=1;i<=cnt;i++){
            try{
                str=src.readLine();
                if(logTarget.equals("screen")){
                    _log.debug("["+i+"]"+str);
                }else{
                    mgrFile.fileAppend(log, str+"\r\n");
                }
                if(str.contains("Invalid account or password.")){                
                    throw new Exception("CLI Login Error");
                }
            } catch (IOException e) {
                //e.printStackTrace();
                if(isTimeOut()){
                    cntTimeOut++;
                }else{
                    cntTimeOut=1;
                }           
                if(cntTimeOut>10){
                    throw new Exception("TimeOut Error");
                }
                setTimeOut(true);
                
                if(targetMCU.getMCUIP().length()>0){
                    _log.debug("[Log] TimeOut["+cntTimeOut+"] Get Lines: "+targetMCU.getMCUIP());
                    mgrFile.fileAppend(log,"[Log] TimeOut["+cntTimeOut+"] Get Lines: "+targetMCU.getMCUIP()+"\r\n");
                }else{
                    _log.debug("[Log] TimeOut["+cntTimeOut+"] Get Lines (MCU IP Is Null): "+host);
                    mgrFile.fileAppend(log,"[Log] TimeOut["+cntTimeOut+"] Get Lines (MCU IP Is Null): "+host+"\r\n");
                }
            }
        }
    }

    public void deleteSensor() throws Exception{
        Hashtable sensorList=getSensorList();
        Enumeration keys = sensorList.keys();
        String key="";
        _log.debug("[Log] Host["+targetMCU.getMCUIP()+"] totalCnt: "+sensorList.get("totalCnt")+" abnCnt: "+sensorList.get("abnCnt"));
        //Every Sensor is Normal
        if(Integer.parseInt(sensorList.get("totalCnt").toString())>0&&Integer.parseInt(sensorList.get("abnCnt").toString())==0){
            _log.debug("[Log] MCU IP["+targetMCU.getMCUIP()+"] Every Sensor Is Normal");
        }
        //Every Sensor is Abnormal                      
        else if(sensorList.get("totalCnt").equals(sensorList.get("abnCnt"))&&Integer.parseInt(sensorList.get("abnCnt").toString())>0){
            deleteSensorAll();
            getSensorList();
        }
        //Some Sensor is Abnormal                   
        else if(!sensorList.get("totalCnt").equals(sensorList.get("abnCnt"))&&Integer.parseInt(sensorList.get("abnCnt").toString())>0){
            while(keys.hasMoreElements()) {
               key = (String)keys.nextElement();
               if(sensorList.get(key).equals("CONN_ERR")||sensorList.get(key).equals("METER_ERR")||sensorList.get(key).equals("UNKNOWN")){                             
                   deleteSensorId(key,0);                        
               }
            }
            getSensorList();
        }
        //No Sensor List
        else{
            _log.debug("[Log] MCU IP["+targetMCU.getMCUIP()+"] No Sensor List");
        }       
        //Set Permit Time
        if(targetMCU.getMCUID().equals("0")&&!targetMCU.getPermitTime().equals("0")){
            sendPacket("set coordinator permit 0");
            getPacket();
            sendPacketNoReturn("y");
            getPacket();
        }else if(!targetMCU.getMCUID().equals("0")&&!targetMCU.getPermitTime().equals("255")){
            sendPacket("set coordinator permit 255");
            getPacket();
            sendPacketNoReturn("y");
            getPacket();
        }
    }
    
    public void deleteSensorId(String id,int cnt) throws Exception{
        String str="";
        sendPacket("delete sensor "+id);
        getLine();
        getLine();
        sendPacketNoReturn("y");
        for(int i=0;i<4;i++){
            str=getLine();
            if(str.contains("Timeout") && cnt<5){
                cnt++;
                deleteSensorId(id, cnt);
            }
        }
    }
    
    public void deleteSensorAll() throws Exception{
        sendPacket("delete sensor all");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
    }
    
    public Hashtable getSensorList() throws Exception{
        sendPacket("show sensor list");
        boolean once=false;
        int cnt=0;
        int index=0;
        int totalCnt=0;
        int abnCnt=0;
        int checkOver=100;
        if(Integer.parseInt(targetMCU.getMCUSWVer())>=1820){
            checkOver=50;
        }
        String str="";
        String EUIID="";
        String state="";        
        while(cnt<3 && index<50){
            str=getLine();
            if(str.contains("============")){
                cnt++;
                //_log.debug("[Log] cnt++["+cnt+"]");
            }
            else if(totalCnt==checkOver && once==false){      
                //str=getLine();
                sendPacketNoReturn("a");
                sendPacket("");
                once=true;
                _log.debug("[Log] Host["+host+"] Sensor List Over 100");
                if(!targetMCU.getDescr().contains("Sensor List Over 100!!")){
                    targetMCU.setDescr(targetMCU.getDescr()+" Sensor List Over 100!!, ");
                }
            }
            else if(cnt==2){
                totalCnt++;
                EUIID=str.substring(5, 21);
                state=str.substring(str.lastIndexOf(" ")+1, str.length());
                if(state.equals("CONN_ERR")||state.equals("METER_ERR")||state.equals("UNKNOWN")){
                    abnCnt++;
                }
                targetMCU.sensorTable.put(EUIID, state);                    
            }else if(str.contains("ERROR(100): No Entry")){
                break;
            }else{
                index++;
                //_log.debug("[Log] what happen! [IP: "+targetMCU.getMCUIP()+" index: "+index+"]");
            }           
        }
        targetMCU.sensorTable.put("totalCnt", totalCnt);
        targetMCU.sensorTable.put("abnCnt", abnCnt);
        targetMCU.setTotalCnt(String.valueOf(totalCnt));
        targetMCU.setAbnCnt(String.valueOf(abnCnt));        
        if(totalCnt<100){
            str=getLine();          
        }else{
            getPacket();
        }
        return targetMCU.sensorTable;
    }
    public void login() throws Exception{
        getLine(3);
        sendPacket("aimir");
        getLine(2);
        sendPacket("aimir");
        getLine(6);        
    }
    public void showSystem() throws Exception{
        _log.debug("[Log] Host["+host+"] Show System");
        sendPacket("show system");
        String str=getPacket();
        int cnt=0;
        while(str.length()>0){
            cnt++;
            if(cnt>100){
                break;
            }
            str=getLine();
            
            if (targetMCU.getMCUID() == null || targetMCU.getMCUID().length() == 0) {
                targetMCU.setMCUID(getMCUID(str));
            }
            if (targetMCU.getMCUIP() == null || targetMCU.getMCUIP().length() == 0) {
                targetMCU.setMCUIP(getMCUIP(str));
            }
            if (targetMCU.getMCUType() == null || targetMCU.getMCUType().length() == 0) {
                targetMCU.setMCUType(getMCUType(str));
            }
            if (targetMCU.getMCUSWVer() == null || targetMCU.getMCUSWVer().length() == 0) {
                targetMCU.setMCUSWVer(getMCUSWVer(str));
            }
            if (targetMCU.getMCUHWVer() == null || targetMCU.getMCUHWVer().length() == 0) {
                targetMCU.setMCUHWVer(getMCUHWVer(str));
            }
            if (targetMCU.getServer() == null || targetMCU.getServer().length() == 0) {
                targetMCU.setServer(getServer(str));
            }
        }   
    }
    
    public void showCoordinator(int cnt) throws Exception{
        _log.debug("[Log] Host["+host+"] Show Coordinator["+cnt+"]");       
        cnt++;      
        sendPacket("show coordinator");
        String str=getPacket();        
        while(str.length()>0){
            str=getLine();
            if(str.contains("ERROR")){                
                if(cnt>5){                    
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] Show Coordinator["+cnt+"]\r\n");
                    if(!targetMCU.getDescr().contains("Show Coordinator Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" Show Coordinator Fail!!, ");
                    }
                }else{
                    str=getPacket();
                    showCoordinator(cnt);                    
                }
                break;
            }
            
            if ((targetMCU.getCoordiEUI() == null || targetMCU.getCoordiEUI().length() == 0) && !str.contains("TYPE")) {
                targetMCU.setCoordiEUI(getCoordiEUI(str));
            }
            if (targetMCU.getCoordiHWVer() == null || targetMCU.getCoordiHWVer().length() == 0) {
                targetMCU.setCoordiHWVer(getCoordiHWVer(str));
            }
            if (targetMCU.getCoordiFWVer() == null || targetMCU.getCoordiFWVer().length() == 0) {
                targetMCU.setCoordiFWVer(getCoordiFWVer(str));
            }
            if (targetMCU.getRFPower() == null || targetMCU.getRFPower().length() == 0) {
                targetMCU.setRFPower(getRFPower(str));
            }
            if (targetMCU.getPermitTime() == null || targetMCU.getPermitTime().length() == 0) {
                targetMCU.setPermitTime(getPermitTime(str));
            }
            if (targetMCU.getChannel() == null || targetMCU.getChannel().length() == 0) {
                targetMCU.setChannel(getChannel(str));
            }
            if (targetMCU.getPanID() == null || targetMCU.getPanID().length() == 0) {
                targetMCU.setPanID(getPanID(str));
            }
            if (targetMCU.getAutoChannel() == null || targetMCU.getAutoChannel().length() == 0) {
                targetMCU.setAutoChannel(getAutoChannel(str));
            }
            if (targetMCU.getResetKind() == null || targetMCU.getResetKind().length() == 0) {
                targetMCU.setResetKind(getResetKind(str));
            }
        }   
    }
    
    public void showOption() throws Exception{
        _log.debug("[Log] Host["+host+"] Show Option");
        sendPacket("show option");
        String str=getPacket();
        int cnt=0;
        while(str.length()>0){
            if(cnt>500){
                break;
            }
            str=getLine();            
            if (targetMCU.getSensorLimit() == null || targetMCU.getSensorLimit().length() == 0) {
                targetMCU.setSensorLimit(getSensorLimit(str));
            }
        }
        getPacket();
        getPacket(); 
    }
    
    public void resetSystem() throws Exception{
        _log.debug("[Log] Host["+host+"] Reset System");
        sendPacket("reset system");
        getPacket();
        sendPacketNoReturn("y");
    }
    public void resetCoordi(int cnt) throws Exception{
        sendPacket("reset coordinator");
        getPacket();
        sendPacketNoReturn("y");
        String str="temp";
        while(str.length()>0){
            str=getLine();            
            if(str.contains("ERROR")){
                cnt++;                
                getPacket();
                if(cnt>5){
                    _log.debug("\r\n[Error] Host["+targetMCU.getMCUIP()+"] ReSet Coordinator Fail!!");
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] ReSet Coordinator Fail!!\r\n");
                    if(!targetMCU.getDescr().contains("ReSet Coordinator Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" ReSet Coordinator Fail!!, ");
                    }
                }else{
                    Thread.sleep(60000);
                    resetCoordi(cnt);                    
                }
                break;
            }
        }
    }
    
    public void setCoordiPower(String strPower,int cnt) throws Exception{
        sendPacket("set coordinator power "+strPower);
        getPacket();
        sendPacketNoReturn("y");
        String str="temp";
        while(str.length()>0){
            str=getLine();            
            if(str.contains("ERROR")){
                cnt++;                
                getPacket();
                if(cnt>5){
                    _log.debug("\r\n[Error] Host["+targetMCU.getMCUIP()+"] Set CoordiPower["+strPower+"]");
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] Set CoordiPower["+strPower+"]\r\n");
                    if(!targetMCU.getDescr().contains("Set CoordiPower Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" Set CoordiPower Fail!!, ");
                    }
                }else{
                    Thread.sleep(60000);
                    setCoordiPower(strPower,cnt);                    
                }
                break;
            }   
        }
    }

    public void setPermitTime(String strPermit,int cnt) throws Exception{
        sendPacket("set coordinator permit "+strPermit);
        getPacket();
        sendPacketNoReturn("y");
        String str="temp";
        while(str.length()>0){
            str=getLine();            
            if(str.contains("ERROR")){
                cnt++;                
                getPacket();
                if(cnt>5){
                    _log.debug("\r\n[Error] Host["+targetMCU.getMCUIP()+"] Set Permit Time["+strPermit+"]");
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] Set Permit Time["+strPermit+"]\r\n");
                    if(!targetMCU.getDescr().contains("Set Permit Time Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" Set Permit Time Fail!!, ");
                    }
                }else{
                    Thread.sleep(60000);
                    setPermitTime(strPermit,cnt);                    
                }
                break;
            }   
        }
    }

    public void setAutoSetting(String strSetting,int cnt) throws Exception{
        sendPacket("set coordinator autosetting "+strSetting);
        getPacket();
        sendPacketNoReturn("y");
        String str="temp";
        while(str.length()>0){
            str=getLine();            
            if(str.contains("ERROR")){
                cnt++;                
                getPacket();
                if(cnt>5){
                    _log.debug("\r\n[Error] Host["+targetMCU.getMCUIP()+"] Set Auto Setting["+strSetting+"]");
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] Set Auto Setting["+strSetting+"]\r\n");
                    if(!targetMCU.getDescr().contains("Set Auto Setting Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" Set Auto Setting Fail!!, ");
                    }
                }else{
                    Thread.sleep(60000);
                    setAutoSetting(strSetting,cnt);                    
                }
                break;
            }
        }
    }

    public void setChannel(String strChannel,int cnt) throws Exception{
        sendPacket("set coordinator channel "+strChannel);
        getPacket();
        sendPacketNoReturn("y");
        String str="temp";
        while(str.length()>0){
            str=getLine();            
            if(str.contains("ERROR")){
                cnt++;                
                getPacket();
                if(cnt>5){
                    _log.debug("\r\n[Error] Host["+targetMCU.getMCUIP()+"] Set Channel["+strChannel+"]");
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] Set Channel["+strChannel+"]\r\n");
                    if(!targetMCU.getDescr().contains("Set Channel Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" Set Channel Fail!!, ");
                    }
                }else{
                    Thread.sleep(60000);
                    setChannel(strChannel,cnt);                    
                }
                break;
            }
        }
    }

    public void setPanID(String strPanID,int cnt) throws Exception{
        sendPacket("set coordinator panid "+strPanID);
        getPacket();
        sendPacketNoReturn("y");
        String str="temp";
        while(str.length()>0){
            str=getLine();            
            if(str.contains("ERROR")){
                cnt++;                
                getPacket();
                if(cnt>5){
                    _log.debug("\r\n[Error] Host["+targetMCU.getMCUIP()+"] Set PanID["+strPanID+"]");
                    mgrFile.fileAppend(log,"[Error] Host["+targetMCU.getMCUIP()+"] Set PanID["+strPanID+"]\r\n");
                    if(!targetMCU.getDescr().contains("Set PanID Fail!!")){
                        targetMCU.setDescr(targetMCU.getDescr()+" Set PanID Fail!!, ");
                    }
                }else{
                    Thread.sleep(60000);
                    setPanID(strPanID,cnt);                    
                }
                break;
            }
        }
    }
    
    public MCU getMCUInfo() throws Exception{
        _log.debug("[Log] Host["+host+"] getMCUInfo");
        initMCU();                  
        if(!targetMCU.getIsInit()){
            showSystem();                       
            showCoordinator(0);
            showOption();            
            
            // MCU 정보를 받아왔는지 체크해서 루프를 빠져나감
            if (targetMCU.getAutoChannel().length() > 0
                    & targetMCU.getCoordiEUI().length() > 0
                    & targetMCU.getCoordiFWVer().length() > 0
                    & targetMCU.getCoordiHWVer().length() > 0
                    & targetMCU.getMCUHWVer().length() > 0
                    & targetMCU.getMCUID().length() > 0
                    & targetMCU.getMCUIP().length() > 0
                    & targetMCU.getMCUSWVer().length() > 0
                    & targetMCU.getMCUType().length() > 0
                    & targetMCU.getPermitTime().length() > 0
                    & targetMCU.getResetKind().length() > 0
                    & targetMCU.getRFPower().length() > 0
                    & targetMCU.getSensorLimit().length() > 0) {
                targetMCU.setIsInit(true);                          
            }                               
        }
        
        //Set Diff File Name and Size
        if(!targetMCU.getMCUSWVer().equals(targetMCU.getLatestMCUVer())){           
            if(targetMCU.getMCUType().equals("INDOOR")){
                if(targetMCU.getMCUHWVer().equals("1.2")){
                    targetMCU.setDiffName("NZC1_I_"+targetMCU.getMCUSWVer()+"_TO_"+targetMCU.getLatestMCUVer()+".diff");
                }else{
                    targetMCU.setDiffName("NZC2_I_"+targetMCU.getMCUSWVer()+"_TO_"+targetMCU.getLatestMCUVer()+".diff");
                }
            }else if(targetMCU.getMCUType().equals("OUTDOOR")){
                if(targetMCU.getMCUHWVer().equals("1.2")){
                    targetMCU.setDiffName("NZC1_O_"+targetMCU.getMCUSWVer()+"_TO_"+targetMCU.getLatestMCUVer()+".diff");
                }else{
                    targetMCU.setDiffName("NZC2_O_"+targetMCU.getMCUSWVer()+"_TO_"+targetMCU.getLatestMCUVer()+".diff");
                }           
            }       
            targetMCU.setDiffSize((String)(targetMCU.getDiffTable().get(targetMCU.getDiffName())));
        }
        
        //Set Coordi File Name and Size
        if(!targetMCU.getCoordiFWVer().equals(targetMCU.getLatestCoordiVer())){           
            if(targetMCU.getCoordiHWVer().equals("1.6")){
                targetMCU.setCoordiName("NZM106_"+targetMCU.getLatestCoordiVer().replaceAll("\\(", "_").replaceAll("\\)", "")+".ebl");         
            }else if(targetMCU.getCoordiHWVer().equals("1.8")){
                targetMCU.setCoordiName("NZM108_"+targetMCU.getLatestCoordiVer().replaceAll("\\(", "_").replaceAll("\\)", "")+".ebl");         
            }       
            targetMCU.setCoordiSize((String)(targetMCU.getCoordiTable().get(targetMCU.getCoordiName())));
        }
        
        //MCU F/W Upgraded 여부 설정
        if(targetMCU.getMCUSWVer().equals(targetMCU.getLatestMCUVer())){
            targetMCU.setMCUFWUpgraded(true);
            if(!targetMCU.getDescr().contains("Already Upgraded MCU")){
                targetMCU.setDescr(targetMCU.getDescr()+" Already Upgraded MCU, ");
            }
        }else{
            targetMCU.setMCUFWUpgraded(false);
        }
        
        //Coordi Upgraded 여부 설정
        if(targetMCU.getCoordiFWVer().equals(targetMCU.getLatestCoordiVer())){
            targetMCU.setCoordiFWUpgraded(true);
            if(!targetMCU.getDescr().contains("Already Upgraded Coordi")){
                targetMCU.setDescr(targetMCU.getDescr()+" Already Upgraded Coordi, ");
            }
        }else{
            targetMCU.setCoordiFWUpgraded(false);
        }            
        
        mgrFile.fileAppend(log,"\r\n[Log] Previous MCU Info\r\n");
        mgrFile.fileAppend(log,targetMCU.toString());
        return targetMCU;
    }
    
    public void setMCUInfo() throws Exception{
        _log.debug("[Log] Host["+host+"] MCU Setting");
        targetMCU.setDate(getTime());
        //Reset Coordi
        if(targetMCU.getResetKind().equals("RESET_UNKNOWN") || targetMCU.getResetKind().equals("RESET_ASSERT")){
            resetCoordi(0);         
        }
        
        //RF Power Setting
        if(targetMCU.getCoordiHWVer().equals("1.8") & !targetMCU.getRFPower().equals("-12")){
            setCoordiPower("-12",0);            
            setCoordiPower("-12",0);            
        }else if(targetMCU.getCoordiHWVer().equals("1.6") & !targetMCU.getRFPower().equals("-1")){
            setCoordiPower("-1",0);            
            setCoordiPower("-1",0);
        }
        
        //Permit Time
        if(targetMCU.getMCUID().equals("0")&&!targetMCU.getPermitTime().equals("0")){
            setPermitTime("0", 0);                      
        }else if(!targetMCU.getMCUID().equals("0")&&!targetMCU.getPermitTime().equals("255")){
            setPermitTime("255", 0);            
        }
                
        
        //Auto Channel
        if(!targetMCU.getMCUID().equals("0")&&targetMCU.getAutoChannel().equals("Enable")){
            setAutoSetting("0", 0);            
        }

        //Sensor Limit
        if(targetMCU.getSensorLimit().equals("Unlimited(-1)")){
            if(targetMCU.getMCUType().equals("INDOOR")){
                sendPacket("set option sensorlimit 40");
                getPacket();
                sendPacketNoReturn("y");
                getPacket();
            }else{
                sendPacket("set option sensorlimit 100");
                getPacket();
                sendPacketNoReturn("y");
                getPacket();
            }
        }       
        
        //DefaultSetting
        sendPacket("set server port 8000");
        getPacket();
        getPacket();
        sendPacket("set server alarmport 8001");
        getPacket();
        getPacket();
        sendPacket("set option autoreset 1 6 0");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("set option meteringschedule 7FFFFFFF 800100 5 3 30");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("set option recoveryschedule 7fffffff ffffff 36 3 9");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();

        sendPacket("set option autoupload hourly ffffff 46 10");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();

        sendPacket("set option metersecurity H \"72 07 27 50 12 46 12 24 58 54\"");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("disable meterping");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("enable sensortimesync");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("set option flashcheck 90");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("set option memorycheck 90");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("set option eventdelay 120");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
        sendPacket("set option meteringthread 2");
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
    }
    
    public void setCoordi() throws Exception{
        setAutoSetting("0", 0);
        Thread.sleep(10000);
        setChannel(targetMCU.getChannel(), 0);
        Thread.sleep(10000);
        setPanID(targetMCU.getPanID(), 0);
        Thread.sleep(10000);
        setPermitTime(targetMCU.getPermitTime(), 0);
    }
    public void cmdUploadFile(String fileName) throws Exception{
        _log.debug("[Log] Host["+host+"] upload file["+fileName+"]");
        sendPacket("shell \"./ftp.script -s "+targetMCU.getServer()+" -f "+fileName+" &\"");
        getPacket();
    }
    public boolean uploadFile(String fileName)  throws Exception{
        boolean diffFileExist=false;
        targetMCU.setDate(getTime());
        //DiffTable에 업로드할 파일명이 존재할 때만 Upload 실행
        if(targetMCU.getDiffTable().containsKey(fileName)){         
            sendPacket("shell \"./ftp.script -s "+targetMCU.getServer()+" -f "+fileName+" &\"");
            getPacket();
    
            targetMCU.setMCUFWUploaded(false);
            int cnt=0;
            while(!(targetMCU.isMCUFWUploaded()) && (cnt>1 ? diffFileExist:true) && (cnt<10)){              
                String str="dummy";
                String tempStr="dummy";
                sendPacket("ls");
                _log.debug("\r\n[Log] Host["+host+"] ls["+(cnt++)+"] isUploaded["+targetMCU.isMCUFWUploaded()+"]\r\n");
                getLine();
                while(!(str.length()==0&&tempStr.length()==0)){
                    tempStr=str;
                    str = getLine();
                    if(getFileSize(str, fileName).equals(targetMCU.getDiffSize())){
                        targetMCU.setMCUFWUploaded(true);
                        _log.debug("[Log] Upload Sucessed Host["+host+"], DiffName["+targetMCU.getDiffName()+"], DiffSize["+getFileSize(str, fileName)+"]\r\n");
                    }
                    if(str.contains(targetMCU.getDiffName())){
                        diffFileExist=true;                     
                    }                   
                }
                getLine();//getLine을 대기를 위해 사용하자 -_-;;
            }
            if(diffFileExist==false){
                if(!targetMCU.getDescr().contains("Please Check CoordiFile")){
                    targetMCU.setDescr(targetMCU.getDescr()+" Please Check CoordiFile, ");
                }
                _log.debug("[Log] "+targetMCU.getDiffName()+"이 "+targetMCU.getServer()+"에 존재하는지 Check하세요!");    
            }
        }else{
            if(!targetMCU.getDescr().contains("Please Check DiffFile")){
                targetMCU.setDescr(targetMCU.getDescr()+" Please Check DiffFile, ");
            }
            _log.debug("[Log] "+targetMCU.getDiffName()+"이 "+targetMCU.getServer()+"에 존재하는지 Check하세요!");
        }
        
        mgrFile.fileAppend(log,"\r\n[Log] Setted MCU Info\r\n");
        mgrFile.fileAppend(log,targetMCU.toString());
        return targetMCU.isMCUFWUploaded();
    }

    public boolean uploadCoordiFile(String fileName)  throws Exception{
        boolean coordiFileExist=false;
        targetMCU.setDate(getTime());
        //DiffTable에 업로드할 파일명이 존재할 때만 Upload 실행
        if(targetMCU.getCoordiTable().containsKey(fileName)){           
            sendPacket("shell \"./ftp.script -s "+targetMCU.getServer()+" -f "+fileName+" &\"");
            getPacket();

            targetMCU.setCoordiFWUploaded(false);
            int cnt=0;
            while(!(targetMCU.isCoordiFWUploaded()) && (cnt>1 ? coordiFileExist:true) && (cnt<10)){             
                String str="dummy";
                String tempStr="dummy";
                sendPacket("ls");
                _log.debug("\r\n[Log] Host["+host+"] ls["+(cnt++)+"] isCoordiFWUploaded["+targetMCU.isCoordiFWUploaded()+"]\r\n");
                getLine();
                while(!(str.length()==0&&tempStr.length()==0)){
                    tempStr=str;
                    str = getLine();
                    if(getFileSize(str, fileName).equals(targetMCU.getCoordiSize())){
                        targetMCU.setCoordiFWUploaded(true);
                        _log.debug("[Log] Coordi FW Upload Sucessed Host["+host+"], CoordiName["+targetMCU.getCoordiName()+"], CoordiSize["+getFileSize(str, fileName)+"]\r\n");
                    }
                    if(str.contains(targetMCU.getDiffName())&&cnt>1){
                        coordiFileExist=true;                       
                    }                   
                }
                getLine();//getLine을 대기를 위해 사용하자 -_-;;
            }
            if(coordiFileExist==false){
                if(!targetMCU.getDescr().contains("Please Check CoordiFile")){
                    targetMCU.setDescr(targetMCU.getDescr()+" Please Check CoordiFile, ");
                }
                _log.debug("[Log] "+targetMCU.getCoordiName()+"이 "+targetMCU.getServer()+"에 존재하는지 Check하세요!");    
            }
        }else{
            if(!targetMCU.getDescr().contains("Please Check CoordiFile")){
                targetMCU.setDescr(targetMCU.getDescr()+" Please Check CoordiFile, ");
            }
            _log.debug("[Log] "+targetMCU.getCoordiName()+"이 "+targetMCU.getServer()+"에 존재하는지 Check하세요!");
        }
        return targetMCU.isCoordiFWUploaded();
    }

    public void renameFile(String fileName) throws Exception{
        _log.debug("[Log] Host["+host+"] renameFile");
        sendPacket("shell \"mv "+fileName+" firm.diff\"");
        getPacket();
        Thread.sleep(5000);
    }
    public void bsPatch(String fileName) throws Exception{
        _log.debug("[Log] Host["+host+"] bsPatch");
        sendPacket("shell \"./bspatch oldfirm.tar newfirm.tar "+fileName+" &\"");        
        getPacket();
    }
    public boolean checkProcess(String processName)  throws Exception{        
        boolean processIsRunning=true;
        boolean processIsExist=false;
        targetMCU.setDate(getTime());                         
        
        int cnt=0;
        while(processIsRunning && (cnt<20)){
            _log.debug("[Log] Host["+host+"] ps["+cnt+"] process["+processName+"] IsRunning["+processIsRunning+"]");
            mgrFile.fileAppend(log,"\r\n[Log] Host["+host+"] ps["+cnt+"] process["+processName+"] IsRunning["+processIsRunning+"]\r\n");
            processIsRunning=false;
            String str="dummy";
            String tempStr="dummy";
            sendPacket("ps");            
            getLine();
            while(!(str.length()==0&&tempStr.length()==0)){                
                tempStr=str;
                str = getLine();
                //프로세스 완료를 체크             
                if(processIsRunning==false && str.contains(processName)){
                    processIsRunning=true;
                    processIsExist=true;
                }
            }
            getLine();//getLine을 대기를 위해 사용하자 -_-;;
            cnt++;
        }
        if(processIsExist==false){
            if(!targetMCU.getDescr().contains("Process["+processName+"] Is Not Started")){
                targetMCU.setDescr(targetMCU.getDescr()+" Process["+processName+"] Is Not Started, ");
            }
            _log.debug("[Log] Host["+host+"] Process["+processName+"] Is Not Started!");
            mgrFile.fileAppend(log, "[Log] Host["+host+"] Process["+processName+"] Is Not Started!");
        }else{
            if(processIsRunning==true){
                if(!targetMCU.getDescr().contains("Please Check Process["+processName+"]")){
                    targetMCU.setDescr(targetMCU.getDescr()+" Please Check Process["+processName+"], ");
                }
                _log.debug("[Log] Host["+host+"] Process["+processName+"] Is Not Ended!");
                mgrFile.fileAppend(log, "[Log] Host["+host+"] Process["+processName+"] Is Not Ended!");
            }else{                
                _log.debug("[Log] Host["+host+"] Process["+processName+"] Is Ended!");
                mgrFile.fileAppend(log, "[Log] Host["+host+"] Process["+processName+"] Is Ended!");                
            }
        }                
        return !processIsRunning&&processIsExist;
    }

    public void upgradeMCU() throws Exception{
        _log.debug("[Log] Host["+host+"] UpgradeMCU["+targetMCU.getMCUSWVer()+"] To ["+targetMCU.getLatestMCUVer()+"]");
        sendPacket("shell \"./update_agent.exe &\"");
        getPacket();
        sendPacket("logout");
        getPacket();
    }
    
    public boolean installMCU() throws Exception{
        _log.debug("[Log] Host["+host+"] InstallMCU["+targetMCU.getMCUSWVer()+"] To ["+targetMCU.getLatestMCUVer()+"]");
        boolean done = false; 
        sendPacket("shell \"./install &\"");
        String str=getPacket();
        getPacket();
        resetSystem();
        /*        
        int cnt=0;
        while(str.length()>0 && cnt<50){
            str=getLine();
            _log.debug("[Log] Host["+host+"] Wait MCU Install["+cnt+"]!!");
            if(str.contains("MCU Firmware Install DONE")){
                done=true;
                _log.debug("[Log] Host["+host+"] Reset System!!");                
                break;
            }
            cnt++;
        }
        if(done==false){
            if(!targetMCU.getDescr().contains("MCU Install Don't")){
                targetMCU.setDescr(targetMCU.getDescr()+" MCU Install Don't, ");
            }
            _log.debug("[Log] Host["+host+"] MCU Install Don't");
        }else{
            resetSystem();
            _log.debug("[Log] Host["+host+"] MCU Install Done");
        }
        */
        return done;
    }
    
    public void upgradeCoordi() throws Exception{
        _log.debug("[Log] Host["+host+"] upgrade Coordi Current Ver["+targetMCU.getCoordiFWVer()+"] To ["+targetMCU.getLatestCoordiVer()+"]");
        mgrFile.fileAppend(log, "\r\n[Log] Host["+host+"] upgrade Coordi Current Ver["+targetMCU.getCoordiFWVer()+"] To ["+targetMCU.getLatestCoordiVer()+"]\r\n");
        sendPacket("upgrade coordinator "+targetMCU.getCoordiName());
        getPacket();
        sendPacketNoReturn("y");
        getPacket();
    }   

    private String getMCUHWVer(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("H/W VERSION : ([0-9]|\\.)+.+S/W VERSION");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).replaceAll(
                    "S/W VERSION", "").split(":")[1].trim();
        }
        return find;
    }

    private String getCoordiEUI(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("ID : [0-9A-Z]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }
    
    private String getCoordiHWVer(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("H/W VERSION : ([0-9]|\\.)+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    private String getCoordiFWVer(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern
                .compile("F/W VERSION :([a-zA-Z0-9]|\\.|\\s|\\()+.*\\)");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim().replaceAll(" ", "");
        }
        return find;
    }

    private String getRFPower(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("RF POWER : [0-9-]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    private String getChannel(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("CHANNEL : [0-9]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }
    
    private String getPanID(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("PAN ID : [0-9]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }
    
    private String getPermitTime(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("PERMIT TIME : [0-9]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    private String getAutoChannel(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("AUTO CHANNEL : [a-zA-Z0-9]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    private String getResetKind(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("RESET KIND : [a-zA-Z0-9_]+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    private String getSensorLimit(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern
                .compile("(Sensor limit : [A-Za-z0-9]+\\([0-9-]+\\))|(Sensor limit : [0-9]+)");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    /**
     * @param str
     * @param reg
     * @return
     */
    private String getMCUSWVer(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("S/W VERSION :.*\\)");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = str.substring(matches.start(), matches.end());
            find = find.substring(find.indexOf("(") + 1, find.lastIndexOf(")"));
        }
        return find;
    }

    private String getMCUType(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("OUTDOOR|INDOOR");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = str.substring(matches.start(), matches.end());
        }
        return find;
    }

    private String getMCUID(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("ID : [0-9]+.*TYPE");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).replaceAll(
                    "TYPE", "").split(":")[1].trim();
        }
        return find;
    }
    
    private String getServer(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("SERVER : [0-9.]+.*PORT");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).replaceAll(
                    "PORT", "").split(":")[1].trim();
        }
        return find;
    }

    private String getMCUIP(String str) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("LOCAL : ([0-9]|\\.)+");
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = (str.substring(matches.start(), matches.end())).split(":")[1]
                    .trim();
        }
        return find;
    }

    private String getFileSize(String str,String fileName) {
        String find = "";
        Pattern pattern;
        Matcher matches;
        pattern = Pattern.compile("[0-9]+\\s+[a-zA-Z]+\\s+[0-9]+\\s+[0-9]+:[0-9]+\\s+.*"+fileName);
        matches = pattern.matcher(str);
        while (matches.find()) {
            find = str.substring(matches.start(), matches.end()).split(" ")[0];
        }
        return find;
    }

    /**
     * @return the isTimeOut
     */
    public boolean isTimeOut()
    {
        return isTimeOut;
    }

    /**
     * @param isTimeOut the isTimeOut to set
     */
    public void setTimeOut(boolean isTimeOut)
    {
        this.isTimeOut = isTimeOut;
    }
}

// Telnet 클래스
// Telnet 클래스는 네트워크 접속을 관리한다.
// StreamConnector 클래스를 사용하여 slot 처리를 행한다.
// Constructor는 2 종류가 있고 사용법은 각각 (1)(2)에 대응한다.
public class Telnet extends Thread{
    private static Log _log = LogFactory.getLog(Telnet.class);
    
    static final int DEFAULT_TELNET_PORT = 23;// telnet의 포트 번호(23)
    String host;
    int port;
    int timeOut=60000;  
    static MgrFile mgrFile = new MgrFile();

    boolean settingMCU = false;
    boolean upgradeMCU = false;
    boolean upgradeCoordi = false;
    boolean deleteSensor = false;   
    String server="";
    MCU targetMCU;
    
    ArrayList ipList=new ArrayList();
    Hashtable fileIPTable = new Hashtable();
    Hashtable coordiTable = new Hashtable();        

    Socket serverSocket;
    BufferedReader br;
    BufferedWriter bw;
    
    //TCP 연결을 열어서 처리를 개시한다.
    public void run() {     
        for(int i = 0 ; i<ipList.size();i++){
            setHost((String)ipList.get(i));         
            //---------------------------------------
            //Log File Setting
            //---------------------------------------
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date currentTime = new Date();
            String date = formatter.format(currentTime);
            String log = "";
            log = "log/"+date+"_upgrade_log_"+host+".txt";

            mgrFile.fileAppend(log, "\r\n========================"+host+"===================\r\n");
    
            try {
                serverSocket = new Socket(host, port);
                if(serverSocket.isConnected()){
                    serverSocket.setSoTimeout(timeOut);
                    br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                    bw = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
                    Stream stream = new Stream(br,bw,mgrFile,host,log, targetMCU);
                    stream.initMCU();                               
                    
                    //---------------------------------------
                    //Login
                    //---------------------------------------
                    stream.login();                 
                    
                     
                    //---------------------------------------
                    //MCU 정보 가져오기
                    //---------------------------------------                   
                    targetMCU=stream.getMCUInfo();                                  
                                    
                    //MCU 정보를 성공적으로 가져온 경우만 진행
                    if(targetMCU.getIsInit()){
                    //if(false){
    
                        if(settingMCU){
                            //---------------------------------------
                            //MCU Setting
                            //---------------------------------------                           
                            stream.setMCUInfo();
                            targetMCU=stream.getMCUInfo();                          
                        }   
                        if(upgradeMCU){
                            //Already Upgraded MCU
                            if(targetMCU.getMCUSWVer().equals(targetMCU.getLatestMCUVer())){
                                _log.debug("[Log] "+host+"["+targetMCU.getMCUID()+"] Already Upgraded MCU");
                                if(!targetMCU.getDescr().contains("Already Upgraded MCU")){
                                    targetMCU.setDescr(targetMCU.getDescr()+" Already Upgraded MCU, ");
                                }
                                mgrFile.fileAppend(log,"\r\n[Log] "+targetMCU.getMCUID()+"("+host+"["+port+"]) Already Upgraded MCU\r\n");                          
                            }
                            //업그레이드할 MCU와 현재 MCU 버전이 다른 경우만 Upgrade 진행
                            else if(!targetMCU.getMCUSWVer().equals(targetMCU.getLatestMCUVer())){
        
                                //---------------------------------------
                                //Diff 파일을 업로드
                                //---------------------------------------                               
                                stream.cmdUploadFile(targetMCU.getDiffName());                              
                                //stream.uploadFile(targetMCU.getDiffName());
                                
        
                                //Diff 파일이 성공적으로 업로드 될 경우 실행
                                if(stream.checkProcess("./ftp.script")){
                                    //1703이전 Revision에 대한 MCU 업그레이드
                                    if(Integer.parseInt(targetMCU.getMCUSWVer())<1703){
                                        //---------------------------------------
                                        //Diff 파일 이름 변경
                                        //---------------------------------------                                       
                                        stream.renameFile(targetMCU.getDiffName());
            
                                        //---------------------------------------
                                        //Upgrade 실행
                                        //---------------------------------------                                       
                                        stream.upgradeMCU();
                                    }
                                    //1703 이후 Revision에 대한 MCU 업그레이드
                                    else if(Integer.parseInt(targetMCU.getMCUSWVer())>=1703){
                                        //---------------------------------------
                                        //BSPatch 실행
                                        //---------------------------------------                                       
                                        stream.bsPatch(targetMCU.getDiffName());
                                        
                                        if(stream.checkProcess("./bspatch")){
                                            //---------------------------------------
                                            //Install 실행
                                            //---------------------------------------                                           
                                            stream.installMCU();
                                        }
                                    }
                                    
                                    //---------------------------------------
                                    //Socket 종료
                                    //---------------------------------------
                                    serverSocket.close();
                                    _log.debug("[Log] Host["+host+"] Thread Exit!!!");                                  
                                    Thread.currentThread().sleep(60000*5);                                  
        
                                    //---------------------------------------
                                    //Reconnection
                                    //---------------------------------------
                                    serverSocket = new Socket(host, port);
        
                                    if(serverSocket.isConnected()){
                                        serverSocket.setSoTimeout(timeOut);
                                        br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                                        bw = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
                                        stream = new Stream(br,bw,mgrFile,host,log, targetMCU);
                                        stream.initMCU();
                                        
                                        //---------------------------------------
                                        //Login
                                        //---------------------------------------
                                        stream.login();
        
                                        //---------------------------------------
                                        //MCU 정보 가져오기
                                        //---------------------------------------                                       
                                        targetMCU=stream.getMCUInfo();                                                                                                      
                                        
                                        if(targetMCU.getLatestMCUVer().equals(targetMCU.getMCUSWVer())){
                                            _log.debug("[Log] Host["+host+"] MCU Upgrade Success!!!");
                                            targetMCU.setMCUFWUpgraded(true);
                                            if(!targetMCU.getDescr().contains("MCU Upgrade Success TargetRevision["+targetMCU.getLatestMCUVer()+"]")){
                                                targetMCU.setDescr(targetMCU.getDescr()+" MCU Upgrade Success TargetRevision["+targetMCU.getLatestMCUVer()+"], ");
                                            }
                                        }else{
                                            _log.debug("[Log] Host["+host+"] MCU Upgrade Fail ["+targetMCU.getMCUSWVer()+"] To ["+targetMCU.getLatestMCUVer()+"]!!!");
                                            targetMCU.setMCUFWUpgraded(false);
                                            if(!targetMCU.getDescr().contains("MCU Upgrade Fail ["+targetMCU.getMCUSWVer()+"] To ["+targetMCU.getLatestMCUVer()+"]!!!")){
                                                targetMCU.setDescr(targetMCU.getDescr()+" MCU Upgrade Fail ["+targetMCU.getMCUSWVer()+"] To ["+targetMCU.getLatestMCUVer()+"]!!!, ");
                                            }
                                            mgrFile.fileAppend(log,"\r\n[Error] MCU Upgraded Fail!\r\n");
                                        }
                                    }else{
                                        mgrFile.fileAppend(log,"\r\n[Error] "+host+"["+port+"] ReConnection Fail\r\n");
                                    }                                                               
                                }
                                //Upload Fail
                                else{
                                    mgrFile.fileAppend(log,"\r\n[Error] "+targetMCU.getDiffName()+" Upload Fail\r\n");
                                    if(!targetMCU.getDescr().contains(targetMCU.getDiffName()+" Upload Fail")){
                                        targetMCU.setDescr(targetMCU.getDescr()+" "+targetMCU.getDiffName()+" Upload Fail, ");
                                    }
                                }
                            }                           
                        }
                        //---------------------------------------
                        //Coordi Upgrade
                        //---------------------------------------
                        if(upgradeCoordi){
                            //MCU가 업그레이드 되지 않음
                            if(!targetMCU.getLatestMCUVer().equals(targetMCU.getMCUSWVer())){
                                _log.debug("[Log] Host["+host+"] Coordi Upgrade Fail Because MCU was't Upgraded!!");
                                mgrFile.fileAppend(log,"\r\n[Log] Host["+host+"] Coordi Upgrade Fail Because MCU was't Upgraded!!\r\n");
                                targetMCU.setCoordiFWUpgraded(true);
                                if(!targetMCU.getDescr().contains("Coordi Upgrade Fail Because MCU was't Upgraded!!")){
                                    targetMCU.setDescr(targetMCU.getDescr()+" Coordi Upgrade Fail Because MCU was't Upgraded!!, ");
                                }
                            }
                            //코디가 이미 업그레이드 됨
                            else if(targetMCU.getLatestCoordiVer().equals(targetMCU.getCoordiFWVer())){
                                _log.debug("[Log] Host["+host+"] Already Upgraded Coordi");
                                mgrFile.fileAppend(log,"\r\n[Log] Host["+host+"] Already Upgraded Coordi\r\n");
                                targetMCU.setCoordiFWUpgraded(true);
                                if(!targetMCU.getDescr().contains("Already Upgraded Coordi")){
                                    targetMCU.setDescr(targetMCU.getDescr()+" Already Upgraded Coordi, ");
                                }
                            }
                            //MCU가 업그레이드 되어 코디를 업그레이드 해야함
                            else if(targetMCU.getMCUSWVer().equals(targetMCU.getLatestMCUVer()) && !targetMCU.getCoordiFWVer().equals(targetMCU.getLatestCoordiVer())){                             
                                //---------------------------------------
                                //Coordi 파일을 업로드
                                //---------------------------------------
                                _log.debug("[Log] Host["+host+"] upload Coordi file["+targetMCU.getCoordiName()+"]");
                                stream.cmdUploadFile(targetMCU.getCoordiName());
                                mgrFile.fileAppend(log,"\r\n[Log] Uploaded Coordi Info\r\n");
                                mgrFile.fileAppend(log,targetMCU.toString());
                                
                                if(stream.checkProcess("./ftp.script")){
                                    //---------------------------------------
                                    //Coordi Upgrade
                                    //---------------------------------------                                    
                                    stream.upgradeCoordi();                                
                                    Thread.sleep(120000);                                    
                                    
                                    //---------------------------------------
                                    //Coordi Show Coordinator
                                    //---------------------------------------                                    
                                    targetMCU.setCoordiFWVer("");
                                    stream.showCoordinator(0);
                                    _log.debug("[Log] latestCoordiVer : "+targetMCU.getLatestCoordiVer()+" Coordi F/WVer : "+targetMCU.getCoordiFWVer());
                                    if(targetMCU.getLatestCoordiVer().equals(targetMCU.getCoordiFWVer())){
                                        _log.debug("[Log] Host["+host+"] Coordi Upgraed Success!!");
                                        mgrFile.fileAppend(log,"\r\n[Log] Host["+host+"] Coordi Upgrade Success!!\r\n");
                                        targetMCU.setCoordiFWUpgraded(true);
                                        if(!targetMCU.getDescr().contains("Coordi Upgrade Success")){
                                            targetMCU.setDescr(targetMCU.getDescr()+" Coordi Upgrade Success, ");
                                        }
                                        
                                        //if(!targetMCU.getMCUID().equals("0")){
                                            //---------------------------------------
                                            //Coordi Setting
                                            //---------------------------------------
                                            _log.debug("[Log] Host["+host+"] Coordi Setting");
                                            stream.setCoordi();
                                        //}
                                    }else{
                                        _log.debug("[Log] Host["+host+"] Coordi Upgraed Fail!!");
                                        mgrFile.fileAppend(log,"\r\n[Log] Host["+host+"] Coordi Upgrade Fail!!\r\n");
                                        targetMCU.setCoordiFWUpgraded(false);
                                        if(!targetMCU.getDescr().contains("Coordi Upgrade Fail!!")){
                                            targetMCU.setDescr(targetMCU.getDescr()+" Coordi Upgrade Fail!!, ");
                                        }
                                    }
                                }
                                //Coordi Upload Fail
                                else{
                                    mgrFile.fileAppend(log,"\r\n[Error] "+targetMCU.getCoordiName()+" Coordi Upload Fail\r\n");
                                    if(!targetMCU.getDescr().contains(targetMCU.getCoordiName()+" Coordi Upload Fail")){
                                        targetMCU.setDescr(targetMCU.getDescr()+" "+targetMCU.getCoordiName()+" Coordi Upload Fail, ");
                                    }
                                }
                            }
                        }
                        //---------------------------------------
                        //Abnormal Sensor 정리
                        //---------------------------------------
                        if(deleteSensor){                       
                            _log.debug("[Log] Host["+host+"] Delete Sensor");                       
                            stream.deleteSensor();                                              
                        }                   
                        //---------------------------------------
                        //ipList에 저장
                        //---------------------------------------                   
                        if(!coordiTable.contains(targetMCU.getCoordiEUI())){
                            mgrFile.fileWrite(targetMCU, mgrFile.getFileName());
                            _log.debug("[Log] ADD File, Host["+host+"], Coordi EUI["+targetMCU.getCoordiEUI()+"] Description["+targetMCU.getDescr()+"]");
                        }else{
                            _log.debug("[Log] Replace File, Host["+host+"], Coordi EUI["+targetMCU.getCoordiEUI()+"] Description["+targetMCU.getDescr()+"]");
                            mgrFile.replaceFile(mgrFile.getFileName(), ".+"+targetMCU.getCoordiEUI()+".+\\r\\n", mgrFile.makeMCUList(targetMCU));
                        }
                    }
                    //Failed MCU Info
                    else{
                        mgrFile.fileAppend(log,"\r\n[Log] "+targetMCU.getMCUID()+"("+host+"["+port+"]) Failed Mcu info\r\n");
                    }                   
                }
                //Connection Fail
                else{                   
                    _log.debug("\r\n[Error] Skip This IP "+host+"["+port+"] Connection Fail\r\n");
                    mgrFile.fileAppend(log,"[Error] Skip This IP "+host+"["+port+"] Connection Fail\r\n");                  
                }           
            } catch (ConnectException e){
                _log.debug("\r\n[Error] Skip This IP "+host+"["+port+"] Connection Exception\r\n");
                mgrFile.fileAppend(log,"[Error] Skip This IP "+host+"["+port+"] Connection Exception\r\n");             
                //e.printStackTrace();
            } catch (Exception e){
                //CLI Login Error
                if (e.getMessage().contains("CLI Login Error")){
                    
                    targetMCU.setMCUID("Unknown");
                    targetMCU.setMCUIP(host);
                    targetMCU.setMCUType("Unknown");
                    targetMCU.setMCUSWVer("Unknown");
                    targetMCU.setMCUHWVer("Unknown");
                    targetMCU.setCoordiEUI("Unknown");
                    targetMCU.setCoordiHWVer("Unknown");
                    targetMCU.setCoordiFWVer("Unknown");
                    targetMCU.setRFPower("Unknown");
                    targetMCU.setPermitTime("Unknown");
                    targetMCU.setTotalCnt("Unknown");
                    targetMCU.setAbnCnt("Unknown");                 
                    targetMCU.setSensorLimit("Unknown");
                    targetMCU.setAutoChannel("Unknown");
                    targetMCU.setResetKind("Unknown");                  
                    targetMCU.setServer("Unknown");
                    targetMCU.setDescr(targetMCU.getDescr()+" CLI Login Error!!");
                    targetMCU.setDate(getTime());
                    
                    if(targetMCU.getIsInit()){
                        if(!fileIPTable.contains(host)){
                            _log.debug("\r\n[Error] ADD File, Host["+host+"] Description["+targetMCU.getDescr()+"]\r\n");
                            mgrFile.fileWrite(targetMCU, mgrFile.getFileName());                        
                        }else{
                            _log.debug("\r\n[Error] Replace File, Host["+host+"] Description["+targetMCU.getDescr()+"]\r\n");
                            mgrFile.replaceFile(mgrFile.getFileName(), ".+"+host+".+\\r\\n", mgrFile.makeMCUList(targetMCU));
                        }
                    }
                    mgrFile.fileAppend(log,"[Error] Host["+host+"] CLI Login Error!!\r\n");                                     
                }
                else if (e.getMessage().contains("TimeOut Error")){
                    targetMCU.setDescr(targetMCU.getDescr()+" TimeOut Error!!");
                    if(targetMCU.getIsInit()){
                        if(!fileIPTable.contains(host)){
                            _log.debug("\r\n[Error] ADD File, Host["+host+"] Description["+targetMCU.getDescr()+"]\r\n");
                            mgrFile.fileWrite(targetMCU, mgrFile.getFileName());                        
                        }else{
                            _log.debug("\r\n[Error] Replace File, Host["+host+"] Description["+targetMCU.getDescr()+"]\r\n");
                            mgrFile.replaceFile(mgrFile.getFileName(), ".+"+host+".+\\r\\n", mgrFile.makeMCUList(targetMCU));
                        }
                    }
                    mgrFile.fileAppend(log,"[Error] Host["+host+"] TimeOut Error!!\r\n");
                }
                else if ((e instanceof java.net.SocketException) && e.getMessage().equals("Broken pipe")){
                    //e.printStackTrace();
                    targetMCU.setDescr(targetMCU.getDescr()+" Broken Pipe!!");                                      
                    if(targetMCU.getIsInit()){
                        if(!coordiTable.contains(targetMCU.getCoordiEUI())){
                            _log.debug("\r\n[Error] ADD File, Host["+host+"] Description["+targetMCU.getDescr()+"]\r\n");
                            mgrFile.fileWrite(targetMCU, mgrFile.getFileName());                        
                        }else{
                            _log.debug("\r\n[Error] Replace File, Host["+host+"] Description["+targetMCU.getDescr()+"]\r\n");
                            mgrFile.replaceFile(mgrFile.getFileName(), ".+"+targetMCU.getCoordiEUI()+".+\\r\\n", mgrFile.makeMCUList(targetMCU));
                        }
                    }
                    mgrFile.fileAppend(log,"[Error] Skip This IP IP["+host+"] Broken Pipe\r\n");                    
                }else{
                    e.printStackTrace();
                    _log.debug("\r\n[Error] Skip This IP["+host+"] Exception!!\r\n");                  
                    mgrFile.fileAppend(log,e.getMessage());                                     
                }
            } finally{
                if(!serverSocket.isClosed()){
                    //---------------------------------------
                    //Socket 종료
                    //---------------------------------------
                    try {
                        serverSocket.close();
                        mgrFile.fileAppend(log,"[Log] Socket["+host+"] Close!\r\n");
                    } catch (IOException e) {
                        _log.error("\r\n[Error] Socket["+host+"] Close!\r\n");
                        mgrFile.fileAppend(log,"[Error] Socket["+host+"] Close!\r\n");
                        mgrFile.fileAppend(log,e.getMessage());
                        //e.printStackTrace();
                    }
                }
                else{
                    _log.debug("\r\n[Error] Socket["+host+"] is Already Closed!\r\n");
                    mgrFile.fileAppend(log,"[Error] Socket["+host+"] is Already Closed!\r\n");
                }
            }
        }
    }


    public static void main(String[] arg) {
        /*
        mgrFile.setFileName("log/ipList.txt");
        Vector vec=mgrFile.fileRead(mgrFile.getFileName());
        mgrFile.getInfo(vec, "10.1.16.211");
        */
        
        int thirdStart=16;
        int thirdEnd=22;
        double ipStart=0;
        double ipEnd= 255;              
        int start;
        int end;
        double group=10;        
        
        //---------------------------------------
        //Upgrade가 된 IP list를 얻어옴
        //---------------------------------------
        mgrFile.setFileName("log/ipList.txt");      
        Hashtable fileIPTable = mgrFile.getColumn(mgrFile.fileRead(mgrFile.getFileName()), "MCUIP");
        Hashtable coordiTable = mgrFile.getColumn(mgrFile.fileRead(mgrFile.getFileName()), "coordiEUI");        
        //하나의 IP에 대해서 진행
        if(arg.length==8){      
            System.out.println("//---------------------------------------");
            System.out.println("//Only One Ip["+arg[7]+"]");
            System.out.println("//---------------------------------------");
            System.out.println("[Log] settingMCU : "+arg[0]);
            System.out.println("[Log] upgradeMCU : "+arg[1]);
            System.out.println("[Log] upgradeCoordi : "+arg[2]);
            System.out.println("[Log] deleteSensor : "+arg[3]);
            System.out.println("[Log] ipCheck : "+arg[4]);
            System.out.println("[Log] latestMCUVer : "+arg[5]);
            System.out.println("[Log] latestCoordiVer : "+arg[6]);
            
            String[] ip=arg[7].split("\\.");
            IPList findIP = new IPList(Integer.parseInt(ip[0]),Integer.parseInt(ip[1]),Integer.parseInt(ip[2]),Integer.parseInt(ip[3]),Integer.parseInt(ip[3]),fileIPTable,5000, arg[4]);           
            findIP.start();
            
            //---------------------------------------
            //upgrade해야할 IP를 찾는 Thread가 끝날 때까지 대기
            //---------------------------------------
            try{
                findIP.join();              
            }catch(Exception e){
                System.out.println("[Error] Find IP Exception");
                e.printStackTrace();
            }
    
            //---------------------------------------
            //targetIP에 대해 Upgrade 작업을 시작함
            //---------------------------------------
            if(findIP.getTargetIPList().size()>0){
                Telnet telnet= new Telnet(findIP.getTargetIPList(), fileIPTable, coordiTable, arg[0], arg[1], arg[2],arg[3], arg[5], arg[6]);           
                telnet.start();
            }
        }
        //검색된 모든 IP에 대해서 진행
        else{
            System.out.println("//---------------------------------------");
            System.out.println("//Every IP");
            System.out.println("//---------------------------------------");
            System.out.println("[Log] settingMCU : "+arg[0]);
            System.out.println("[Log] upgradeMCU : "+arg[1]);
            System.out.println("[Log] upgradeCoordi : "+arg[2]);
            System.out.println("[Log] deleteSensor : "+arg[3]);
            System.out.println("[Log] ipCheck : "+arg[4]);
            System.out.println("[Log] latestMCUVer : "+arg[5]);
            System.out.println("[Log] latestCoordiVer : "+arg[6]);
            
            IPList findIPs[];           
            findIPs=new IPList[(int)(Math.ceil((ipEnd-ipStart)/group)*(thirdEnd-thirdStart+1))];            
            int cnt=0;
            int tempCnt=0;
            for(int j=thirdStart;j<=thirdEnd;j++){
                for(cnt=0;cnt<Math.ceil((ipEnd-ipStart)/group);cnt++){
                    start=(int) (ipStart+cnt*group);
                    if(cnt>0) {
                        start=start+1;
                    }
                    end=(int) (ipStart+(cnt+1)*group);
                    if(end>ipEnd){
                        end=(int) ipEnd;                    
                    }       
                    System.out.println("[Log] Find Open Ip Thread["+(tempCnt+cnt)+"] ip[10.1."+j+"."+start+" ~ "+end+"] Start!!");                  
                    findIPs[(int)(tempCnt+cnt)]=new IPList(10,1,j,start,end,fileIPTable,5000, "false");
                    findIPs[(int)(tempCnt+cnt)].start();            
                }
                tempCnt=tempCnt+cnt;
            }
            
            //---------------------------------------
            //upgrade해야할 IP를 찾는 Thread가 끝날 때까지 대기
            //---------------------------------------
            try{
                for(int i=0;i<tempCnt;i++){
                    findIPs[i].join();          
                }
            }catch(Exception e){
                System.out.println("[Error] Find IP Join Exception");
                e.printStackTrace();
            }
            
            //---------------------------------------
            //targetIP에 대해 Upgrade 작업을 시작함
            //---------------------------------------           
            Telnet telnets[];
            telnets=new Telnet[(int)(Math.ceil((ipEnd-ipStart)/group)*(thirdEnd-thirdStart+1))];
            for(int i=0;i<tempCnt;i++){
                if(findIPs[i].getTargetIPList().size()>0){
                    telnets[i]= new Telnet(findIPs[i].getTargetIPList(), fileIPTable,coordiTable, arg[0], arg[1], arg[2],arg[3], arg[5], arg[6]);               
                    telnets[i].start();
                    System.out.println("[Log] Telnet Thread["+i+"] IP Count["+findIPs[i].getTargetIPList()+"] Start!");
                }
            }   
        }
                
    }
    
    /**
     * @param ipList
     * @param fileIPTable
     * @param coordiTable
     * @param settingMCU
     * @param upgradeMCU
     * @param upgradeCoordi
     * @param deleteSensor
     * @param latestMCUVer
     * @param latestCoordiVer
     */
    public Telnet(ArrayList ipList, Hashtable fileIPTable, Hashtable coordiTable, String settingMCU, String upgradeMCU,String upgradeCoordi, String deleteSensor, String latestMCUVer, String latestCoordiVer) {        
        this.port = DEFAULT_TELNET_PORT;
        this.fileIPTable=fileIPTable;
        this.coordiTable=coordiTable;
        this.ipList=ipList;
        if(settingMCU.equals("true")){
            this.settingMCU = true;
        }else{
            this.settingMCU = false;
        }
        if(upgradeMCU.equals("true")){
            this.upgradeMCU = true;
        }else{
            this.upgradeMCU = false;
        }
        if(upgradeCoordi.equals("true")){
            this.upgradeCoordi = true;
        }else{
            this.upgradeCoordi = false;
        }
        if(deleteSensor.equals("true")){
            this.deleteSensor = true;
        }else{
            this.deleteSensor = false;
        }
        targetMCU= new MCU(latestMCUVer,latestCoordiVer);
    }

    /**
     * @return the host
     */
    public String getHost() {
        return this.host;
    }


    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }


    public String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        String date = formatter.format(currentTime);
        return date;
    }
}
class IPList extends Thread{
    private static Log _log = LogFactory.getLog(IPList.class);
    
    int firstIP;
    int secondIP;
    int thirdIP;
    int fourthFrom;
    int fourthTo;
    Hashtable fileIPList = new Hashtable();
    int timeOut;
    String checkIP="false";
    ArrayList<String> targetIPList = new ArrayList();
    Hashtable exceptIpTable = new Hashtable();

    public IPList(int firstIP, int secondIP, int thirdIP,
            int fourthFrom, int fourthTo, Hashtable fileIPList, int timeOut, String checkIP) {
        super();
        this.firstIP = firstIP;
        this.secondIP = secondIP;
        this.thirdIP = thirdIP;
        this.fourthFrom = fourthFrom;
        this.fourthTo = fourthTo;
        this.fileIPList = fileIPList;
        this.timeOut = timeOut;
        this.checkIP = checkIP;
        /*
        for(int i=0;i<83;i++){
            exceptIpTable.put("10.1.16."+i, "");
        }
        */      
    }

    /**
     * 열려있는 IP 중 Upgrade 해야할 IP를 ArrayList로 리턴한다.
     */
    public void run() {
        try {
            for (int j = fourthFrom; j <= fourthTo; j++) {
                InetAddress address = InetAddress.getByName(firstIP+"."+secondIP+"."+thirdIP+"."+ j);
                if (address.isReachable(timeOut)) {
                    if(!exceptIpTable.containsKey(address.getHostAddress())){                           
                        if(fileIPList.contains(address.getHostAddress())){
                            //IP Check가 false인 경우
                            if(checkIP.equals("false")){
                                targetIPList.add(address.getHostAddress());
                            }
                            _log.debug("[Log] "+address.getHostAddress()+" Exist in File List");
                        }else{
                            targetIPList.add(address.getHostAddress());
                            _log.debug("[Log] "+address.getHostAddress()+" Don't  Existed in File List");
                        }
                    }else{
                        _log.debug("[Log] "+address.getHostAddress()+" Except Ip");
                    }
                }
            }
        } catch (Exception e) {
            _log.debug("[Error] Find IP Thread Exception");
            e.printStackTrace();
        }
    }



    /**
     * @return the targetIPList
     */
    public ArrayList getTargetIPList() {
        return targetIPList;
    }

    /**
     * @param targetIPList the targetIPList to set
     */
    public void setTargetIPList(ArrayList targetIPList) {
        this.targetIPList = targetIPList;
    }
}
class MgrFile{
    private static Log _log = LogFactory.getLog(MgrFile.class);
    
    String fileName="";
    
    synchronized void fileWrite(MCU targetMCU, String fileName){        
        fileAppend(fileName, makeMCUList(targetMCU));       
    }
    
    public String makeMCUList(MCU targetMCU){
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(targetMCU.getMCUID()+";");
        strBuff.append(targetMCU.getMCUIP()+";");
        strBuff.append(targetMCU.getMCUType()+";");
        strBuff.append(targetMCU.getMCUSWVer()+";");
        strBuff.append(targetMCU.getMCUHWVer()+";");
        strBuff.append(targetMCU.getCoordiEUI()+";");
        strBuff.append(targetMCU.getCoordiHWVer()+";");
        strBuff.append(targetMCU.getCoordiFWVer()+";");
        strBuff.append(targetMCU.getChannel()+";");
        strBuff.append(targetMCU.getPanID()+";");
        strBuff.append(targetMCU.getRFPower()+";");
        strBuff.append(targetMCU.getPermitTime()+";");
        strBuff.append(targetMCU.getTotalCnt()+";");
        strBuff.append(targetMCU.getAbnCnt()+";");
        strBuff.append(targetMCU.getJoiningRatio()+";");
        strBuff.append(targetMCU.getSensorLimit()+";");
        strBuff.append(targetMCU.getAutoChannel()+";");
        strBuff.append(targetMCU.getResetKind()+";");       
        strBuff.append(targetMCU.isMCUFWUpgraded()+";");
        strBuff.append(targetMCU.isCoordiFWUpgraded()+";");
        strBuff.append(targetMCU.getDescr()+";");
        strBuff.append(targetMCU.getDate()+";");
        strBuff.append("\r\n");
        return strBuff.toString();
    }

    synchronized void headerWrite(String fileName){     
        fileAppend(fileName, "#");
        fileAppend(fileName, "MCUID;");
        fileAppend(fileName, "MCUIP;");
        fileAppend(fileName, "MCUType;");
        fileAppend(fileName, "MCUSWVer;");
        fileAppend(fileName, "MCUHWVer;");
        fileAppend(fileName, "coordiEUI;");
        fileAppend(fileName, "coordiHWVer;");
        fileAppend(fileName, "CoordiFWVer;");
        fileAppend(fileName, "Channel;");
        fileAppend(fileName, "PanID;");
        fileAppend(fileName, "RFPower;");
        fileAppend(fileName, "permitTime;");
        fileAppend(fileName, "totalCnt;");
        fileAppend(fileName, "abnCnt;");
        fileAppend(fileName, "joiningRatio;");
        fileAppend(fileName, "sensorLimit;");
        fileAppend(fileName, "autoChannel;");
        fileAppend(fileName, "resetKind;");     
        fileAppend(fileName, "isMCUUpgraded;");
        fileAppend(fileName, "isCoordiUpgraded;");
        fileAppend(fileName, "descr;");
        fileAppend(fileName, "date;");
        fileAppend(fileName, "\r\n");       
    }
    /**
     * 파일로 부터 한줄씩 읽어 들인 값을 title,value쌍으로 Hashtable에 넣고
     * Hashtable를 다시 Vector에 넣어 리턴함
     * @return Vector
     */
    public Vector fileRead(String fileName) {
        StringTokenizer token;
        Vector<Hashtable> vc = new Vector();
        String str;
        Hashtable hash;
        try {
            File file= new File(fileName);
            if(!file.exists()){
                headerWrite(fileName);
            }else{
                FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr);
                ArrayList<String> title = new ArrayList<String>();
                while (br.ready()) {
                    int index = 0;
                    hash = new Hashtable();
                    str = br.readLine();
                    token = new StringTokenizer(str,";");
    
                    while (token.hasMoreTokens()) {
                        if(str.startsWith("#")){
                            title.add(token.nextToken().replaceAll("#", ""));
                        }else{
                            String temp=token.nextToken();
                            hash.put(title.get(index), temp);
                            //_log.debug("[Log] key: "+title.get(index)+" value: "+temp);
                            index++;
                        }
                    }
                    if(!str.startsWith("#")){
                        vc.add(hash); // 한줄을 공백으로 나누어 저장된 hash를 배열에 저장
                    }
                }
                fr.close();
            }       
        } catch (Exception e){
            e.printStackTrace();
            _log.debug("[Log] File Don't Exist");
        }
        return vc;
    }

    /**
     * @param vc
     * @param columnName
     * @return Hashtable : Vector에 들어있는 Hashtable에서 전달 받은 키값과 동일한 값만을 뽑아내
     * 다시 Hashtable에 넣어 리턴한다.
     */
    public Hashtable getColumn(Vector vc, String columnName){
        Hashtable originalHash = new Hashtable();
        Hashtable returnHash = new Hashtable();
        for(int k=0;k<vc.size();k++){
            originalHash=(Hashtable)vc.elementAt(k);
            returnHash.put(k, originalHash.get(columnName));
            //_log.debug("[Log] key: "+k+" value: "+originalHash.get(columnName));
        }
        return returnHash;
    }
    
    public Hashtable getInfo(Vector vc, String ip){
        Hashtable originalHash = new Hashtable();
        Hashtable returnHash = new Hashtable();
        for(int k=0;k<vc.size();k++){
            originalHash=(Hashtable)vc.elementAt(k);            
            if(originalHash.get("MCUIP").equals(ip)){
                _log.debug(originalHash.toString());
                returnHash=originalHash;                
            }
        }
        return returnHash;
    }

    synchronized void fileAppend(String fileName, String str) {     
        try {
            BufferedWriter buffWrite = new BufferedWriter(new FileWriter(fileName,true));           
            buffWrite.write(str, 0, str.length());
            buffWrite.flush();          
            buffWrite.close();          
        } catch (Exception e) {
            _log.debug(e);
        }
    }
    
    synchronized void replaceFile(String fileName, String _old, String _new) {
        File srcFile= new File(fileName);
        FileReader freader = null;
        FileWriter fwriter = null;
        StringBuffer sb = new StringBuffer();
        try {
            if (!srcFile.exists()) {
                _log.debug("file not found : "
                        + srcFile.getCanonicalPath());
                System.exit(-1);
            }
            freader = new FileReader(srcFile);
            char[] buffer = new char[512];
            int num = 0;
            while ((num = freader.read(buffer)) > 0) {
                sb.append(buffer, 0, num);
            }
            try {
                freader.close();
            } catch (Exception ignore) {
            }           
            String replaced = replace(sb.toString(), _old, _new);           
            fwriter = new FileWriter(srcFile);
            fwriter.write(replaced);
        } catch (Exception e) {
            _log.debug("[Error] Replace File Exception");
            e.printStackTrace();
        } finally {
            try {
                freader.close();
            } catch (Exception ignore) {
            }
            try {
                fwriter.close();
            } catch (Exception ignore) {
            }           
        }
    }

    synchronized String replace(String origin, String _old, String _new) {
        return origin.replaceAll(_old, _new);
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
