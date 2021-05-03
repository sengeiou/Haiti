package com.aimir.service.system.SMS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Test {
   public static void main(String[] args) {
      String URL = "http://api.atompark.com/members/sms/xml.php";
      String login="haitiabd@gmail.com";
      String password="Abd@8438";
      
      RequestBuilder Request= new RequestBuilder(URL);
      API ApiSms=new API(Request, login, password);
      
      //GET STATUS *************************************************************
      System.out.println(ApiSms.getStatus("1299"));
      /*
       * response: <?xml version="1.0" encoding="UTF-8"?>
            <deliveryreport><message id="1299" sentdate="0000-00-00 00:00:00" donedate="0000-00-00 00:00:00" status="0" /></deliveryreport>
      */
      
      //GET PRICE *************************************************************
      Map<String, String>  phones = new HashMap();
      phones.put("id1", "+821091883082");
//      phones.put("id2", "+38093101****");
      System.out.println(ApiSms.getPrice("Test sms",phones));
      /*
       * response: <?xml version="1.0" encoding="UTF-8"?><RESPONSE><status>0</status><credits>0.682</credits></RESPONSE>
      */
      
      //GET BALANCE *************************************************************
      System.out.println(ApiSms.getBalance());
      /*
       * response: <?xml version="1.0" encoding="UTF-8"?><RESPONSE><status>0</status><credits>780.64</credits></RESPONSE>
      */
      
      //SEND PHONE *************************************************************
      ArrayList<Phones> phoneSend=new ArrayList<Phones>();
      phoneSend.add(new Phones("id1","","+821091883082"));
//      phoneSend.add(new Phones("id2","","+821095777131"));
//      phoneSend.add(new Phones("id2","","+38093101****"));
      System.out.println(ApiSms.sendSms("Haiti", "Testing send SMS", phoneSend));
      /*
       * response: <?xml version="1.0" encoding="UTF-8"?><RESPONSE><status>2</status><credits>0.682</credits></RESPONSE>
      */
   }    
}