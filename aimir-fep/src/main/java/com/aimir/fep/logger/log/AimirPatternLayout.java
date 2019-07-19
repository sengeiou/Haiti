package com.aimir.fep.logger.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class AimirPatternLayout extends PatternLayout {

	@Override
	protected PatternParser createPatternParser(String pattern) {
		return new AimirPatternParser(pattern);
	}
	
}
