package com.aimir.fep.protocol.snmp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class SnmpCodecFactory implements ProtocolCodecFactory {
	@Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return new SnmpTrapEncoder();
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return new SnmpTrapDecoder();
    }
}
