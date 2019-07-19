package com.aimir.fep.protocol.nip.server.wssl;

import com.wolfssl.WolfSSLSession;

public class WsslMessage {

	WolfSSLSession session;
	public WolfSSLSession getSession() {
		return session;
	}
	public void setSession(WolfSSLSession session) {
		this.session = session;
	}
	public byte[] getMessage() {
		return message;
	}
	public void setMessage(byte[] message) {
		this.message = message;
	}
	byte[] message;
	WsslMessage(WolfSSLSession session , byte[] message)
	{
		this.session = session;
		this.message = message;
	}
	
}
