package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataBlock implements java.io.Serializable{
	private static Log log = LogFactory.getLog(DataBlock.class);
	private String dataName="";
	private String dataType="";
	private int dataLength=0;
	private String dataUnit="";
	private double dataValue=0;
	private double dataMultiplier=1;
	private String functionField="";
	private boolean isMBusFormat=false;
	private boolean isEmpty=false;

	public DataBlock(){
		isEmpty=true;
	}
	public boolean isEmpty() {
		return isEmpty;
	}
	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	/**
	 * @param controlInformation
	 * @param DIF
	 * @param VIF
	 * @param DATA
	 * @param dataName
	 */
	public DataBlock(ControlInformation controlInformation, byte[] DIF, byte[] VIF, byte[] DATA, String dataName) {
		DIF dif = new DIF(DIF);
		VIF vif = new VIF(VIF);
		Data data = new Data(DATA,controlInformation, dif, vif);

		functionField=dif.getFunctionDescr();
		dataType=dif.getDataType();
		dataLength=dif.getDataLength();
		dataUnit=vif.getUnit();
		dataMultiplier=vif.getMultiplier();
		dataValue=data.getValue();
		this.dataName=dataName;
		isMBusFormat=true;
	}

	/**
	 * @param controlInformation
	 * @param DATA
	 * @param dataName
	 */
	public DataBlock(ControlInformation controlInformation, byte[] DATA, String dataName){
		Data data = new Data(DATA,controlInformation);

		dataType="BCD";
		dataLength=DATA.length;
		dataMultiplier=1;
		dataValue=data.getValue();
		this.dataName=dataName;
		isMBusFormat=false;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public double getDataValue() {
		return dataValue;
	}

	public void setDataValue(double dataValue) {
		this.dataValue = dataValue;
	}

	public double getDataMultiplier() {
		return dataMultiplier;
	}

	public void setDataMultiplier(double dataMultiplier) {
		this.dataMultiplier = dataMultiplier;
	}

	public String getFunctionField() {
		return functionField;
	}

	public void setFunctionField(String functionField) {
		this.functionField = functionField;
	}



}