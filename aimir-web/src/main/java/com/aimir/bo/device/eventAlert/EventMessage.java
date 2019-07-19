package com.aimir.bo.device.eventAlert;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Map;

/**
 * Use in conjunction with TopicPublisher to test the performance of ActiveMQ
 * Topics.
 */
public class EventMessage {

    
    private String message="";
    private Map<String,Object> mapMessage =null;
    private String messageId="";

    private static EventMessage INSTANCE = new EventMessage();
    
    public static EventMessage getInstnce(){
    	if(INSTANCE == null ){
    		INSTANCE = new EventMessage();
    	}
    	
    	return INSTANCE;
    }
    private EventMessage(){
    	
    }

    public void setMessage(String message) {
    
        this.message = message;
    }
    
    public void setMapMessage(Map<String,Object> mapMessage) {
    	//System.out.println("mapMessage:"+mapMessage);
        this.mapMessage = mapMessage;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Map<String,Object> getMapMessage() {
        return mapMessage;
    }
    
    public void setMessageId(String messageId) {
    	//System.out.println("messageId:"+messageId);
        this.messageId = messageId;
    }
    
    public String getMessageId() {
        return messageId;
    }

}
