package com.aimir.fep.logger.log;

import org.apache.log4j.helpers.PatternParser;

public class AimirPatternParser extends PatternParser {

	private static final char DEV_LOG_SEQ = 's';
	private static final char DEV_LOG_TIME = 'N';
	
	public AimirPatternParser(String pattenr) {
		super(pattenr);
	}
	
	@Override
	protected void finalizeConverter(char c) {
		switch(c) {
		case DEV_LOG_SEQ:
			currentLiteral.setLength(0);
			addConverter(new AimirPatternConverter());
			break;		
		case DEV_LOG_TIME:
			currentLiteral.setLength(0);
			addConverter(new AimirPatternSecConverter());
			break;
		default:
			super.finalizeConverter(c);
			break;
		}
	}

}
