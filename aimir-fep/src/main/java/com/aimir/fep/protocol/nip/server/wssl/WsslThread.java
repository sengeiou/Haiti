package com.aimir.fep.protocol.nip.server.wssl;

import java.net.DatagramSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.aimir.fep.protocol.nip.server.NiProtocolHandler;
import com.wolfssl.WolfSSL;
import com.wolfssl.WolfSSLJNIException;
import com.wolfssl.WolfSSLSession;

/**
 * @author 
 *
 */
public class WsslThread extends Thread  {
    private static Log log = LogFactory.getLog(WsslThread.class);
    private NiProtocolHandler handler;
    private WolfSSLSession ssl;
    private DatagramSocket socket; 
    
    
	/**
	 * @param socket
	 * @param ssl
	 * @param handler
	 */
	WsslThread(DatagramSocket socket, WolfSSLSession ssl, NiProtocolHandler handler){
		this.socket = socket;
		this.ssl = ssl;
		this.handler = handler;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
        int ret, insz;
        byte[] input = new byte[4096];
   		try {
   			ret = ssl.accept();
    		if (ret != WolfSSL.SSL_SUCCESS) {
    			int err = ssl.getError(ret);
    			String msg = "wolfSSL_accept failed. err = " + err + WolfSSL.getErrorString(err);
    			log.error(msg);
           		throw new Exception(msg);
    		}
    		log.debug("wolfSSL_accept SUCCESS");
   			/* show peer info */
   			showPeer(ssl);

   			/* read client response */
   			insz = ssl.read(input, input.length);
   			if ( insz > 0) {
   				byte[] message = new byte[insz];
   				System.arraycopy(input, 0, message, 0, insz);
   				WsslMessage wmsg= new WsslMessage(ssl, message);
   				handler.messageReceived(null , wmsg);
   			} else {
   				log.error("wolfSSL_read failed, ret = " + insz);
   			}
   		} catch (Exception ex){
   			log.error(ex,ex);
   		}finally {
   			try {
   				ssl.shutdownSSL();
   				ssl.freeSSL();			
   			} catch (Exception esd){
   				log.error(esd, esd);	
   			}
 			socket.disconnect();
   			socket.close();
   		}
	}
	/**
	 * @param ssl
	 */
	void showPeer(WolfSSLSession ssl) {

		String altname;
		long peerCrtPtr;

		try {
			peerCrtPtr = ssl.getPeerCertificate();

			if (peerCrtPtr != 0) {

				log.debug("issuer : " +
						ssl.getPeerX509Issuer(peerCrtPtr));
				log.debug("subject : " +
						ssl.getPeerX509Subject(peerCrtPtr));

				while( (altname = ssl.getPeerX509AltName(peerCrtPtr)) != null)
					log.debug("altname = " + altname);

			} else {
				log.error("peer has no cert!\n");
			}

			log.debug("SSL version is " + ssl.getVersion());
			log.debug("SSL cipher suite is " + ssl.cipherGetName());

		} catch (WolfSSLJNIException e) {
			log.error(e,e);
		}
	}
}
