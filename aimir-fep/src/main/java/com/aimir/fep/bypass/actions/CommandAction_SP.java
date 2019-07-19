package com.aimir.fep.bypass.actions;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.datatype.SMIValue;

public class CommandAction_SP extends CommandAction {

	@Override
	public void execute(String cmd, SMIValue[] smiValues, IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeBypass(byte[] frame, IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

}
