package com.aimir.fep.bypass.actions;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;

public class CommandAction_GD extends CommandAction {

	public void executeReverseGPRSCommand(IoSession session, CommandData cd) throws Exception {

	}

	@Override
	public void execute(String cmd, SMIValue[] smiValues, IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeBypass(byte[] frame, IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	public void executeReverseGPRSCommandResponse(IoSession session, CommandData sd) {
		// TODO Auto-generated method stub
		
	}
}