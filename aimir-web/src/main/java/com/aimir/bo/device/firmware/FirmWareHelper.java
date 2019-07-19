package com.aimir.bo.device.firmware;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import  com.aimir.bo.common.CommandProperty;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmWareHelper.java
 * 작성일자/작성자 : 2010.12.09 최창희
 * @see 
 * 
 * 펌웨어 관리자 페이지 에서 사용하는 Helper
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */

public class FirmWareHelper
{
    private static Log log = LogFactory.getLog(FirmWareHelper.class);
    
    private static MessageDigest md = null;
    
    public static int SCOUR_MD5_BYTE_LIMIT = (60000 * 1024);

    private static final String DIFFPROC_WIN = "bsdiff.exe";
    private static final String DIFFPROC_LINUX = "bsdiff";
    private static final String[] COMMAND_WIN = {"cmd.exe", "/c"};
    private static final String[] COMMAND_LINUX = {"/bin/sh","-c"};
    private static final long MAX_PROCESS_RUNNING_TIME = 30 * 1000;

    /*public static int getNextBoardNo(int equipKind,String firmwareInstName){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            String sql = "SELECT REPLACE(ID,'"+firmwareInstName.substring(firmwareInstName.indexOf("/")+1)+"_','') +1 as nextno "
                       + "FROM MI_" +AimirModel.FW_EQUIP_KIND_BOARD[equipKind]+" "
                       + "WHERE FIRMWAREID='"+firmwareInstName+"' ";
            log.debug("SQL> "+sql);
            con = JDBCUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if(rs.next()) {
				return rs.getInt("nextno");
			}
			else {
				return 1;
			}
        }catch(Exception e){
            log.error(e.getMessage());
        }finally{
            JDBCUtil.close(rs, pstmt, con);
        }
        return 1;
    }*/

    public static String getNewTriggerId(int uniqIdx)
    {

        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat uniqdf = new DecimalFormat("0000");
        return DateTimeUtil.getCurrentDateTimeByFormat("yyMMddHHmmss")+uniqdf.format(uniqIdx);
    }

    public static void main(String args[]) {
    	System.out.println(encodeCodiBuild("10"));
    	System.out.println(decodeCodiBuild("16"));
    }

    /**
     * @return current date(format:yyyyMMddHHmmss)
     */
    public static String getCurrentYYYYMMDDHHMMSS() {
        String format = "yyyyMMddHHmmss";

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());
    }

    /**
     * @param strVer
     * @return
     * @throws Exception
     */
    public static int getIntVersion(String strVer) throws Exception
    {
        int intVer = 0;
        Pattern pattern = Pattern.compile("[0-9\\.]+");
        Matcher matcher = pattern.matcher(strVer);
        String ver[] = new String[] {};
        if (matcher.matches())
        {
            if (strVer.contains("."))
            {
                ver = strVer.split("\\.");
            }
            else
            {
                ver = new String[] { strVer, "0" };
            }
            intVer = ((Integer.parseInt(ver[0]) & 0xff) << 8)+ (Integer.parseInt(ver[1]) & 0xff);
        }
        else
        {
            throw new Exception("Parameter Is Not CorrectFormat Format");
        }
        return intVer;
    }

    /**
     * @param strVer
     * @return version 2.1 -> return 513
     * @throws Exception
     */
    public static int getIntBuild(String strVer) throws Exception
    {
    	
    	if(strVer.startsWith("B")|| strVer.startsWith("b")){
    		strVer = strVer.replaceAll("B", "");
    		strVer = strVer.replaceAll("b", "");
    	}
        int intVer = 0;
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(strVer);
        if (matcher.matches())
        {
            intVer = Integer.parseInt(strVer);
        }
        else
        {
            throw new Exception("Parameter Is Not Number Format");
        }
        return intVer;
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
	 * 프로퍼티 설정에 gzip을 사용하도록 설정되어 있어도,
	 * mcuRevision이 gzip 파일을 지원하지 않는 버전이면 gzip 압축을 사용하지 않도록 함 , 구버전의 FirmWareUtil의 함수를 가지고 옴 
	 * @param toFileName
	 * @param mcuRevision
	 * @return
	 * @throws Exception
	 */
	public static String getBinaryURL(String toFileName, String mcuRevision, String fwDownUrl) throws Exception {

		boolean enableGzip = "true".equals(CommandProperty.getProperty("firmware.enableGzip")) ? true:false;
		//http://localhost:8080/aimir-web/gadget/device/firmware/firmwareDown.jsp 호출
		if(enableGzip && isCmdDistributionRevision(mcuRevision)) {
			if (toFileName.toLowerCase().endsWith(".tar.gz")) {
				toFileName =fwDownUrl+"?fileType=binary&fileName=" + toFileName;
			}
			else {
				toFileName = fwDownUrl+"?fileType=binary&fileName=" + toFileName+".gz";
			}
		}
		else {
			if (toFileName.toLowerCase().endsWith(".tar.gz")) {
				toFileName = fwDownUrl+"?fileType=binary&fileName=" + toFileName.replace(".tar.gz", ".tar");
			}else if(toFileName.toLowerCase().endsWith(".ebl")){
				toFileName = fwDownUrl+"?fileType=binary&fileName=" + toFileName+".gz";
			}
			else {
				toFileName = fwDownUrl+"?fileType=binary&fileName=" + toFileName;
			}
		}
		return toFileName;
	}


	/**
	 * 해당 MCU의 revision 정보를 입력 받아 cmdPackageDistribution을 사용해야 하는 버전인지 , 구버전의 FirmWareUtil의 함수를 가지고 옴 
	 * cmdDistribution 명령을 사용해야하는지 여부를 리턴해준다.
	 * @param revision
	 * @return
	 */
	public static boolean isCmdDistributionRevision(String revision) {
		boolean isCmdDistributionRevision=false;
//		double doubleCehckRevision=Double.parseDouble(AIMIRProperty.getProperty("firmware.checkRevison", "3265"));// 프로퍼티 등록 필요
		double doubleCehckRevision=Double.parseDouble("3265");
		
		if(revision!=null && revision.length()>0) {
			double doubleRevision=Double.parseDouble(revision.replaceAll("\\D", ""));
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
	 * mcuRevision에 따라 gzip을 쓸지 안 쓸지 결정되므로 mcuRevision을 파라미터로 넘겨 받음 , 구버전의 FirmWareUtil의 함수를 가지고 옴 
	 * @param toFileName
	 * @param mcuRevision
	 * @return
	 * @throws Exception
	 */
	public static String getBinaryMD5(String toFileName, String mcuRevision) throws Exception {

        String osName = System.getProperty("os.name");
	    String firmwareDir = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	firmwareDir = CommandProperty.getProperty("firmware.window.tooldir");
        }else{
        	firmwareDir = CommandProperty.getProperty("firmware.tooldir");
        }
        
		boolean enableGzip = "true".equals(CommandProperty.getProperty("firmware.enableGzip")) ? true:false;

		File toFile = null;
		if(enableGzip && isCmdDistributionRevision(mcuRevision)) {
			if (toFileName.toLowerCase().endsWith(".tar.gz")) {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".tar.gz", "") + File.separator + toFileName);
			}
			else {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".ebl", "").replace(".dwl", "").replace(".bin", "") + File.separator + toFileName+".gz");
			}
		}
		else {
			if (toFileName.toLowerCase().endsWith(".tar.gz")) {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".tar.gz", "") + File.separator + toFileName.replace(".tar.gz", ".tar"));
			}
			else if(toFileName.toLowerCase().endsWith(".ebl")){
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".ebl", "").replace(".dwl", "").replace(".bin", "") + File.separator + toFileName+".gz");
			}
			else {
				toFile = new File(firmwareDir + File.separator + toFileName.replaceAll(".ebl", "").replace(".dwl", "").replace(".bin", "") + File.separator + toFileName);
			}
		}
		
		System.out.println("toFile======================================================================================="+toFile);
		
		return getFileMD5Sum(toFile);
	}	
	
	   /**
     * Method: getFileMD5Sum Purpose: get the MD5 sum of a file. Scour exchange only
     * counts the first SCOUR_MD5_BYTE_LIMIT bytes of a file for caclulating checksums
     * (probably for efficiency or better comaprison counts against unfinished downloads).
     *
     * @param f
     *            the file to read
     * @return the MD5 sum string
     * @throws IOException
     *             on IO error
     */
    public static String getFileMD5Sum(File f) throws Exception 
    {
        String sum = null;
        FileInputStream in;
       
		in = new FileInputStream(f.getAbsolutePath());
        byte[] b = new byte[1024];
        int num = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();        	
    	
		while ((num = in.read(b)) != -1)
		{
		    out.write(b, 0, num);

		    if (out.size() > SCOUR_MD5_BYTE_LIMIT)
		    {
		        log.debug("Over size: "+out.size()+" file size: "+f.length());
		        sum = md5Sum(out.toByteArray(), 10000);
		        break;
		    }
		}
        if (sum == null)
            sum = md5Sum(out.toByteArray(), SCOUR_MD5_BYTE_LIMIT);

        in.close();
        out.close();			

        return sum;
    }	
    
    public static String md5Sum(byte[] input, int limit)
    {
        try
        {
            if (md == null)
                md = MessageDigest.getInstance("MD5");

            md.reset();
            byte[] digest;

            if (limit == -1)
            {
                digest = md.digest(input);
            }
            else
            {
                md.update(input, 0, limit > input.length ? input.length : limit);
                digest = md.digest();
            }

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < digest.length; i++)
            {
                hexString.append(hexDigit(digest[i]));
            }

            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e.getMessage());
        }
    }
    

    /**
     * Method: hexDigit Purpose: convert a hex digit to a String, used by md5Sum.
     *
     * @param x
     *            the digit to translate
     * @return the hex code for the digit
     */
    static private String hexDigit(byte x)
    {
        StringBuffer sb = new StringBuffer();
        char c;

        // First nibble
        c = (char) ((x >> 4) & 0xf);
        if (c > 9)
        {
            c = (char) ((c - 10) + 'a');
        }
        else
        {
            c = (char) (c + '0');
        }

        sb.append(c);

        // Second nibble
        c = (char) (x & 0xf);
        if (c > 9)
        {
            c = (char) ((c - 10) + 'a');
        }
        else
        {
            c = (char) (c + '0');
        }

        sb.append(c);
        return sb.toString();
    }
    
	/**
	 * toFile에서 fromFile로 diff가 없으면 diff를 만들어주고 해당 diff 파일의 MD5값을 리턴함
	 * @param toFileName
	 * @param fromFileName
	 * @return
	 * @throws Exception
	 */
	public static String getDiffMD5(String toFileName,String fromFileName) throws Exception{
		
		log.info("###########################getDiffMD5()");
        String osName = System.getProperty("os.name");
	    String firmwareDir = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	firmwareDir = CommandProperty.getProperty("firmware.window.tooldir");
        }else{
        	firmwareDir = CommandProperty.getProperty("firmware.tooldir");
        }
        
		log.info("###########################firmwareDir=="+firmwareDir);
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
			+ toFileName.replaceAll(".ebl", "").replace(".bin", "").replace(".dwl", "")
			+ File.separator
			+ toFileName;
			oldFileName = firmwareDir + File.separator
			+ fromFileName.replaceAll(".ebl", "").replace(".bin", "").replace(".dwl", "")
			+ File.separator
			+ fromFileName;

			diffFileName = firmwareDir + File.separator
			+ toFileName.replaceAll(".ebl", "").replace(".bin", "").replace(".dwl", "")
			+ File.separator
			+ toFileName.replaceAll(".ebl", "").replace(".bin", "").replace(".dwl", "") + "_FROM_"
			+ fromFileName.replaceAll(".ebl", "").replace(".bin", "").replace(".dwl", "") + ".diff";
		}
		
		File oldFile = new File(oldFileName);
		File newFile = new File(newFileName);
		File diffFile = new File(diffFileName);
//		System.out.println("###################################################################################diffFile.exists()=="+diffFile.exists());
		if(!diffFile.exists()){
			//makeDiff(oldFileName, newFileName, diffFileName);			
			JBDiff.bsdiff (oldFile, newFile, diffFile) ;
		}

		return getFileMD5Sum(diffFile);
	}    

	/*
	 public static String makeDiff(String oldFileName, String newFileName, String diffFileName) throws Exception{
		 System.out.println("###################################################################################makeDiff()");
		   String processDir = CommandProperty.getProperty("firmware.tooldir"); 
		   System.out.println("###################################################################################processDir=="+processDir);
	        String comm[] = new String[3];
	        System.out.println("###################################################################################oldFileName=="+oldFileName);
	        System.out.println("###################################################################################newFileName=="+newFileName);
	        System.out.println("###################################################################################diffFileName=="+diffFileName);
	        if(System.getProperty("os.name").toLowerCase().indexOf("window")>-1) {
	            comm[0] = COMMAND_WIN[0];
	            comm[1] = COMMAND_WIN[1];
	            comm[2] = DIFFPROC_WIN + " " + oldFileName + " " + newFileName + " " + diffFileName;
	        } else {
	            comm[0] = COMMAND_LINUX[0];
	            comm[1] = COMMAND_LINUX[1];
	            comm[2] = DIFFPROC_LINUX + " " + oldFileName + " " + newFileName + " " + diffFileName;
	            System.out.println("###################################################################################=="+DIFFPROC_LINUX + " " + oldFileName + " " + newFileName + " " + diffFileName);
	        }

	        StringBuffer ret = new StringBuffer();
	        long start = TimeUtil.getCurrentLongTime();
	        try {
	            //Start process
	        	System.out.println("###################################################################################/Start process");
	            Process ls_proc = Runtime.getRuntime().exec(comm, null, new File(processDir));
	            System.out.println("###################################################################################/End Process");
	            //Get input and error streams
	            BufferedInputStream ls_in = new BufferedInputStream(ls_proc.getInputStream());
	            BufferedInputStream ls_err = new BufferedInputStream(ls_proc.getErrorStream());
	            boolean end = false;
	            System.out.println("###################################################################################end=="+end);
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
	                    System.out.println("###################################################################################tryok");
	                }
	                catch (IllegalThreadStateException ex) {
	                	//ex.printStackTrace();
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
	                catch (InterruptedException ie) {ie.printStackTrace();}
	            }
	        }
	        catch (Exception e) {
	            ret.append("Error: " + e);
	            log.error( ret.toString(),e);
	            throw new Exception(e.getMessage());
	        }
	        
	        log.debug("Make Diff="+ret.toString() );

	        return newFileName;
	 }
	 */
}