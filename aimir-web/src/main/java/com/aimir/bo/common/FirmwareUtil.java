package com.aimir.bo.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Firmware and File Utility Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
public class FirmwareUtil
{
	
	private static Log log = LogFactory.getLog(FirmwareUtil.class);

	private static File pfile = null;
	private static String home = null;
	private static Hashtable fileList = null;
	private static long lastModified = 0;

    private static final String DIFFPROC_WIN = "bsdiff.exe";
    private static final String DIFFPROC_LINUX = "bsdiff";
    private static final String[] COMMAND_WIN = {"cmd.exe", "/c"};
    private static final String[] COMMAND_LINUX = {"/bin/sh","-c"};
    private static final long MAX_PROCESS_RUNNING_TIME = 30 * 1000;
    
    
	static {
		
        //파일 저장
        String osName = System.getProperty("os.name");
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	home = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	home = CommandProperty.getProperty("firmware.dir");
        }
		

		pfile = new File(home);
		if(!pfile.exists()) {
			log.error(pfile.getName()+" does not exists");
		}
		if(!pfile.isDirectory()) {
			log.error(pfile.getName()+" does not directory");
		}
		lastModified = pfile.lastModified();
	}
	
	/**
	 * 타 클래스에도 makeDiff함수 있음. 비교해서 수정 요함
	 * */
	/*
    public static String makeDiff(String oldFileName, String newFileName, String diffFileName) throws Exception{

        String osName = System.getProperty("os.name");
	    String processDir = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	processDir = CommandProperty.getProperty("firmware.window.tooldir");
        }else{
        	processDir = CommandProperty.getProperty("firmware.tooldir");
        }
        String comm[] = new String[3];

        if(System.getProperty("os.name").toLowerCase().indexOf("window")>-1) {
            comm[0] = COMMAND_WIN[0];
            comm[1] = COMMAND_WIN[1];
            comm[2] = DIFFPROC_WIN + " " + oldFileName + " " + newFileName + " " + diffFileName;
        } else {
            comm[0] = COMMAND_LINUX[0];
            comm[1] = COMMAND_LINUX[1];
            comm[2] = DIFFPROC_LINUX + " " + oldFileName + " " + newFileName + " " + diffFileName;
        }

        StringBuffer ret = new StringBuffer();
        long start = TimeUtil.getCurrentLongTime();
        try {
        	Process ls_proc = null;
        	try{
            //Start process
             ls_proc = Runtime.getRuntime().exec(comm, null, new File(processDir));
        	}catch(Exception e){
        		e.printStackTrace();
        		throw new Exception(e.getMessage());
        	}
            
            //Get input and error streams
            BufferedInputStream ls_in = new BufferedInputStream(ls_proc.getInputStream());
            BufferedInputStream ls_err = new BufferedInputStream(ls_proc.getErrorStream());
            boolean end = false;
            while (!end) {
                int c = 0;
                while ((ls_err.available() > 0) && (++c <= 1000)) {
                    ret.append(ls_err.read());
                }
                c = 0;
                while ((ls_in.available() > 0) && (++c <= 1000)) {
                    ret.append(ls_in.read());
                }
                try {
                    ls_proc.exitValue();
                    //if the process has not finished, an exception is thrown
                    //else
                    while (ls_err.available() > 0)
                        ret.append(ls_err.read());
                    while (ls_in.available() > 0)
                        ret.append(ls_in.read());
                    end = true;
                }
                catch (IllegalThreadStateException ex) {
                    //Process is running
                }
                //The process is not allowed to run longer than given time.
                if (TimeUtil.getCurrentLongTime() - start > MAX_PROCESS_RUNNING_TIME) {
                    ls_proc.destroy();
                    end = true;
                    ret.append("!!!! Process has timed out, destroyed !!!!!");
                    new Exception("Process has timed out, destroyed");
                }
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException ie) {}
            }
        }
        catch (Exception e) {
            ret.append("Error: " + e);
            log.error( ret.toString(),e);
            throw new Exception(e.getMessage());
        }
        
        log.debug( ret.toString() );

        return newFileName;
    }
    */
    

	/**
	 * get files in firmware directory
	 * @return files
	 */
	private static void loadFiles()
	{
		if (fileList == null || lastModified < pfile.lastModified())
		{
			fileList = new Hashtable();
			File[] files = pfile.listFiles();
			if (files == null) {
				return;
			}
			for (File file : files) {
				if (file.isFile())
				{
					fileList.put(file.getName(), file);
				}
			}
		}
	}

	/**
	 * get file names in firmware directory
	 * @return file names
	 */
	public static Vector getFileNames()
	{
		Vector v = new Vector();
		File[] files = pfile.listFiles();
		if (files == null) {
			return v;
		}
		for (File file : files) {
			if (file.isFile())
			{
				v.add(file.getName());
			}
		}
		return v;
	}

	/**
	 * get file
	 * @return file full name
	 */
	public static File getFile(String filename)
	{
		loadFiles();
		return (File) fileList.get(filename);
	}

	/**
	 * @param triggerId
	 * @return
	 */
	/*
	public static Map getTriggerEquipInfo(String triggerId){
		Map map = new HashMap<String, String>();
		String sql = "SELECT \n"
			+ "       ( \n"
			+ "           CASE \n"
			+ "               WHEN EQUIPKIND=0 \n"
			+ "               THEN 'MCU' \n"
			+ "               WHEN EQUIPKIND=1 \n"
			+ "               THEN 'MODEM' \n"
			+ "               WHEN EQUIPKIND=2 \n"
			+ "               THEN 'CODINATOR' \n"
			+ "               ELSE 'Unknown' \n"
			+ "           END \n"
			+ "       ) AS EQUIPKIND, \n"
			+ "       (SELECT DESCR \n"
			+ "         FROM MI_CODE \n"
			+ "        WHERE ID= \n"
			+ "              ( \n"
			+ "                  CASE \n"
			+ "                      WHEN EQUIPKIND=0 \n"
			+ "                      THEN 'McuType.'||EQUIPTYPE \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                      THEN 'MeterType.'||EQUIPTYPE \n"
			+ "                      WHEN EQUIPKIND=2 \n"
			+ "                      THEN 'CodiType.'||EQUIPTYPE \n"
			+ "                      ELSE '' \n"
			+ "                  END \n"
			+ "              ) \n"
			+ "       ) AS EQUIPTYPE, \n"
			+ "       (SELECT DESCR \n"
			+ "         FROM MI_CODE \n"
			+ "        WHERE ID= \n"
			+ "              ( \n"
			+ "                  CASE \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=1 \n"
			+ "                      THEN 'EnergyMeterVendor.'||EQUIPVENDOR \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=2 \n"
			+ "                      THEN 'WaterMeterVendor.'||EQUIPVENDOR \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=3 \n"
			+ "                      THEN 'GasMeterVendor.'||EQUIPVENDOR \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=4 \n"
			+ "                      THEN 'HeatMeterVendor.'||EQUIPVENDOR \n"
			+ "                      ELSE '' \n"
			+ "                  END \n"
			+ "              ) \n"
			+ "       ) AS EQUIPVENDOR, \n"
			+ "       (SELECT DESCR \n"
			+ "         FROM MI_CODE \n"
			+ "        WHERE ID= \n"
			+ "              ( \n"
			+ "                  CASE \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=1 \n"
			+ "                      THEN 'EnergyMeterModel.'||EQUIPMODEL \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=2 \n"
			+ "                      THEN 'WaterMeterModel.'||EQUIPMODEL \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=3 \n"
			+ "                      THEN 'GasMeterModel.'||EQUIPMODEL \n"
			+ "                      WHEN EQUIPKIND=1 \n"
			+ "                          AND EQUIPTYPE=4 \n"
			+ "                      THEN 'HeatMeterModel.'||EQUIPMODEL \n"
			+ "                      ELSE '' \n"
			+ "                  END \n"
			+ "              ) \n"
			+ "       ) AS EQUIPMODEL \n"
			+ "  FROM MI_FIRMWAREHISTORY HIS WHERE HIS.TRIGGERID='"+triggerId+"' GROUP BY TRIGGERID,EQUIPKIND,EQUIPTYPE,EQUIPVENDOR,EQUIPMODEL \n";



		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			log.debug("[getTriggerEquipInfo SQL]="+sql);
			// TODO
            // con = JDBCUtil.getDataSource().getConnection();
			pstmt = con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				map.put("equipKind", rs.getString("equipKind"));
				map.put("equipType", rs.getString("equipType"));
				if(rs.getString("equipVendor")!=null && rs.getString("equipVendor").length()>0){
					map.put("equipVendor", rs.getString("equipVendor"));
				}
				if(rs.getString("equipModel")!=null && rs.getString("equipModel").length()>0){
					map.put("equipModel", rs.getString("equipModel"));
				}
			}

		}catch(Exception e){
			log.error(e);
		}
		finally{
		    // TODO
            // JDBCUtil.close(rs, pstmt, con);
		}
		return map;
	}
	*/

	public static String getCurrentYYYYMMDDHHMMSS() {
		String format = "yyyyMMddHHmmss";

		return DateTimeUtil.getCurrentDateTimeByFormat(format);
	}

	/**
     * decodeCodiVer
     * @param strVer
     * @return 33 -> 2.1
     */
    public static String decodeCodiVer(String strVer)
    {
        if(!strVer.contains(".")){
            strVer=Integer.toHexString(Integer.parseInt(strVer));
            if(strVer.length()>=2){
                strVer=strVer.substring(0,1)+"."+strVer.substring(1);
            }
        }
        return strVer;
    }

    /**
     * encodeCodiVer
     * @param strVer
     * @return 2.1 -> 33
     */
    public static String encodeCodiVer(String strVer)
    {
        if(strVer.contains(".")){
            strVer=strVer.replace(".", "");
            strVer=String.valueOf(Integer.parseInt(strVer, 16));
        }
        return strVer;
    }

    /**
     * decodeCodiBuild
     * @param strBuild
     * @return 10진수를 16진수로 변환 : 19 -> 13, 09->09
     */
    public static String decodeCodiBuild(String strBuild){
    	if(Integer.parseInt(strBuild)<10) {
    		return strBuild;
    	}
        return Integer.toHexString(Integer.parseInt(strBuild));
    }

    /**
     * encodeCodiBuild
     * @param strBuild
     * @return 16진수를 10진수로 변환 : 13 -> 19, 09->09
     */
    public static String encodeCodiBuild(String strBuild){
    	if(Integer.parseInt(strBuild)<10) {
    		return strBuild;
    	}
        return String.valueOf(Integer.parseInt(strBuild, 16));
    }
	/**
	 * @param gmtEntry
	 * @return
	 */
	public static String getTimeStampFromGmtEntry(String gmtEntry){
		String gmtYear = "gmtYear: ";
		String gmtMon = "gmtMon: ";
		String gmtDay = "gmtDay: ";
		String gmtHour = "gmtHour: ";
		String gmtMin = "gmtMin: ";
		String gmtSec = "gmtSec: ";
		String _year = gmtEntry.substring(gmtEntry.indexOf(gmtYear)+gmtYear.length(),
				gmtEntry.indexOf(gmtMon)).trim();
		String _mon = gmtEntry.substring(gmtEntry.indexOf(gmtMon)+gmtMon.length(),
				gmtEntry.indexOf(gmtDay)).trim();
		String _day = gmtEntry.substring(gmtEntry.indexOf(gmtDay)+gmtDay.length(),
				gmtEntry.indexOf(gmtHour)).trim();
		String _hour = gmtEntry.substring(gmtEntry.indexOf(gmtHour)+gmtHour.length(),
				gmtEntry.indexOf(gmtMin)).trim();
		String _min = gmtEntry.substring(gmtEntry.indexOf(gmtMin)+gmtMin.length(),
				gmtEntry.indexOf(gmtSec)).trim();
		String _sec = gmtEntry.substring(gmtEntry.indexOf(gmtSec)+gmtSec.length()).trim();

		log.debug("YEAR[" + _year + "] MONTH[" + _mon + "] DAY[" + _day +
		                                                       "] HOUR[" + _hour + "] MIN[" + _min + "] SEC[" + _sec + "]");

		int year = Integer.parseInt(_year);
		int month = Integer.parseInt(_mon);
		int day = Integer.parseInt(_day);
		int hour = Integer.parseInt(_hour);
		int min = Integer.parseInt(_min);
		int sec = Integer.parseInt(_sec);

		log.debug("YEAR[" + year + "] MONTH[" + month + "] DAY[" + day + "]" +
				"HOUR[" + hour + "] MINUTE[" + min + "] SECOND[" + sec + "]");

		String timestamp = year + (month < 10? "0"+month:""+month) +
		(day < 10? "0"+day:""+day) + (hour < 10? "0"+hour:hour) +
		(min < 10? "0"+min:""+min) + (sec < 10? "0"+sec:sec);
		return timestamp;
	}

	/**
	 * @param date
	 * @param Sec
	 * @return
	 */
	public static String getAddSecYYYYMMDDHHMMSS(String date,int Sec) {
		try {
			SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHHmmss");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.SECOND, Sec);
			return sdf.format(cal.getTime());
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param sec
	 * @return
	 */
	public static String getSecToTime(int sec){
		int day, hour, min=0;
		min = sec / 60;
		sec %= 60;
		hour = min / 60;
		min %= 60;
		day = hour / 24;
		hour %= 24;
		if(day>0){
			return day+" Day "+hour+" Hour "+min+" Min "+sec+" Sec";
		}else if(hour>0){
			return hour+" Hour "+min+" Min "+sec+" Sec";
		}else if(min >0){
			return min+" Min "+sec+" Sec";
		}else {
			return sec+" Sec";
		}
	}

	/**
	 * 해당 MCU의 revision 정보를 입력 받아 cmdPackageDistribution을 사용해야 하는 버전인지
	 * cmdDistribution 명령을 사용해야하는지 여부를 리턴해준다.
	 * @param revision
	 * @return
	 */
	public static boolean isCmdDistributionRevision(String revision) {
		boolean isCmdDistributionRevision=false;
		FirmwareUtil fu = new FirmwareUtil();
		Double doubleCehckRevision=Double.parseDouble(fu.getCommonProperty("firmware.checkRevision"));//★★ 로직 수정 필요.. 임시로 넣었음.
//		Double doubleCehckRevision=Double.parseDouble(CommandProperty.getProperty("firmware.checkRevision", "3265"));
		if(revision!=null && revision.length()>0) {
			Double doubleRevision=Double.parseDouble(revision.replaceAll("\\D", ""));
			if(doubleRevision>1703 && doubleRevision<doubleCehckRevision) {
				isCmdDistributionRevision=false;
			}
			else if(doubleRevision>=doubleCehckRevision) {
				isCmdDistributionRevision=true;
			}else {
				new Exception("Revision["+revision+"] Do Not Support F/W Distribution!");
			}
		}else {
			log.error("Please Check MCU Revision Information!!");
		}
		return isCmdDistributionRevision;
	}

	/**
	 * 바이너리 파일의 MD5값을 구함
	 * mcuRevision에 따라 gzip을 쓸지 안 쓸지 결정되므로 mcuRevision을 파라미터로 넘겨 받음
	 * @param toFileName
	 * @param mcuRevision
	 * @return
	 * @throws Exception
	 */
	public static String getBinaryMD5(String toFileName, String mcuRevision) throws Exception {

        String osName = System.getProperty("os.name");
        String firmwareDir = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	firmwareDir = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	firmwareDir = CommandProperty.getProperty("firmware.dir");
        }
        
		boolean enableGzip = "true".equals(CommandProperty.getProperty("firmware.enableGzip", "false")) ? true:false;

		File toFile = null;
		if(enableGzip && isCmdDistributionRevision(mcuRevision)) {
			if (toFileName.toLowerCase().endsWith(".tar.gz")) {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".tar.gz", "") + File.separator + toFileName);
			}
			else {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".ebl", "").replace(".bin", "") + File.separator + toFileName+".gz");
			}
		}
		else {
			if (toFileName.toLowerCase().endsWith(".tar.gz")) {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".tar.gz", "") + File.separator + toFileName.replace(".tar.gz", ".tar"));
			}
			else {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".ebl", "").replace(".bin", "") + File.separator + toFileName);
			}
		}
		return MD5Sum.getFileMD5Sum(toFile);
	}

	/**
	 * toFile에서 fromFile로 diff가 없으면 diff를 만들어주고 해당 diff 파일의 MD5값을 리턴함
	 * @param toFileName
	 * @param fromFileName
	 * @return
	 * @throws Exception
	 */
	public static String getDiffMD5(String toFileName,String fromFileName) throws Exception{

        String osName = System.getProperty("os.name");
        String firmwareDir = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	firmwareDir = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	firmwareDir = CommandProperty.getProperty("firmware.dir");
        }
		String newFileName = null;
		String oldFileName = null;
		String diffFileName = null;
		if(toFileName.toLowerCase().endsWith(".tar.gz")){
			newFileName = firmwareDir + File.separator
			+ toFileName.replaceAll(".tar.gz", "")
			+ File.separator
			+ toFileName.replaceAll(".tar.gz", ".tar");
			oldFileName = firmwareDir + File.separator
			+ fromFileName.replaceAll(".tar.gz", "")
			+ File.separator
			+ fromFileName.replaceAll(".tar.gz", ".tar");

			diffFileName = firmwareDir + File.separator
			+ toFileName.replaceAll(".tar.gz", "")
			+ File.separator
			+ toFileName.replaceAll(".tar.gz", "") + "_FROM_"
			+ fromFileName.replaceAll(".tar.gz", "") + ".diff";
		}else{
			newFileName = firmwareDir + File.separator
			+ toFileName.replaceAll(".ebl", "").replace(".bin", "")
			+ File.separator
			+ toFileName;
			oldFileName = firmwareDir + File.separator
			+ fromFileName.replaceAll(".ebl", "").replace(".bin", "")
			+ File.separator
			+ fromFileName;

			diffFileName = firmwareDir + File.separator
			+ toFileName.replaceAll(".ebl", "").replace(".bin", "")
			+ File.separator
			+ toFileName.replaceAll(".ebl", "").replace(".bin", "") + "_FROM_"
			+ fromFileName.replaceAll(".ebl", "").replace(".bin", "") + ".diff";
		}
		File diffFile = new File(diffFileName);

		if(!diffFile.exists()){
			DiffGeneratorUtil.makeDiff(oldFileName, newFileName, diffFileName);
		}

		return MD5Sum.getFileMD5Sum(diffFile);
	}

	public static LinkedHashMap getCodeDescr(){
		LinkedHashMap codeDescrMap = new LinkedHashMap();
    	//Code, Descr
		LinkedHashMap<String, String> equipKindMap = new LinkedHashMap<String, String>();
		LinkedHashMap<String, LinkedHashMap<String, String>> equipTypeMap = new LinkedHashMap<String, LinkedHashMap<String,String>>();
		LinkedHashMap<String, LinkedHashMap<String, String>> vendorMap = new LinkedHashMap<String, LinkedHashMap<String,String>>();
		LinkedHashMap<String, LinkedHashMap<String, String>> modelMap = new LinkedHashMap<String, LinkedHashMap<String,String>>();

    	LinkedHashMap<String, String> tempKindMap = new LinkedHashMap<String,String>();
    	LinkedHashMap<String, String> tempTypeMap = new LinkedHashMap<String,String>();
    	LinkedHashMap<String, String> tempVendorMap = new LinkedHashMap<String,String>();
    	LinkedHashMap<String, String> tempModelMap = new LinkedHashMap<String,String>();
        String sql ="";
    	sql+= "SELECT TO_NUMBER('0') equipkindcode, \n";
    	sql+= "       'MCU' equipkinddescr, \n";
    	sql+= "       TO_NUMBER(mcuType.code) equiptypecode, \n";
    	sql+= "       mcuType.descr equiptypedescr, \n";
    	sql+= "       TO_NUMBER('') vendorcode, \n";
    	sql+= "       '' vendordescr, \n";
    	sql+= "       TO_NUMBER('') modelcode, \n";
    	sql+= "       '' modeldescr \n";
    	sql+= "  FROM \n";
    	sql+= "       (SELECT code, \n";
    	sql+= "              descr \n";
    	sql+= "         FROM MI_CODE \n";
    	sql+= "        WHERE id LIKE 'MCUType%' \n";
    	sql+= "       ) mcuType \n";
    	sql+= "    UNION \n";
    	sql+= "SELECT TO_NUMBER('1') equipkindcode, \n";
    	sql+= "       'Modem' equipkinddescr, \n";
    	sql+= "       TO_NUMBER(vendor.equipTypecode) equiptypecode, \n";
    	sql+= "       vendor.equipTypedescr, \n";
    	sql+= "       TO_NUMBER(vendor.vendorcode) vendorcode, \n";
    	sql+= "       vendor.vendordescr, \n";
    	sql+= "       TO_NUMBER(model.code) modelCode, \n";
    	sql+= "       model.descr modelDescr \n";
    	sql+= "  FROM \n";
    	sql+= "       (SELECT * \n";
    	sql+= "         FROM \n";
    	sql+= "              (SELECT CODE.*, \n";
    	sql+= "                     SUB.FIRSTINSTANCE \n";
    	sql+= "                FROM MI_SUBCODE sub, \n";
    	sql+= "                     MI_CODE code \n";
    	sql+= "               WHERE SUB.SECONDINSTANCE=CODE.INSTANCENAME \n";
    	sql+= "              ) \n";
    	sql+= "       ) model, \n";
    	sql+= "       (SELECT metertype.code equipTypeCode, \n";
    	sql+= "              metertype.descr equipTypeDescr, \n";
    	sql+= "              VENDOR.instancename vendorInst, \n";
    	sql+= "              VENDOR.CODE vendorCode, \n";
    	sql+= "              VENDOR.DESCR vendorDescr \n";
    	sql+= "         FROM MI_CODE vendor , \n";
    	sql+= "              (SELECT code, \n";
    	sql+= "                     descr \n";
    	sql+= "                FROM MI_CODE \n";
    	sql+= "               WHERE id LIKE 'MeterType%' \n";
    	sql+= "              ) meterType \n";
    	sql+= "        WHERE vendor.id LIKE meterType.descr||'Vendor%' \n";
    	sql+= "       ) vendor \n";
    	sql+= " WHERE model.FIRSTINSTANCE =vendor.vendorInst \n";
    	sql+= "    UNION \n";
    	sql+= "SELECT TO_NUMBER('2') equipkindcode, \n";
    	sql+= "       'Coordinator' equipkinddescr, \n";
    	sql+= "       TO_NUMBER('') equiptypecode, \n";
    	sql+= "       '' equiptypedescr, \n";
    	sql+= "       TO_NUMBER('') vendorcode, \n";
    	sql+= "       '' vendordescr, \n";
    	sql+= "       TO_NUMBER('') modelcode, \n";
    	sql+= "       '' modeldescr \n";
    	sql+= "  FROM dual \n";
    	sql+= "ORDER BY equipkindcode, \n";
    	sql+= "       equiptypecode, \n";
    	sql+= "       vendorcode, \n";
    	sql+= "       modelcode \n";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            log.debug("[getCodeDescr SQL]="+sql);
            // TODO
            // con = JDBCUtil.getDataSource().getConnection();
            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();
            String equipKindCode="";
            String equipKindDescr="";
            String equipTypeCode="";
            String equipTypeDescr="";
            String vendorCode="";
            String vendorDescr="";
            String modelCode="";
            String modelDescr="";

            String prevEquipKindCode="";
            String prevEquipTypeCode="";
            String prevVendorCode="";
            String prevModelCode="";

            int equipKindIdx=-1;
            int equipTypeIdx=-1;
            int vendorIdx=-1;
            int modelIdx=-1;
            while (rs.next()) {
            	equipKindCode=rs.getString("equipKindCode")!=null ? rs.getString("equipKindCode") : "";
            	equipKindDescr=rs.getString("equipKindDescr")!=null ? rs.getString("equipKindDescr") : "";
            	equipTypeCode=rs.getString("equipTypeCode")!=null ? rs.getString("equipTypeCode") : "";
            	equipTypeDescr=rs.getString("equipTypeDescr")!=null ? rs.getString("equipTypeDescr") : "";
            	vendorCode=rs.getString("vendorCode")!=null ? rs.getString("vendorCode") : "";
            	vendorDescr=rs.getString("vendorDescr")!=null ? rs.getString("vendorDescr") : "";
            	modelCode=rs.getString("modelCode")!=null ? rs.getString("modelCode") : "";
            	modelDescr=rs.getString("modelDescr")!=null ? rs.getString("modelDescr") : "";

            	/*
            	if(prevEquipKindCode.equals("")&&!equipKindCode.equals("")) {
            		prevEquipKindCode=equipKindCode;
            		prevEquipTypeCode=equipTypeCode;
            		prevVendorCode=vendorCode;
            		prevModelCode=modelCode;
            	}
            	*/

            	log.debug(equipKindCode+"["+equipKindDescr+"] "+equipTypeCode+"["+equipTypeDescr+"] "+vendorCode+"["+vendorDescr+"] "+modelCode+"["+modelDescr+"]");
            	log.debug(prevEquipKindCode+">"+equipKindCode+", "+prevEquipTypeCode+">"+equipTypeCode+", "+prevVendorCode+">"+vendorCode+", "+prevModelCode+">"+modelCode);

            	//-----------------------
            	//  equipKindMap Setting
            	//-----------------------
            	if(!prevEquipKindCode.equals(equipKindCode)) {
            		equipKindIdx++;
            		equipTypeIdx=-1;
            		vendorIdx=-1;
            		modelIdx=-1;

            		equipKindMap.put(equipKindIdx+"", equipKindDescr);
            		log.debug("new[equipKindMap]["+equipKindDescr+"] - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}

            	//-----------------------
            	//  equipTypeMap Setting
            	//-----------------------
            	if(!prevEquipKindCode.equals(equipKindCode)) {
            		equipTypeIdx++;
            		vendorIdx=-1;
            		modelIdx=-1;

            		tempTypeMap = new LinkedHashMap<String,String>();
            		tempTypeMap.put(equipTypeCode, equipTypeDescr);
            		equipTypeMap.put(equipKindIdx+"", tempTypeMap);
            		log.debug("new[equipTypeMap]["+equipTypeDescr+"]  - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}else if(!tempTypeMap.containsKey(equipTypeCode)) {
            		equipTypeIdx++;

            		tempTypeMap.put(equipTypeCode, equipTypeDescr);
            		equipTypeMap.put(equipKindIdx+"", tempTypeMap);
            		log.debug("[equipTypeMap]["+equipTypeDescr+"]  - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}

            	//-----------------------
            	//  vendorMap Setting
            	//-----------------------
            	if(!prevEquipTypeCode.equals(equipTypeCode)) {
            		vendorIdx=0;
            		modelIdx=-1;

            		tempVendorMap = new LinkedHashMap<String,String>();
            		tempVendorMap.put(vendorCode, vendorDescr);
            		vendorMap.put(equipKindIdx+"_"+equipTypeIdx, tempVendorMap);
            		log.debug("new[vendorMap]["+vendorDescr+"]  - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}else if(!tempVendorMap.containsKey(vendorCode)) {
            		vendorIdx++;

            		tempVendorMap.put(vendorCode, vendorDescr);
            		vendorMap.put(equipKindIdx+"_"+equipTypeIdx, tempVendorMap);
            		log.debug("[vendorMap]["+vendorDescr+"]  - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}

            	//-----------------------
            	//  modelMap Setting
            	//-----------------------
            	if(!prevVendorCode.equals(vendorCode)) {
            		modelIdx++;

            		tempModelMap = new LinkedHashMap<String,String>();
            		tempModelMap.put(modelCode, modelDescr);
            		modelMap.put(equipKindIdx+"_"+equipTypeIdx+"_"+vendorIdx, tempModelMap);
            		log.debug("new[modelMap]["+modelDescr+"]  - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}else{
            		modelIdx++;

            		tempModelMap.put(modelCode, modelDescr);
            		modelMap.put(equipKindIdx+"_"+equipTypeIdx+"_"+vendorIdx, tempModelMap);
            		log.debug("[modelMap]["+modelDescr+"]  - ["+equipKindIdx+"]["+equipTypeIdx+"]["+vendorIdx+"]["+modelIdx+"]");
            	}

            	prevEquipKindCode=equipKindCode;
            	prevEquipTypeCode=equipTypeCode;
            	prevVendorCode=vendorCode;
            	prevModelCode=modelCode;
            }

            //리턴할 Map에 구해온 각 타입별 맵들을 셋팅함
            codeDescrMap.put("equipKindMap", equipKindMap);
            codeDescrMap.put("equipTypeMap", equipTypeMap);
            codeDescrMap.put("vendorMap", vendorMap);
            codeDescrMap.put("modelMap",modelMap);

            String key;
            Set set=modelMap.keySet();
            log.debug("mapSize: "+modelMap.size());
            Iterator keys=set.iterator();
            while(keys.hasNext()){
            	//log.debug(keys.next());
                key=(String)keys.next();
                LinkedHashMap<String, String> subMap=(LinkedHashMap<String, String>)modelMap.get(key);
                String key1;
                Set set1=subMap.keySet();
                Iterator keys1=set1.iterator();
                while(keys1.hasNext()){
                    key1=(String)keys1.next();
                    log.debug("key["+key+"] key1["+key1+"] value["+subMap.get(key1)+"]");
                }
            }


        }catch(Exception ex)
        {
            log.error("[getCodeDescr Fail] "+ex.getMessage());
            log.error(ex,ex);
        }
        finally
        {
            try { if(rs!=null) {
				rs.close();
			} } catch(Exception e){}
            try { if(pstmt!=null) {
				pstmt.close();
			} } catch(Exception e){}
            try { if(con!=null) {
				con.close();
			} } catch(Exception e){}
			return codeDescrMap;
        }
    }
	
	public static String getHWVersion(WORD word){
		return word.decodeVersion();
	}
	
	public static String getFWVersion(WORD word) {
		return word.decodeVersion();
	}
	
	public static String getFWBuild(WORD word) {
		return word.getValue()+"";
	}
	
	public String getCommonProperty(String keyStr){
		
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		return prop.getProperty(keyStr);
	}
}