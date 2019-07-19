package com.aimir.fep.meter.parser.elsterA1140Table;

import com.aimir.fep.util.Hex;

/**
 * 
 * @author choiEJ
 *
 */
/**
 * @author choiEJ
 *
 */
public class A1140_TEST_DATA {   
	
	private String testData_modem   = "";
	private String testData_meter   = "";
	private String testData_billing = "";
	private String testData_lp      = "";
	private String testData_event   = "";
	
	public A1140_TEST_DATA() {
		setTestData_modem();
		setTestData_meter();
		setTestData_billing();
		setTestData_lp();
		setTestData_event();
	}
	
	public byte[] getTestDataAll() {
		String testData = "";
		
		testData += "4D44";
		testData += "0021";
		testData += testData_modem;
		
		testData += "4D54";
		testData += "0110";
		testData += testData_meter;
		
		testData += "4244";
		testData += "035F";
		testData += testData_billing;
		
		testData += "4C44";
		testData += "02E9";
		testData += testData_lp;
		
		testData += "454C";
		testData += "003B";
		testData += testData_event;
		
		return Hex.encode(testData);
	}

	public byte[] getTestData_modem() {
		String testData = testData_modem;
		return Hex.encode(testData);
	}

	public byte[] getTestData_meter() {
		String testData = testData_meter;
		return Hex.encode(testData);
	}

	public byte[] getTestData_billing() {
		String testData = testData_billing;
		return Hex.encode(testData);
	}

	public byte[] getTestData_lp() {
		String testData = testData_lp;
		return Hex.encode(testData);
	}

	public byte[] getTestData_event() {
		String testData = testData_event;
		return Hex.encode(testData);
	}

	public void setTestData_modem() {
		String testData = "";
		testData += "01";
    	testData += "02";
    	testData += "01";
    	testData += "3435303030313032313134303037340000000000";
    	testData += "07DB051E1E2539";
    	testData += "0D00";
    	testData += "00";
    	
		this.testData_modem = testData;
	}

	public void setTestData_meter() {
		String testData = "";
		testData += "456C737465722D4131313430";     
		testData += "36383530353136350000000000000000";   
		testData += "373908D3470011";  
		testData += "0000005C0000001E000000000000001E";		
		testData += "FFFFFFF5FFFFFFFD00000000FFFFFFFD";		   
		testData += "0000005D0000001E000000000000001E";		  
		testData += "000000020000000000000002";
		testData += "00000257";
		testData += "085800000854"; 	
		testData += "03DD03C7FFFF03E8";
		testData += "FFFFFFCA00FF0000FFFFFFCA";		
		testData += "0000000100000001";		
		this.testData_meter = testData;
	}

	public void setTestData_billing() {
		String testData = "";
		testData += "0030594015010000000000000000000050961549480000000000000000000000000000000000000000000000000000000030594015010000509615494800000000966462630000000000000000000000509921715100000000000000000000000000000000000000000000000000000000000000000000000000000000000000007466401501000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000060000000000000000FF0000000000000000FF0000000000000000FF00000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF0000000000000000000000FF00000000000000";
		
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "2000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000";  
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "0000000000000000";  
//		testData += "0000000000000000"; 
//		testData += "0000000000000000"; 
//		testData += "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"; 
//		testData += "5000000000000000"; 
//		testData += "06"; 
//		testData += "0000000000000000"; 
//		testData += "FF"; 
//		testData += "0000000000000000"; 
//		testData += "FF"; 
//		testData += "0000000000000000"; 
//		testData += "FF";
//		testData += "483E154E"; 
//		testData += "06"; 
//		testData += "50000000000000"; 
//		testData += "00000000"; 
//		testData += "FF"; 
//		testData += "00000000000000"; 
//		testData += "00000000"; 
//		testData += "FF"; 
//		testData += "00000000000000"; 
//		testData += "00000000"; 
//		testData += "FF"; 
//		testData += "00000000000000"; 
//		testData += "00000000"; 
//		testData += "FF";  
//		testData += "00000000000000"; 
//		testData += "00000000"; 
//		testData += "FF"; 
//		testData += "00000000000000"; 
//		testData += "00000000"; 
//		testData += "FF"; 
//		testData += "00000000000000"; 
//		testData += "00000000"; 
//		testData += "FF";  
//		testData += "00000000000000";  
//		testData += "00000000";  
//		testData += "FF";  
//		testData += "00000000000000";  
//		testData += "00000000";  
//		testData += "FF";  
//		testData += "00000000000000";  
//		testData += "00000000";  
//		testData += "FF";  
//		testData += "00000000000000";  
//		testData += "00000000";  
//		testData += "FF";  
//		testData += "00000000000000";  
//		testData += "81";  
//		testData += "493E154E"; 
		
		
		this.testData_billing = testData;
	}

	public void setTestData_lp() {
		String testData = "";
		
		testData +=	"E48097204F004D09008567721818520000008567720058128215093200000058128200524332160112000000524332006584121885020000006584120069109221344200000069109200723392137622000000723392006461521886320000006461520056118219429200000056118200805002164582000000805002007123921858220000007123920068695215527200000068695200135173157632000000135173002304031691320000002304030021359319695200000021359300157233204072000000157233001136031840420000001136030012502323343200000012502300148493205992000000148493001038132212720000001038130012562319024200000012562300799442194212000000799442008489622103620000008489620070936220148200000070936200939332222062000000939332008074222464120000008074220073542224369200000073542200820332246102000000820332009697121988720000009697120069849219888200000069849214505021170781000000505021E8E063214F004D0904042000013800000000042000E8E063214F004D09E6EB63214FE5F063214F06115293345922000000115293021437333768820000001437330211193340998200000011193302136563364562000000136563021692434101520000001692430220853339766200000020853302312253355832000000312253023393933365520000003393930229843334910200000029843302278513297512000000278513023835632610120000003835630227979321928200000027979300266093239842000000266093002980032462020000002980030024257324921200000024257300252643290702000000252643001824132945220000001824130017502328705200000017502300125283272222000000125283E400E9214F004D0900129603250332000000129603001298232925220000001298230098364225062200000098364200125013277992000000125013FF";
		
		
		this.testData_lp = testData;
	}

	public void setTestData_event() {
		String testData = "";

		testData += "0000000000000000000000000000060098E7D14E33CFCF4E07CFCF4E01030300000000000000000000000000000C0070CF1F4F14A9164F9D39154F";
		
		this.testData_event = testData;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("modem=[").append(testData_modem.getBytes().length).append("]\n")
		  .append("meter=[").append(testData_meter.getBytes().length).append("]\n")
		  .append("billing=[").append(testData_billing.getBytes().length).append("]\n")
		  .append("lp=[").append(testData_lp.getBytes().length).append("]\n")
		  .append("event=[").append(testData_event.getBytes().length).append("]\n");
		return sb.toString();
	}
}
