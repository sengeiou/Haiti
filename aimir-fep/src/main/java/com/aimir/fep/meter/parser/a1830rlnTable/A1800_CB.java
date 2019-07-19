package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;

public class A1800_CB implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4372632601192532184L;
	private Log log = LogFactory.getLog(A1800_CB.class);
	private byte[] rawData = null;
	byte[] s21 = new byte[10];
	byte[] s23 = new byte[0];
	private ST21 st21 = null;
	private ST23 st23 = null;
	
	public A1800_CB(byte[] rawData, double ke, int meterConstantScal) {
        this.rawData = rawData;
		parse(ke, meterConstantScal);
	}
	
	public void parse(double ke, int meterConstantScale) {
		int offset = 0;
        System.arraycopy(rawData,offset,s21,0,s21.length);
        offset += s21.length;
        
        s23 = new byte[rawData.length - offset];
        System.arraycopy(rawData,offset,s23,0,s23.length);
        offset += s23.length;
        this.st21 = new ST21(s21);
		this.st23 = new ST23(s23, st21.getNBR_TIERS(), st21.getNBR_SUMMATIONS(), st21.getNBR_DEMANDS(),st21.getNBR_COINCIDENT(),ke,meterConstantScale);
	}
	
	public byte[] getTOUBlk() throws Exception{
		return this.st23.parseTOUBlk();
	}
	public BillingData getBillingData(){
		
		BillingData bill = new BillingData();


		try {
			
			 String time = "";
	         java.util.Calendar cal = java.util.Calendar.getInstance();
	         java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
	         time = sdf.format(cal.getTime());
	         
			bill.setBillingTimestamp(time);
			
			bill.setActiveEnergyRateTotal(st23.getAWHTOT());
			bill.setActiveEnergyRate1(st23.getARATE1WH());
			bill.setActiveEnergyRate2(st23.getARATE2WH());
			bill.setActiveEnergyRate3(st23.getARATE3WH());
			
			bill.setReactiveEnergyRateTotal(st23.getRWHTOT());		
			bill.setReactiveEnergyRate1(st23.getRRATE1WH());
			bill.setReactiveEnergyRate2(st23.getRRATE2WH());
			bill.setReactiveEnergyRate3(st23.getRRATE3WH());

			bill.setActivePowerMaxDemandRate1(st23.getARATE1MAXW());
			bill.setActivePowerMaxDemandRate2(st23.getARATE2MAXW());
			bill.setActivePowerMaxDemandRate3(st23.getARATE3MAXW());
			
			bill.setActivePowerDemandMaxTimeRate1(st23.getARATE1MAXWTIME());
			bill.setActivePowerDemandMaxTimeRate2(st23.getARATE2MAXWTIME());
			bill.setActivePowerDemandMaxTimeRate3(st23.getARATE3MAXWTIME());

			bill.setReactivePowerMaxDemandRate1(st23.getRRATE1MAXW());
			bill.setReactivePowerMaxDemandRate2(st23.getRRATE2MAXW());
			bill.setReactivePowerMaxDemandRate3(st23.getRRATE3MAXW());
			
			bill.setReactivePowerDemandMaxTimeRate1(st23.getRRATE1MAXWTIME());
			bill.setReactivePowerDemandMaxTimeRate2(st23.getRRATE2MAXWTIME());
			bill.setReactivePowerDemandMaxTimeRate3(st23.getRRATE3MAXWTIME());
			
			bill.setCumulativeActivePowerDemandRate1(st23.getARATE1CUMW());
			bill.setCumulativeActivePowerDemandRate2(st23.getARATE2CUMW());
			bill.setCumulativeActivePowerDemandRate3(st23.getARATE3CUMW());
			
			bill.setCumulativeReactivePowerDemandRate1(st23.getRRATE1CUMW());
			bill.setCumulativeReactivePowerDemandRate2(st23.getRRATE2CUMW());
			bill.setCumulativeReactivePowerDemandRate3(st23.getRRATE3CUMW());
			
		} catch (Exception e) {
			e.printStackTrace();
			bill = null;
		}


		return bill;
	}
}
