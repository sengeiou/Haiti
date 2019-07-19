package com.aimir.fep.protocol.nip.server.wssl;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.CharacterCodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;
import com.wolfssl.WolfSSL;
import com.wolfssl.WolfSSLContext;
import com.wolfssl.WolfSSLException;
import com.wolfssl.WolfSSLJNIException;
import com.wolfssl.WolfSSLSession;
import com.aimir.fep.protocol.nip.server.NiProtocolHandler;
import com.aimir.fep.protocol.nip.server.wssl.*;

@Deprecated
public class WsslServer  extends Thread  {
    private static Log log = LogFactory.getLog(WsslServer.class);
    public int PORT = 8002;
    
//    private ObjectName objectName = null;
    
    private Integer protocolType = 
            new Integer(FMPProperty.getProperty("protocol.type.default"));
 //   private NioDatagramAcceptor acceptor;
    String dtlsProtocol = FMPProperty.getProperty("protocol.dtls.protocol", "DTLSv1.2"); //	DTLSv1 DTLSv1.2
    // test I/O callbacks */
    private boolean useIOCallbacks= new String("true").equals(FMPProperty.getProperty("protocol.dtls.use.iocallbacks"))? true : false;
    String cipherList =  FMPProperty.getProperty("protocol.dtls.ciperlist");   /* default cipher suite list */

    
    /* verify peer by default */
    private  Integer  verifyPeer = new Integer(FMPProperty.getProperty("protocol.dtls.verifypeer", "1"));
    /* don't use OCSP by default */
    private Integer useOcsp= new Integer(FMPProperty.getProperty("protocol.dtls.use.ocsp","0"));
    /* OCSP override URL */
    String ocspUrl =  FMPProperty.getProperty("protocol.dtls.ocspurl");
    /* atomic record lyr processing */
    private Integer useAtomic = new Integer(FMPProperty.getProperty("protocol.dtls.use.atomic","0"));
    /* public key callbacks */
    private Integer pkCallbacks= new Integer(FMPProperty.getProperty("protocol.dtls.pkcallbacks","0"));
    /* test logging callback */
    private Integer logCallback= new Integer(FMPProperty.getProperty("protocol.dtls.logcallback","0"));
    /* enable CRL monitor */
    private Integer crlDirMonitor= new Integer(FMPProperty.getProperty("protocol.dtls.crldirmonitor","0"));
    /* use pre shared keys */
    private Integer usePsk= new Integer(FMPProperty.getProperty("protocol.dtls.use.psk","0"));
    /* toggle for loading DH params */
    private Integer needDH= new Integer(FMPProperty.getProperty("protocol.dtls.need.dh","0"));
    /* toggle sending PSK ident hint */
    private Integer sendPskIdentityHint = new Integer(FMPProperty.getProperty("protocol.dtls.send.pskidentityhint","1"));


    
    
    private String serverCert =  FMPProperty.getProperty("protocol.dtls.servercert");
    private String serverCertChain =  FMPProperty.getProperty("protocol.dtls.servercertchain");
    private String serverKey =  FMPProperty.getProperty("protocol.dtls.serverkey");
    private String caCert =  FMPProperty.getProperty("protocol.dtls.cacert");
    private String crlPemDir =  FMPProperty.getProperty("protocol.dtls.crlpemdir");
    private String dhParam  =  FMPProperty.getProperty("protocol.dtls.dhparam");
//    private static final int doDTLS = 1;
    private Integer recvTimeout = new Integer(FMPProperty.getProperty("protocol.dtls.recv.timeout","15"));
    
    
    private WolfSSLContext sslCtx = null;
    private WolfSSL sslLib= null;

    private boolean stop = false;
    private NiProtocolHandler handler = null;
    
    
    /**
     * @throws Exception
     */
    public  WsslServer()  throws Exception {

        int ret = 0;
        long method = 0;
        handler = new NiProtocolHandler(false, this.getClass().getSimpleName() + ":" + DateTimeUtil.getCurrentDateTimeByFormat(null));     

        /* load JNI library */
        WolfSSL.loadLibrary();
        log.debug("WolfSSL.loadLibrary()");
        
        /* init library */
        sslLib = new WolfSSL();
        log.debug("new WolfSSL();"); 
        WolfSSL.debuggingON();
        log.debug(" sslLib.debuggingON()");  

        /* set logging callback */
        if (logCallback == 1) {
        	MyLoggingCallback lc = new MyLoggingCallback();
        	WolfSSL.setLoggingCb(lc);
        }
        int dtlsVersion;
        /* sort out DTLS versus TLS versions */
        if (dtlsProtocol.equals("DTLSv1.2")) {
        	dtlsVersion = 2;
        }
        else {
        	dtlsVersion = 1;
        }

        /* set SSL version method */
        switch (dtlsVersion) {
        case 1:
        	method = WolfSSL.DTLSv1_ServerMethod();
        	break;
        case 2:
        	method = WolfSSL.DTLSv1_2_ServerMethod();
        	break;
        }


        if ( cipherList != null && cipherList.equals("")){
        	cipherList = null;
        }
        if ( ocspUrl != null && ocspUrl.equals("")){
        	ocspUrl = null;
        }
        log.debug(" Version : " +( (dtlsVersion == 1) ? "DTLSv1" : "DTLSv1.2"));
        log.debug(" useIOCallbacks : "  + useIOCallbacks);      /* test I/O callbacks */
        log.debug(" String cipherList : " +cipherList );           /* default cipher suite list */
        log.debug(" verifyPeer : " + verifyPeer);                   /* verify peer by default */
        log.debug(" useOcsp : " + useOcsp);                  /* don't use OCSP by default */
        log.debug(" ocspUrl : " + ocspUrl);               /* OCSP override URL */
        log.debug(" useAtomic : " + useAtomic);                /* atomic record lyr processing */
        log.debug(" pkCallbacks : " + pkCallbacks);                /* public key callbacks */
        log.debug(" logCallback : " + logCallback);                /* test logging callback */
        log.debug(" crlDirMonitor :" + crlDirMonitor);             /* enable CRL monitor */
        log.debug(" usePsk : " +usePsk);                    /* use pre shared keys */
        log.debug(" needDH : " +needDH );                    /* toggle for loading DH params */
        
        /* create context */
        sslCtx = new WolfSSLContext(method);

        if (usePsk == 1) {

        	MyPskServerCallback pskServerCb = new MyPskServerCallback();
        	sslCtx.setPskServerCb(pskServerCb);
        	if (sendPskIdentityHint == 1) {
        		ret = sslCtx.usePskIdentityHint("cyassl server");
        		if (ret != WolfSSL.SSL_SUCCESS) {
        			log.error("Error setting PSK Identity Hint");
           			throw new Exception("");
 
        		}
        	}

        } else {

        	/* load certificate/key files */
        	if ( serverCertChain != null &&serverCertChain.length() > 0 ){
        		ret = sslCtx.useCertificateChainFile(serverCertChain);
        		if ( ret !=  WolfSSL.SSL_SUCCESS) {
            		log.error("failed to load server useCertificateChainFile!");
            		throw new Exception("");
            	}
        		log.debug("Read CertificateChainFile" + serverCertChain);
        	}
        	else {
        		ret = sslCtx.useCertificateFile(serverCert,

        				WolfSSL.SSL_FILETYPE_PEM);
        		if (ret != WolfSSL.SSL_SUCCESS) {
        			log.error("failed to load server certificate!");
        			throw new Exception("");
        		}
        		log.debug("Read CertificateFile" + serverCert);
        	}

        	ret = sslCtx.usePrivateKeyFile(serverKey,
        			WolfSSL.SSL_FILETYPE_PEM);
        	if (ret != WolfSSL.SSL_SUCCESS) {
        		log.error("failed to load server private key!");
        		throw new Exception("");
        	}
    		log.debug("Read PrivateKeyFile" + serverKey);
    		
        	/* set verify callback */
        	if (verifyPeer == 0) {
        		sslCtx.setVerify(WolfSSL.SSL_VERIFY_NONE, null);
        	} else {
        		ret = sslCtx.loadVerifyLocations(caCert, null);
        		if (ret != WolfSSL.SSL_SUCCESS) {
        			log.error("failed to load CA certificates!");
        			throw new Exception("");
        		}
        		log.debug("load CA certificate!" + caCert);
        		VerifyCallback vc = new VerifyCallback();
        		sslCtx.setVerify(WolfSSL.SSL_VERIFY_PEER, vc);
        	}
        }

        /* set cipher list */
        if (cipherList == null) {
        	if (usePsk == 1)
        		ret = sslCtx.setCipherList("DHE-PSK-AES128-GCM-SHA256");
        	needDH = 1;
        } else {
        	ret = sslCtx.setCipherList(cipherList);
        }

        if (ret != WolfSSL.SSL_SUCCESS) {
        	log.error("failed to set cipher list, ret = " + ret);
        	throw new Exception("");
        }

        /* set OCSP options, override URL */
        if (useOcsp == 1) {

        	long ocspOptions = WolfSSL.WOLFSSL_OCSP_NO_NONCE;

        	if (ocspUrl != null) {
        		ocspOptions = ocspOptions |
        				WolfSSL.WOLFSSL_OCSP_URL_OVERRIDE;
        	}

        	if (ocspUrl != null) {
        		ret = sslCtx.setOCSPOverrideUrl(ocspUrl);

        		if (ret != WolfSSL.SSL_SUCCESS) {
        			String msg = "failed to set OCSP overrideUrl";
        			log.error(msg);
        			throw new Exception(msg);
        		}
        	}

        	ret = sslCtx.enableOCSP(ocspOptions);
        	if (ret != WolfSSL.SSL_SUCCESS) {
        		String msg = "failed to enable OCSP, ret = " + ret;
        		log.error(msg);
        		throw new Exception(msg);
        	}
        }

    }

    

    /**
     * @return
     */
    public int getPort() {
        return PORT;
    }

    /**
     * @param port
     */
    public void setPort(int port) {
        this.PORT = port;
    }
       

    public void run() {
    	try {
    		startServer();
    	} catch (Exception ex){
    		log.error(ex,ex);
    	}
    }
	/**
	 * @throws Exception
	 */
	public void startServer() throws Exception{
        DatagramSocket d_serverSocket = null;
        ExecutorService executor = Executors.newCachedThreadPool();
        
        int ret = 0;


        InetAddress hostAddress = InetAddress.getByName(FMPProperty.getProperty("fep.ipv6.addr"));
        log.info("Started DTLS Server: localAddress:[ " + hostAddress +":"+PORT+"]");

		MyRecvCallback rcb = new MyRecvCallback();
		MySendCallback scb = new MySendCallback();
		sslCtx.setIORecv(rcb);
		sslCtx.setIOSend(scb);
		
		MyGenCookieCallback gccb = new MyGenCookieCallback();
		sslCtx.setGenCookie(gccb);
		
		
		if (useAtomic == 1) {
			/* register atomic record layer callbacks */
			MyMacEncryptCallback mecb = new MyMacEncryptCallback();
			MyDecryptVerifyCallback dvcb =
					new MyDecryptVerifyCallback();
			sslCtx.setMacEncryptCb(mecb);
			sslCtx.setDecryptVerifyCb(dvcb);
		}
   		if (pkCallbacks == 1) {
			/* register public key callbacks */

			/* ECC */
			MyEccSignCallback eccSign = new MyEccSignCallback();
			MyEccVerifyCallback eccVerify = new MyEccVerifyCallback();
			sslCtx.setEccSignCb(eccSign);
			sslCtx.setEccVerifyCb(eccVerify);

			/* RSA */
			MyRsaSignCallback rsaSign = new MyRsaSignCallback();
			MyRsaVerifyCallback rsaVerify = new MyRsaVerifyCallback();
			MyRsaEncCallback rsaEnc = new MyRsaEncCallback();
			MyRsaDecCallback rsaDec = new MyRsaDecCallback();

			sslCtx.setRsaSignCb(rsaSign);
			sslCtx.setRsaVerifyCb(rsaVerify);
			sslCtx.setRsaEncCb(rsaEnc);
			sslCtx.setRsaDecCb(rsaDec);
   		}
   		
        /* wait for new client connections, then process */
        while (stop == false) {
        	WolfSSLSession ssl  = null;
        	d_serverSocket = null;
        	try {
        		log.debug("\nwaiting for client connection...");

        		byte[] buf = new byte[1500];
        		d_serverSocket = new DatagramSocket(null);
        		d_serverSocket.setReuseAddress(true);
        		//d_serverSocket.bind(new InetSocketAddress(PORT));
        		d_serverSocket.bind(new InetSocketAddress(hostAddress,PORT));
        		DatagramPacket dp = new DatagramPacket(buf, buf.length);
        		d_serverSocket.setSoTimeout(0);

        		try{    			
        			d_serverSocket.receive(dp);
                    log.debug("Receive address[" +  dp.getAddress() + ":" + dp.getPort() + "] Data(length=" + dp.getData().length + ")[" + Hex.decode(dp.getData()) + "]");
        		} catch (Exception ex){
            		log.error("d_serverSocket.receive Error, " + 	dp.getAddress() + " at port " + dp.getPort() );
            		throw ex;
            	}
        		try{
        			d_serverSocket.connect(dp.getAddress(), dp.getPort());
        		} catch (Exception ex){
            		log.error("d_serverSocket.connect error , " +
            				dp.getAddress() + " at port " + dp.getPort() );
            		throw ex;
            	}
        		log.debug("client connection received from " +
        				dp.getAddress() + " at port " + dp.getPort() );
        		

        		/* create SSL object */
        		ssl = new WolfSSLSession(sslCtx);

        		if (usePsk == 0 || cipherList != null || needDH == 1) {
        			ret = ssl.setTmpDHFile(dhParam, WolfSSL.SSL_FILETYPE_PEM);
        			if (ret != WolfSSL.SSL_SUCCESS) {
        				String msg = "failed to set DH file, ret = " +	ret ;
        				log.error(msg);
        				throw new Exception(msg);
        			}
        		}

        		/* enable/load CRL functionality */
        		ret = ssl.enableCRL(0);
        		if (ret != WolfSSL.SSL_SUCCESS) {
        			String msg = "failed to enable CRL, ret = " + ret ;
        			log.error(msg);
        			throw new Exception(msg);
        		}
        		if (crlDirMonitor == 1) {
        			ret = ssl.loadCRL(crlPemDir, WolfSSL.SSL_FILETYPE_PEM,
        					(WolfSSL.WOLFSSL_CRL_MONITOR |
        							WolfSSL.WOLFSSL_CRL_START_MON));
        			if (ret == WolfSSL.MONITOR_RUNNING_E) {
        				log.error("CRL monitor already running, " +
        						"continuing");
        			} else if (ret != WolfSSL.SSL_SUCCESS) {
        				String msg = "failed to start CRL monitor, ret = " + ret ;
        				log.error(msg);
        				throw new Exception(msg);
        			}
        		} else {
        			ret = ssl.loadCRL(crlPemDir, WolfSSL.SSL_FILETYPE_PEM, 0);
        			if (ret != WolfSSL.SSL_SUCCESS) {
        				String msg = "failed to load CRL, ret = " + ret;
        				log.error(msg);
        				throw new Exception(msg);
        			}
        		}

        		MyMissingCRLCallback crlCb = new MyMissingCRLCallback();
        		ret = ssl.setCRLCb(crlCb);
        		if (ret != WolfSSL.SSL_SUCCESS) {
        			String msg ="failed to set CRL callback, ret = " + ret  ;
        			log.error(msg);
        			throw new Exception(msg);
        		}


        		/* register I/O callbacks */
        		MyIOCtx ioctx = new MyIOCtx(null, null,
        				d_serverSocket, hostAddress, PORT);
        		ioctx.setTimeout(this.recvTimeout);
        		ssl.setIOReadCtx(ioctx);
        		ssl.setIOWriteCtx(ioctx);
        		log.debug("Registered I/O callbacks");

        		/* register DTLS cookie generation callback */
        		MyGenCookieCtx gctx = new MyGenCookieCtx(
        				hostAddress, PORT);
        		ssl.setGenCookieCtx(gctx);
        		log.debug("Registered DTLS cookie callback");


        		if (useAtomic == 1) {
        			/* register atomic record layer callbacks */
        			MyAtomicEncCtx encCtx = new MyAtomicEncCtx();
        			MyAtomicDecCtx decCtx = new MyAtomicDecCtx();
        			ssl.setMacEncryptCtx(encCtx);
        			ssl.setDecryptVerifyCtx(decCtx);
        		}

        		if (pkCallbacks == 1) {
        			/* register public key callbacks */
        			/* ECC */
        			MyEccSignCtx eccSignCtx = new MyEccSignCtx();
        			MyEccVerifyCtx eccVerifyCtx = new MyEccVerifyCtx();
        			ssl.setEccSignCtx(eccSignCtx);
        			ssl.setEccVerifyCtx(eccVerifyCtx);

        			/* RSA */
        			MyRsaSignCtx rsaSignCtx = new MyRsaSignCtx();
        			MyRsaVerifyCtx rsaVerifyCtx = new MyRsaVerifyCtx();
        			MyRsaEncCtx rsaEncCtx = new MyRsaEncCtx();
        			MyRsaDecCtx rsaDecCtx = new MyRsaDecCtx();
        			ssl.setRsaSignCtx(rsaSignCtx);
        			ssl.setRsaVerifyCtx(rsaVerifyCtx);
        			ssl.setRsaEncCtx(rsaEncCtx);
        			ssl.setRsaDecCtx(rsaDecCtx);
        		}
        		// Create Thread 
        		executor.execute(new WsslThread(d_serverSocket, ssl, handler));
        		ssl = null;
        		d_serverSocket = null;
        	} catch (UnsatisfiedLinkError ule) {
        		log.error(ule,ule);
        	} catch (WolfSSLException wex) {
        		log.error(wex,wex);
        	} catch (WolfSSLJNIException jex) {
        		log.error(jex,jex);
        	} catch (CharacterCodingException cce) {
        		log.error(cce,cce);
        	} catch (IOException e) {
        		log.error(e,e);
        	} catch (Exception ex){
        		log.error(ex,ex);
        	}finally {
        		try {
	        		if ( ssl != null){
	        			ssl.shutdownSSL();
	        			ssl.freeSSL();
	        		}
        		} catch (Exception esd){
            		log.error(esd, esd);	
        		}
        		try {
	        		if (d_serverSocket != null) {
	        			if ( d_serverSocket.isConnected() )
	        				d_serverSocket.disconnect();
	        			if ( !d_serverSocket.isClosed())
	        				d_serverSocket.close();
	        		}
        		} catch (Exception ess){
            		log.error(ess, ess);	
        		}
        	}
        }
	}
	
	public void stopServer()
	{
		stop = true;
	}
}
