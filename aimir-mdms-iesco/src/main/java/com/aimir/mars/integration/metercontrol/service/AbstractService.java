package com.aimir.mars.integration.metercontrol.service;

import org.springframework.stereotype.Service;

import com.aimir.mars.integration.metercontrol.server.data.MeterControlMessage;

@Service
public abstract class AbstractService {

	public abstract void execute(MeterControlMessage message) throws Exception;
}
