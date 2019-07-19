package com.aimir.fep.logger.log;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

import com.aimir.fep.logger.snowflake.SnowflakeGeneration;

public class AimirPatternConverter extends PatternConverter {

	@Override
	protected String convert(LoggingEvent arg0) {
		return String.valueOf(SnowflakeGeneration.getId());
	}

}
