package com.aimir.schedule.task;

import org.springframework.core.task.TaskExecutor;

/**
 * 특정 스케줄, 오퍼레이터에 의해 수행되는 Task를 실행하여
 * 특정 리포트를 생성
 * @author goodjob
 *
 */
public class ReportGenerationTask {

	  private class MessagePrinterTask implements Runnable {

		    private String message;

		    public MessagePrinterTask(String message) {
		      this.message = message;
		    }

		    public void run() {
		      System.out.println(message);
		    }

	  }
	  
	  private TaskExecutor taskExecutor;

	  public ReportGenerationTask(TaskExecutor taskExecutor) {
	    this.taskExecutor = taskExecutor;
	  }

	  public void printMessages() {
	    for(int i = 0; i < 25; i++) {
	      taskExecutor.execute(new MessagePrinterTask("Message" + i));
	    }
	  }

		  

}
