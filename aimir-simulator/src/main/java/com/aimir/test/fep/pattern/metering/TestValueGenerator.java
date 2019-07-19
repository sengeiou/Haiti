package com.aimir.test.fep.pattern.metering;

import com.aimir.fep.util.Hex;
import com.aimir.test.fep.generator.ValueGenerator;

public class TestValueGenerator {

    public static void main(String[] args)
    {
    	//String value1 = ValueGenerator.randombcdString(1, 20);
    	//System.out.println("value1="+value1+" "+value1.length());
    	//String value2 = ValueGenerator.randombcdString(2, 20);
    	//System.out.println("value2="+value2+" "+value2.length());
    	
    	
    	//long val = ValueGenerator.randomLong(100, 10000, 100001);
    	//System.out.println("val="+val);
    	int MAX_NODE_COUNT = 10;
    	int nodeCount = 135;
    	int cnt = nodeCount/MAX_NODE_COUNT;
    	int res = nodeCount % MAX_NODE_COUNT;
    	
    	System.out.println("cnt="+cnt);
    	System.out.println("res="+res);
    	
        for(int i = 0; i < 10; i++){

            String value = ValueGenerator.randombcdString(i, 8);
            byte[] code = value.getBytes();
            String val = Hex.decode(code)+"000000000000000000000000";
        	System.out.println("value="+val+" "+val.length());
        }
        
        for(int i = 0; i < 300; i++){

            String value = ValueGenerator.randomTextString(i, 5);
            String val = "000B1200750"+value;
        	System.out.println("value="+val+" "+val.length());
        }
        
        //"000B1200750C5FAA";
        
 		String dcuid = ValueGenerator.randombcdString(1, 5);
    	System.out.println("value="+dcuid+" "+dcuid.length());
        
    	
    }
}
