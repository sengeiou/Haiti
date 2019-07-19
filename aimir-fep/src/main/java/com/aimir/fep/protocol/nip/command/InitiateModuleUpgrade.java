package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class InitiateModuleUpgrade extends AbstractCommand{
    // Params
    private int upgradeMethod;
    private int ftpUrlLength;
    private String ftpUrl;
    private String ftpPort;
    private int ftpDirectoryLength;
    private String ftpDirectory;
    private int targetFileLength;
	private String targetFile;
    private int usernameLength;
    private String username;
    private int passwordLength;
    private String password;
    
    public InitiateModuleUpgrade() {
        super(new byte[] {(byte)0x00, (byte)0x0D});
    }

    public int getUpgradeMethod() {
		return upgradeMethod;
	}

	public int getFtpUrlLength() {
		return ftpUrlLength;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public int getFtpDirectoryLength() {
		return ftpDirectoryLength;
	}

	public String getFtpDirectory() {
		return ftpDirectory;
	}

	public int getTargetFileLength() {
		return targetFileLength;
	}

	public String getTargetFile() {
		return targetFile;
	}

	public int getUsernameLength() {
		return usernameLength;
	}

	public String getUsername() {
		return username;
	}

	public int getPasswordLength() {
		return passwordLength;
	}

	public String getPassword() {
		return password;
	}

	public void setUpgradeMethod(int upgradeMethod) {
		this.upgradeMethod = upgradeMethod;
	}

	public void setFtpUrlLength(int ftpUrlLength) {
		this.ftpUrlLength = ftpUrlLength;
	}

	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public void setFtpDirectoryLength(int ftpDirectoryLength) {
		this.ftpDirectoryLength = ftpDirectoryLength;
	}

	public void setFtpDirectory(String ftpDirectory) {
		this.ftpDirectory = ftpDirectory;
	}

	public void setTargetFileLength(int targetFileLength) {
		this.targetFileLength = targetFileLength;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	public void setUsernameLength(int usernameLength) {
		this.usernameLength = usernameLength;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
    public Command get() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
 
    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        Object obj = info.get("upgradeMethod");
    	log.debug("upgradeMethod : " + obj);
        if ((obj != null) && (obj instanceof Integer)) {
        	upgradeMethod = (int) obj;
		}
        
        obj = info.get("ftpUrl");
    	log.debug("ftpUrl : " + obj);
        if ((obj != null) && (obj instanceof String)) {
        	ftpUrl = (String) obj;
		}
        
        obj = info.get("ftpPort");
    	log.debug("ftpPort : " + obj);
        if ((obj != null) && (obj instanceof String)) {
        	ftpPort = (String) obj;
		}
        
        obj = info.get("ftpDirectory");
    	log.debug("ftpDirectory : " + obj);
        if ((obj != null) && (obj instanceof String)) {
        	ftpDirectory = (String) obj;
		}
        
        obj = info.get("targetFile");
    	log.debug("targetFile : " + obj);
        if ((obj != null) && (obj instanceof String)) {
        	targetFile = (String) obj;
		}
        
        obj = info.get("username");
    	log.debug("username : " + obj);
        if ((obj != null) && (obj instanceof String)) {
        	username = (String) obj;
		}
        
        obj = info.get("password");
    	log.debug("password : " + obj);
        if ((obj != null) && (obj instanceof String)) {
        	password = (String) obj;
		}
        
        ByteArrayOutputStream out = null;
        out = new ByteArrayOutputStream();
        out.write(DataUtil.getByteToInt(upgradeMethod));
        
        if (upgradeMethod == 0x02) {

            byte[] byteArr = ftpUrl.getBytes();
            ftpUrlLength = byteArr.length;
            out.write(DataUtil.getByteToInt(ftpUrlLength));	// FTP URL Length
            out.write(byteArr);								// FTP URL
            log.debug("ftpUrl["+byteArr.length+"] : "+byteArr.toString());
            
            byteArr = DataUtil.get2ByteToInt(ftpPort);
            out.write(byteArr);			// FTP Port
            log.debug("ftpPort["+byteArr.length+"] : "+byteArr.toString());
            
            byteArr = ftpDirectory.getBytes();
            ftpDirectoryLength = byteArr.length;
            out.write(DataUtil.getByteToInt(ftpDirectoryLength));	// FTP Directory Length
            out.write(byteArr);										// FTP Directory
            log.debug("ftpDirectory["+byteArr.length+"] : "+byteArr.toString());
            
            byteArr = targetFile.getBytes();
            targetFileLength = byteArr.length;
            out.write(DataUtil.getByteToInt(targetFileLength));	// Target File Length
            out.write(byteArr);									// Target File
            log.debug("targetFile["+byteArr.length+"] : "+byteArr.toString());
            
            if(username == null || username.isEmpty()) {
                out.write(DataUtil.getByteToInt(0));	// Username Length
                log.debug("username == null or username.isEmpty(). write 0");
            }else {
                byteArr = username.getBytes();
                usernameLength = byteArr.length;
                out.write(DataUtil.getByteToInt(usernameLength));	// User Name Length
                out.write(byteArr);									// User Name
                log.debug("username["+byteArr.length+"] : "+byteArr.toString());
            }
            
            if(password == null || password.isEmpty()) {
                out.write(0);	// Password Length
                log.debug("password == null or password.isEmpty(). write 0");
            }else {
                byteArr = password.getBytes();
                passwordLength = byteArr.length;
                out.write(DataUtil.getByteToInt(passwordLength));	// Password Length
                out.write(byteArr);									// Password
                log.debug("password["+byteArr.length+"] : "+byteArr.toString());
            }
            
        }
        datas[0].setValue(out.toByteArray());
        attr.setData(datas);
        command.setAttribute(attr);
        if (out != null) out.close();
        
        return command;
    }
  
    @Override
    public void decode(byte[] data) {
    	log.info("InitiateModuleUpgrade.decode : " + Hex.decode(data));
    	if(data != null && data.length > 0) {
    		
        	byte[] b = new byte[1];
        	System.arraycopy(data, 0, b, 0, b.length);
        	upgradeMethod =  DataUtil.getIntToByte(b[0]);
        	
        	int len = data.length;
            int pos = 1;
    		if(upgradeMethod == 0x02 && len > 1) {
    			b = new byte[1];
            	System.arraycopy(data, pos, b, 0, b.length);
            	pos += b.length;
            	ftpUrlLength = DataUtil.getIntToByte(b[0]);
            	if(ftpUrlLength > 0) {
                	b = new byte[ftpUrlLength];
                	System.arraycopy(data, pos, b, 0, b.length);
                	pos += b.length;
                	ftpUrl = new String(b).trim();
            	}

    			b = new byte[2];
            	System.arraycopy(data, pos, b, 0, b.length);
            	pos += b.length;
            	ftpPort = DataUtil.getIntTo2Byte(b)+"";
            	
            	b = new byte[1];
            	System.arraycopy(data, pos, b, 0, b.length);
            	pos += b.length;
            	ftpDirectoryLength = DataUtil.getIntToByte(b[0]);
            	if(ftpDirectoryLength > 0) {
                	b = new byte[ftpDirectoryLength];
                	System.arraycopy(data, pos, b, 0, b.length);
                	pos += b.length;
                	ftpDirectory = new String(b).trim();
            	}

            	b = new byte[1];
            	System.arraycopy(data, pos, b, 0, b.length);
            	pos += b.length;
            	targetFileLength = DataUtil.getIntToByte(b[0]);
            	if(targetFileLength > 0) {
                	b = new byte[targetFileLength];
                	System.arraycopy(data, pos, b, 0, b.length);
                	pos += b.length;
                	targetFile = new String(b).trim();
            	}

            	b = new byte[1];
            	System.arraycopy(data, pos, b, 0, b.length);
            	pos += b.length;
            	usernameLength = DataUtil.getIntToByte(b[0]);
            	if(usernameLength > 0) {
                	b = new byte[usernameLength];
                	System.arraycopy(data, pos, b, 0, b.length);
                	pos += b.length;
                	username = new String(b).trim();
            	}

            	b = new byte[1];
            	System.arraycopy(data, pos, b, 0, b.length);
            	pos += b.length;
            	passwordLength = DataUtil.getIntToByte(b[0]);
            	if(passwordLength > 0) {
                	b = new byte[passwordLength];
                	System.arraycopy(data, pos, b, 0, b.length);
                	pos += b.length;
                	password = new String(b).trim();
            	}
    		}
    	}else {
    		log.debug("data == null || data.length = 0");
    	}
    }
 
    @Override
	public String toString() {
	    return "[InitiateModuleUpgrade]"+
	    	   "[upgradeMethod:"+upgradeMethod+"]"+
	    	   "[ftpUrlLength:"+ftpUrlLength+"]"+
	    	   "[ftpUrl:"+ftpUrl+"]"+
	    	   "[ftpPort:"+ftpPort+"]"+
	    	   "[ftpDirectoryLength:"+ftpDirectoryLength+"]"+
	    	   "[ftpDirectory:"+ftpDirectory+"]"+
	    	   "[targetFileLength:"+targetFileLength+"]"+
	    	   "[targetFile:"+targetFile+"]"+
	    	   "[usernameLength:"+usernameLength+"]"+
	    	   "[username:"+username+"]"+
	    	   "[passwordLength:"+passwordLength+"]"+
	    	   "[password:"+password+"]";
	}
 
    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}

    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
    }
}
