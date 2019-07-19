/** 
 * @(#)CommandMMIU.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelecom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.command.frame.mmiu;

import com.aimir.fep.protocol.mrp.command.frame.Command;
import com.aimir.fep.util.FMPProperty;

/** 
 * Modem Command. 
 * 
 * @version     1.0 1 Sep 2003 
 * @author		Park YeonKyoung yeonkyoung@hanmail.net  
 */


public abstract class CommandMMIU extends Command {

    /**
     * Member Variable Description.              <p>
     * String header     : header                  <p>
     * byte   section    :                        <p>
     * ex)             R : modem to server        <p>
     *                 T : server to modem      <p>
     * byte   cmd        : cmd                  <p>
     * byte   len        : length                  <p>
     * String data       : data                <p>
     * byte   chksum     : checksum                <p>
     * byte   end        : end code              <p>
     * byte[] sessionkey : sessionkey (16 bytes) <p>
     */

    protected String header     = "AIMIR";
    protected byte   section    = 0x54;
    protected byte   cmd        = 0x00;
    protected byte   len        = 0x00;
    protected String data;
    protected byte   chksum;
    protected byte   end        = 0x3B;


    /**
     *  Contructor. <p>
     */
    protected CommandMMIU() {

    }
  
 
    /**
     *  Constructor. <p>
     */ 
    protected CommandMMIU(byte cmd, String data) {
        this.cmd = cmd;
        this.data = data;
        setLen();
    }

    /**
     *  Abstract method.<p>
     *  child class runnable this method.<p>
     */
    public abstract byte[] makeCommand();
    public abstract byte[] makeSingleCommand();
    

    /**
     *  get length.               <p>
     *  @return byte type length. <p> 
     */
    public byte getLen() {
        return this.len;
    }


    /**
     *  get check sum 1 byte data.                     <p>
     *  @param command : command not add checksum bit. <p>
     *  @return 1 byte checksum data.                  <p>
     */
    public int getCheckSum (String command) {

        int chk = 0;
		if(command.length() < 1)
            return -1;
		
		for(int x = 0; x < command.length(); x++) {
            chk += (int) command.charAt(x);
        }
        
		return (chk & 0xff);
    }


    /**
     *  get check sum 1 char data.                     <p>
     *  @param command : command not add checksum bit. <p>
     *  @return 1 char checksum data.                  <p>
     *  because in the Java, byte type to 7bit.        <p>
     */
    public char getChecksum(char[] command) {

        int chk = 0;
        int last = 0;

        try {
            for(int i = 0; ; i++) {
                chk += (int)command[i];
            }
        } catch(ArrayIndexOutOfBoundsException e) {
        }

		return (char) (chk & 0xff);

    }


    /**
     *  get check sum 1 byte data.                     <p>
     *  @param command : command not add checksum bit. <p>
     *  @return 1 byte checksum data.                  <p>
     */
    public byte getChecksum(byte[] command) {

        int chk = 0;
        int last = 0;

        try {
            for(int i = 0; ; i++) {
                chk += (int)(command[i]&0xff);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
        }

		return (byte)(chk & 0xff);
    }
   

    /**
     *  set packet data header.        <p>
     *  ex) 'KEPCO'                    <p>
     *  @param header : header string. <p>
     */
    public void setHeader(String header) { 
        this.header = header; 
    }


    /**
     *  set packet data section.            <p>
     *  ex) 'T'                             <p>
     *  @param section : section byte code. <p>
     */
    public void setSection(byte section) { 
        this.section = section; 
    }


    /**
     *  set packet data command.        <p>
     *  ex) '0x00'                      <p>
     *  @param cmd : command byte code. <p>
     */
    public void setCmd(byte cmd) { 
        this.cmd = cmd; 
    }


    /**
     *  set data command.                  <p>
     *  @param data : command data string. <p>
     */
    public void setData(String data) { 
        this.data = data; 
    }


    /**
     *  set packet data end.        <p>
     *  ex) ';'                     <p>
     *  @param end : end byte code. <p>
     */
    public void setEnd(byte end) { 
        this.end = end; 
    }


    /**
     * set length. <p>
     */
    public void setLen() {
        this.len = (byte)this.data.length();
    }


    /**
     * set length.            <p>
     * @param b : byte array. <p>
     */
    public void setLen(byte[] b) {

        try {

            byte temp = 0x00;
            for(int i = 0; ; i++) {
                temp = b[i]; 
                this.len = (byte)(i+1);
            }

        } catch(ArrayIndexOutOfBoundsException e) {
        }
    }


    /**
     * set arributes member varialble. <p>
     */
    public void setAttrByProperty() {

        this.header  = FMPProperty.getProperty("protocol.header.name");
        this.section = (byte)FMPProperty.getProperty("protocol.section.name").charAt(0);
        this.end     = (byte)FMPProperty.getProperty("protocol.end.name").charAt(0);
    }

}
