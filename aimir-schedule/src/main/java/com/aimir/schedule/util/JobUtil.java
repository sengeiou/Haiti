package com.aimir.schedule.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;

import com.aimir.util.TimeUtil;

/**
 * Job Util
 * @author Administrator
 *
 */
public class JobUtil
{
    private static Log log = LogFactory.getLog(JobUtil.class);

    /**
     * jobdefinition.xml
     * @deprecated
     */
    private static String definitionPath;

    /**
     * jobdefinition.xml Definiton
     * @deprecated
     */
    private static DefinitionManager defs;

    /**
     * jobdefinition.xml File
     * @deprecated
     */
    private static File defFile = null;

    /**
     * jobdefinition.xml
     * @deprecated
     */
    private static long lastModifiedJobDefinition = 0;

    public static void addJob(JobDetail job, JobDataMap map) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getScheduler();
            jobDetailImpl.setGroup("AimirJobQueue");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTrigger-"+ TimeUtil.getCurrentLongTime()+ "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueue");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
//                    .forJob(jobKey)
                    .build();

            log.debug("Next Fire Time: "+simpleTrigger.getNextFireTime());
            log.debug("Previous Fire Time: "+simpleTrigger.getPreviousFireTime());
            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJob(JobDetail job, JobDataMap map, Date startTime) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getScheduler();
            jobDetailImpl.setGroup("AimirJobQueue");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTrigger-"+ TimeUtil.getCurrentLongTime()+ "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueue");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(startTime)
//                    .forJob(jobKey)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJob(JobDetail job, JobDataMap map, Date startTime,
                              Date endTime, int repeatCount, long repeatInterval) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getScheduler();
            jobDetailImpl.setGroup("AimirJobQueue");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTrigger-"+ TimeUtil.getCurrentLongTime()+ "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueue");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            simpleScheduleBuilder.withIntervalInMilliseconds(repeatInterval);
            simpleScheduleBuilder.withRepeatCount(repeatCount);

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(startTime)
                    .endAt(endTime)
//                    .forJob(jobKey)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJob(JobDetail job, JobDataMap map, Date startTime, int repeatCount, long repeatInterval) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getScheduler();
            jobDetailImpl.setGroup("AimirJobQueue");
            jobDetailImpl.setJobDataMap(map);
//            SimpleTrigger trigger = new SimpleTrigger("aimirTrigger-"
//                                                      + TimeUtil.getCurrentLongTime()
//                                                      + "-" + Math.round(Math.random() * 1000),
//                                                      "AimirJobQueue", repeatCount,
//                                                      repeatInterval);
            String triggerName = "aimirTrigger-"+ TimeUtil.getCurrentLongTime()+ "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueue");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            simpleScheduleBuilder.withIntervalInMilliseconds(repeatInterval);
            simpleScheduleBuilder.withRepeatCount(repeatCount);

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(startTime)
//                    .forJob(jobKey)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJobCron(JobDetail job, JobDataMap map, String cronExpression) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getSchedulerCron();
            jobDetailImpl.setGroup("AimirJobScheduler");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTriggerCron-" + TimeUtil.getCurrentLongTime() + "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobScheduler");

            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
//                    .forJob(jobKey)
                    .build();

            sched.scheduleJob(jobDetailImpl, cronTrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addJobCron(JobDetail job, JobDataMap map, String cronExpression, Date startTime, Date endTime) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;
        JobKey jobKey = job.getKey();

        try {
            sched = getSchedulerCron();
            jobDetailImpl.setGroup("AimirJobScheduler");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTriggerCron-" + TimeUtil.getCurrentLongTime() + "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobScheduler");

            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .startAt(startTime)
                    .endAt(endTime)
                    .forJob(jobKey)
                    .build();

            sched.scheduleJob(jobDetailImpl, cronTrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addJobMulti(JobDetail job, JobDataMap map) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getSchedulerMulti();
            jobDetailImpl.setGroup("AimirJobQueueMulti");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTriggerMulti-" + TimeUtil.getCurrentLongTime() + "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueueMulti");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJobMulti(JobDetail job, JobDataMap map, Date startTime) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getSchedulerMulti();
            jobDetailImpl.setGroup("AimirJobQueueMulti");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTriggerMulti-" + TimeUtil.getCurrentLongTime() + "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueueMulti");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(startTime)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJobMulti(JobDetail job, JobDataMap map, Date startTime, Date endTime, int repeatCount,
            long repeatInterval) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getSchedulerMulti();
            jobDetailImpl.setGroup("AimirJobQueueMulti");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTriggerMulti-" + TimeUtil.getCurrentLongTime() + "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueueMulti");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            simpleScheduleBuilder.withIntervalInMilliseconds(repeatInterval);
            simpleScheduleBuilder.withRepeatCount(repeatCount);

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(startTime)
                    .endAt(endTime)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJobMulti(JobDetail job, JobDataMap map, Date startTime, int repeatCount, long repeatInterval) {
        Scheduler sched;
        JobDetailImpl jobDetailImpl = (JobDetailImpl)job;

        try {
            sched = getSchedulerMulti();
            jobDetailImpl.setGroup("AimirJobQueueMulti");
            jobDetailImpl.setJobDataMap(map);
            String triggerName = "aimirTriggerMulti-" + TimeUtil.getCurrentLongTime() + "-" + Math.round(Math.random() * 1000);
            TriggerKey triggerKey = new TriggerKey(triggerName, "AimirJobQueueMulti");
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            simpleScheduleBuilder.withIntervalInMilliseconds(repeatInterval);
            simpleScheduleBuilder.withRepeatCount(repeatCount);

            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(startTime)
                    .build();

            sched.scheduleJob(jobDetailImpl, simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * jobdefinition.xml job definition
     * @deprecated
     */
    public static boolean addJobDefinition(String name,JobDefinition defJob) {
        if (!defs.getDefinitions().containsKey(name)) {
            defs.addDefinition(name, defJob);
            saveJobDefine();
            return true;
        } else {
            return false;
        }
    }

    /**
     * jobdefinition.xml job definition
     * @deprecated
     */
    public static boolean deleteJobDefinition(String name) {
        if (defs.getDefinitions().containsKey(name)) {
            defs.removeDefinition(name);
            saveJobDefine();
            return true;
        } else {
            return false;
        }
    }

    /**
     * jobdefinition.xml job definition
     * @deprecated
     */
    public static Map getJobDefinitionList() {
        loadJobDefine();
        return defs.getDefinitions();
    }

    /**
     * jobdefinition.xml job definition
     * @deprecated
     */
    public static JobDefinition getJobDefinition(String jobName) {
        loadJobDefine();
        return defs.getDefinition(jobName);
    }

    public static String getJobCronList() throws Exception {
        Scheduler sched = getSchedulerMulti();
        StringBuffer sb = new StringBuffer();
        String jobGroup = "AimirJobScheduler";
        Set<JobKey> jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        sb.append("\nSchedule Group [").append(jobGroup).append("]");

        if (jobKeys == null || jobKeys.size() == 0) {
            sb.append("\nNone");
        } else {
            int i = 0;
            for (JobKey jobKey : jobKeys) {
                sb.append("\n");
                sb.append("schedule job[").append(i).append("]=").append(jobKey.getName());
                i++;
            }
        }
        return sb.toString();
    }

    public static String[] getJobDefList() {
        return ScheduleProperty.getProperty("quartz.job.list").split(",");
    }

    public static String getJobList() throws Exception {
        Scheduler sched = getScheduler();
        StringBuffer sb = new StringBuffer();
        String jobGroup = "AimirJobQueue";
        Set<JobKey> jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        sb.append("\nSchedule Group [").append(jobGroup).append("]");

        if (jobKeys == null || jobKeys.size() == 0) {
            sb.append("\nNone");
        } else {
            int i = 0;
            for (JobKey jobKey : jobKeys) {
                sb.append("\n");
                sb.append("schedule job[").append(i).append("]=").append(jobKey.getName());
                i++;
            }
        }
        return sb.toString();
    }

    public static String getJobMultiList() throws Exception {
        Scheduler sched = getSchedulerMulti();
        StringBuffer sb = new StringBuffer();
        String jobGroup = "AimirJobQueueMulti";
        Set<JobKey> jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        sb.append("\nSchedule Group [").append(jobGroup).append("]");

        if (jobKeys == null || jobKeys.size() == 0) {
            sb.append("\nNone");
        } else {
            int i = 0;
            for (JobKey jobKey : jobKeys) {
                sb.append("\n");
                sb.append("schedule job[").append(i).append("]=").append(jobKey.getName());
                i++;
            }
        }
        return sb.toString();
    }

    private static Scheduler getScheduler() {
        Scheduler sched = null;
        try {

            DirectSchedulerFactory dsf = DirectSchedulerFactory.getInstance();
            sched = dsf.getScheduler("AimirJobQueue");
            if (sched == null) {
                SimpleThreadPool threadPool = new SimpleThreadPool(
                    Integer.parseInt(ScheduleProperty.getProperty("AimirJobQueue.thread")),
                                                                   Thread.NORM_PRIORITY);
                threadPool.initialize();
                dsf.createScheduler("AimirJobQueue",
                                    "AimirJobQueue", threadPool,
                                    new RAMJobStore());
                sched = dsf.getScheduler("AimirJobQueue");
                sched.start();
            }
        } catch (Exception e) {
        }
        return sched;

    }

    private static Scheduler getSchedulerCron() {
        Scheduler sched = null;
        try {

            DirectSchedulerFactory dsf = DirectSchedulerFactory.getInstance();
            sched = dsf.getScheduler("AimirJobScheduler");
            if (sched == null) {
                StdSchedulerFactory ssf = new StdSchedulerFactory();
                sched = ssf.getScheduler();
                sched.start();
            }
        } catch (Exception e) {
        }
        return sched;

    }

    private static Scheduler getSchedulerMulti() {
        Scheduler sched = null;
        try {

            DirectSchedulerFactory dsf = DirectSchedulerFactory.getInstance();
            sched = dsf.getScheduler("AimirJobQueueMulti");
            if (sched == null) {
                SimpleThreadPool threadPool = new SimpleThreadPool(
                    Integer.parseInt(ScheduleProperty.getProperty("AimirJobQueue.thread")),
                                                                   Thread.NORM_PRIORITY);
                threadPool.initialize();
                dsf.createScheduler("AimirJobQueueMulti",
                                    "AimirJobQueueMulti", threadPool,
                                    new RAMJobStore());
                sched = dsf.getScheduler("AimirJobQueueMulti");
                sched.start();
            }
        } catch (Exception e) {
        }
        return sched;

    }

    /**
     * jobdefinition.xml
     * @deprecated
     */
    private static synchronized void initJobDefine() {
        definitionPath = ScheduleProperty.getProperty("quartz.job.definition");
        log.debug("Quartz Scheduler Job DefinitionPath="+definitionPath);
        defFile = new File(definitionPath);
        if (!defFile.exists()) {
            log.warn(defFile.getName()+" does not exists");
        }
    }

    /**
     * jobdefinition.xml
     * @deprecated
     */
    private static synchronized void loadJobDefine() {
        if(lastModifiedJobDefinition == 0)
            initJobDefine();
        if (defFile.exists() && lastModifiedJobDefinition < defFile.lastModified()) {
            lastModifiedJobDefinition = defFile.lastModified();
            try {
                BeanReader beanReader = new BeanReader();
                beanReader.registerBeanClass("JobDefinitions", DefinitionManager.class);
                defs = (DefinitionManager)beanReader.parse(defFile);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * jobdefinition.xml Definiton
     * @deprecated
     */
    private static synchronized void saveJobDefine() {
        try {
            BeanWriter beanWriter = new BeanWriter(new FileWriter(defFile));
            beanWriter.enablePrettyPrint();
            beanWriter.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
            beanWriter.getBindingConfiguration().setMapIDs(false);
            beanWriter.writeXmlDeclaration("<?xml version='1.0' encoding='UTF-8'?>");
            beanWriter.write("JobDefinitions", defs);
            beanWriter.flush();
            beanWriter.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}