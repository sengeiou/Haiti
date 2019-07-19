package com.aimir.fep.logger.log;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

public class AimirPatternSecConverter extends PatternConverter {

	@Override
	protected String convert(LoggingEvent arg0) {
		return  String.valueOf(System.nanoTime());
	}

}
