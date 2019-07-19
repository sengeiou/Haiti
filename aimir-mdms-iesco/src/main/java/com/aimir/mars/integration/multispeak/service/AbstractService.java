package com.aimir.mars.integration.multispeak.service;

import org.springframework.stereotype.Service;

import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;

@Service
public abstract class AbstractService {

	public abstract void execute(MultiSpeakMessage message) throws Exception;
}
