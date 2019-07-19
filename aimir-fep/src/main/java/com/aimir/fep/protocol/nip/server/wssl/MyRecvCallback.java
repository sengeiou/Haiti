package com.aimir.fep.protocol.nip.server.wssl;
/* MyRecvCallback.java
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
import java.nio.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Hex;
import com.wolfssl.*;

class MyRecvCallback implements WolfSSLIORecvCallback
{
    private static Log log = LogFactory.getLog(MyRecvCallback.class);
    public int receiveCallback(WolfSSLSession ssl, byte[] buf, int sz,
            Object ctx) {
    	log.debug("receiveCallback");
        MyIOCtx ioctx = (MyIOCtx) ctx;
        int doDTLS = ioctx.isDTLS();

        if (doDTLS == 1) {
//        	log.debug("DTLS");
            int dtlsTimeout;
            DatagramSocket dsock;
            DatagramPacket recvPacket;

            try {
                //dtlsTimeout = ssl.dtlsGetCurrentTimeout() * 1000;
            	//dtlsTimeout = 25*1000;
            	//dtlsTimeout = ssl.dtlsGetCurrentTimeout() * 1000;
            	dtlsTimeout = ioctx.getTimeout() * 1000;
                log.debug("dtlsTimeout="+ dtlsTimeout);
                dsock = ioctx.getDatagramSocket();
                dsock.setSoTimeout(dtlsTimeout);
                recvPacket = new DatagramPacket(buf, sz);

                dsock.receive(recvPacket);
//            	log.debug("dsock.receive");
                log.debug("Receive address[" +  recvPacket.getAddress() + ":" + recvPacket.getPort() + "] Data[" + Hex.decode(recvPacket.getData()) + "]");
                ioctx.setAddress(recvPacket.getAddress());
                ioctx.setPort(recvPacket.getPort());

            } catch (SocketTimeoutException ste) {
            	log.error(ste,ste);
                return WolfSSL.WOLFSSL_CBIO_ERR_TIMEOUT;
            } catch (SocketException se) {
            	log.error(se,se);
                return WolfSSL.WOLFSSL_CBIO_ERR_GENERAL;
            } catch (IOException ioe) {
            	log.error(ioe,ioe);
                return WolfSSL.WOLFSSL_CBIO_ERR_GENERAL;
            } catch (Exception e) {
            	log.error(e,e);
                return WolfSSL.WOLFSSL_CBIO_ERR_GENERAL;
            }
//            log.debug("recvPacket.length=" + recvPacket.getLength());
            return recvPacket.getLength();

        } else {
        	log.debug("NOT DTLS");
            DataInputStream is = ioctx.getInputStream();
            if (is == null) {
            	log.error("DataInputStream is null in recvCallback!");
               // System.exit(1);
            }

            try {
                is.read(buf, 0, sz);
            } catch (IOException e) {
            	log.error(e,e);
                return WolfSSL.WOLFSSL_CBIO_ERR_GENERAL;
            }
        }

        return sz;
    }
}

