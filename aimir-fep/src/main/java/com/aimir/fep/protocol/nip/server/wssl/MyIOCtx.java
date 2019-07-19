package com.aimir.fep.protocol.nip.server.wssl;
/* MyIOCtx.java
 *
 * Copyright (C) 2006-2015 wolfSSL Inc.
 *
 * This file is part of wolfSSL.
 *
 * wolfSSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * wolfSSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */

import java.io.*;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MyIOCtx
{
    private DataOutputStream out;
    private DataInputStream in;
    private DatagramSocket dsock;
    private InetAddress hostAddress;
    private int port;
    private int recvTimeout;
    private static Log log = LogFactory.getLog(MyIOCtx.class);

    /* if not using DTLS, sock and hostAddr may be null */
    public MyIOCtx(DataOutputStream outStr, DataInputStream inStr,
            DatagramSocket s, InetAddress hostAddr, int port) {
        this.out = outStr;
        this.in = inStr;
        this.dsock = s;
        this.hostAddress = hostAddr;
        this.port = port;
    }

    public void test() {
        if (this.out == null) {
           log.error("out is NULL!");
           // System.exit(1);
        }
        if (this.in == null) {
           log.error("in is NULL!");
          //  System.exit(1);
        }
    }

    public DataOutputStream getOutputStream() {
        return this.out;
    }

    public DataInputStream getInputStream() {
        return this.in;
    }

    public DatagramSocket getDatagramSocket() {
        return this.dsock;
    }

    public InetAddress getHostAddress() {
        return this.hostAddress;
    }

    public void setAddress(InetAddress addr) {
        this.hostAddress = addr;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
    	return this.recvTimeout;
    }
    public void setTimeout( int timeout ){
    	this.recvTimeout = timeout;
    }
    public int isDTLS() {
        if (dsock != null)
            return 1;
        else
            return 0;
    }
}

