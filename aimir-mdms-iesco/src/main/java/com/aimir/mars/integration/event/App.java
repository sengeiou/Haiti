package com.aimir.mars.integration.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.util.TimeUtil;

/**
 * <p>
 * Copyright NuriTelecom Co.Ltd. since 2015
 * </p>
 * 
 * App.java Description
 *
 */
public class App {

    private static Log log = LogFactory.getLog(App.class);

    /**
     * main class for starting the batch and initiating the transfer
     * 
     * @param args
     */
    public static void main(String[] args) {

        String configFile = null;

        if (args.length < 1) {
            log.info("Usage:");
            log.info(
                    "App  -configFile config/spring-ev-integration-xxxxxx.xml");
            return;
        }

        for (int i = 0; i < args.length; i += 2) {

            String nextArg = args[i];

            if (nextArg.startsWith("-configFile")) {
                configFile = new String(args[i + 1]);
            }
        }

        long startTime = TimeUtil.getCurrentLongTime();
        SORIAIntegrationEVTask command = new SORIAIntegrationEVTask();
        int result = command.start(configFile);
        command.exit(result);
        long endTime = TimeUtil.getCurrentLongTime();

        log.info("duration =" + (endTime - startTime) / 1000);
    }
}
