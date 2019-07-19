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
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Use in conjunction with TopicPublisher to test the performance of ActiveMQ
 * Topics.
 */
public class RealTimeEventProcessor implements MessageListener {

	public void onMessage(Message obj) {

		try {
			if (obj instanceof TextMessage) {
				TextMessage textM = (TextMessage) obj;
				String messageId = textM.getJMSMessageID();
				String message = textM.getText();

				EventMessage.getInstnce().setMessage(message);
				EventMessage.getInstnce().setMessageId(messageId);
			}

			if (obj instanceof MapMessage) {
				Map<String, Object> putMapMessage = new HashMap<String, Object>();
				MapMessage mapMessage = (MapMessage) obj;
				String messageId = mapMessage.getJMSMessageID();

				//System.out.println("RealTimeEventProcessor mapMessage start:"+mapMessage.toString());
				putMapMessage.put("activatorId", mapMessage
						.getString("activatorId"));
				putMapMessage.put("activatorType", mapMessage
						.getString("activatorType"));
				putMapMessage.put("eventAlertName", mapMessage
						.getString("eventAlertName"));
				putMapMessage.put("status", mapMessage.getString("status"));
				putMapMessage.put("severity", mapMessage.getString("severity"));
				putMapMessage.put("openTime", mapMessage.getString("openTime"));
				putMapMessage.put("activatorIp", mapMessage
						.getString("activatorIp"));
				putMapMessage.put("eventOid", mapMessage.getString("eventOid"));
				putMapMessage.put("location", mapMessage.getString("location"));
				putMapMessage.put("closeTime", mapMessage.getString("closeTime"));
				putMapMessage.put("writeTime", mapMessage.getString("writeTime"));
				putMapMessage.put("duration", mapMessage.getString("duration"));
				putMapMessage.put("eventMessage", mapMessage.getString("eventMessage"));

				String message = convertMessage(mapMessage,mapMessage.getString("eventMessage"));

				putMapMessage.put("eventMessage", message);
				
				//System.out.println("RealTimeEventProcessor mapMessage end:"+putMapMessage);
				EventMessage.getInstnce().setMapMessage(putMapMessage);
				EventMessage.getInstnce().setMessageId(messageId);

			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String convertMessage(MapMessage map, String msg) {
	    String pattern = "([$][(][a-zA-Z0-9.]+[)])";
	    try {
    	    RE re = new RE(pattern);
    
    	    String res = "";
    	    int pos = 0;
    
            if(!re.match(msg)){
                return msg;
            }
            while (re.match(msg)) {
                String var = re.getParen(1);
                int idx = msg.indexOf(var);
                String before = msg.substring(0, idx);
    			
                res = res + before;
                int var_len = var.length();
    
                String convertVar = convertParam(map, var.substring(2, var_len - 1));
    		
                res = res + convertVar;
    
                pos = idx + var_len;
                msg = msg.substring(pos);
            }
            if (msg.equals("]")) {
                res = res + msg;
            }
            
            return res;
	    }
	    catch (RESyntaxException e) {}
        
	    return msg;
	}

	public String convertParam(MapMessage map, String msg) {

		int paramIdx = msg.lastIndexOf(".");

		String param = msg.substring(paramIdx + 1, msg.length());
		
		try {
			if (map.getString(param) != null
					&& map.getString(param).length() > 0) {
				return map.getString(param) + "";
			} else {
				return "";
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

}
