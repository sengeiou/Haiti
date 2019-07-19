/**
 * @(#)InitModemCommand.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.protocol.mrp.command.frame.mmiu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.command.frame.Command;
/**
 * InitModemCommand Class.
 *
 * @version     1.0 1 Sep 2003
 * @author		Park YeonKyoung yeonkyoung@hanmail.net
 */


public class InitModemCommand extends CommandMMIU {
    private static Log log = LogFactory.getLog(InitModemCommand.class);
    public static final char CMD_INIT_MODEM = 0x80;
    char cmd;

    public InitModemCommand(char cmd) {
        this.cmd = cmd;
    }

    public byte[] makeCommand() {

        try {
            byte[] temp = new byte[1024];
            byte[] head = header.getBytes();

            int i = 0;

            for(; i < header.length(); i++)
                temp[i] = head[i];

            temp[i++] = section;
            temp[i++] = (byte)cmd;
            temp[i++] = (byte)len;
            temp[i++] = getChecksum(temp);
            temp[i]   = end;

            byte[] command = new byte[i+1];

            for(int k=0; k<i+1; k++)
                command[k] = temp[k];

            return command;

        } catch(ArrayIndexOutOfBoundsException e) {
            log.error(e);
        }

        return null;
    }

    public byte[] makeSingleCommand(){ return null;}
    
    
	/*
	 * Class Test Method.
	 */
    public static void main(String[] argv) {

        try {

            Command c = new InitModemCommand(InitModemCommand.CMD_INIT_MODEM);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
