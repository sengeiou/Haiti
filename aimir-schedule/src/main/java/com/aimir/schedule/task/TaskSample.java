package com.aimir.schedule.task;

import org.springframework.core.task.TaskExecutor;

public class TaskSample {

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

	  public TaskSample(TaskExecutor taskExecutor) {
	    this.taskExecutor = taskExecutor;
	  }

	  public void printMessages() {
	    for(int i = 0; i < 25; i++) {
	      taskExecutor.execute(new MessagePrinterTask("Message" + i));
	    }
	  }

		  

}
