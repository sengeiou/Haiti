package com.aimir.mars.integration.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.JvmSystemExiter;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.batch.core.launch.support.SystemExiter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>
 * Copyright NuriTelecom Co.Ltd. since 2015
 * </p>
 * 
 * SORIAIntegrationEVTask.java
 *
 */
public class SORIAIntegrationEVTask {

    private static Log log = LogFactory.getLog(SORIAIntegrationEVTask.class);

    private static SystemExiter systemExiter = new JvmSystemExiter();
    private ExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();

    /**
     * start the job execution
     * 
     * @return
     */
    public int start(String configFile) {
        ConfigurableApplicationContext context = null;
        try {
            // build a standard ApplicationContext
            context = new ClassPathXmlApplicationContext(
                    configFile);

            // launch the job
            JobLauncher jobLauncher = (JobLauncher) context
                    .getBean("jobLauncher");
            Job job = (Job) context.getBean("soriaIntegrationEVTask");

            JobExecution jobExecution = jobLauncher.run(job,
                    new JobParameters());

            return exitCodeMapper
                    .intValue(jobExecution.getExitStatus().getExitCode());

        } catch (Throwable e) {
            String message = "Job Terminated in error: " + e.getMessage();
            log.error(message, e);
            return exitCodeMapper.intValue(ExitStatus.FAILED.getExitCode());
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /**
     * Delegate to the exiter to (possibly) exit the VM gracefully.
     * 
     * @param status
     */
    public void exit(int status) {
        systemExiter.exit(status);
    }

}
