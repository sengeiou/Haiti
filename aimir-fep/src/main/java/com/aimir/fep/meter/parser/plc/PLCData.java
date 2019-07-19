package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author kaze
 * 2009. 6. 23.
 */
public class PLCData extends PLCDataFrame{
    private String ipAddr = "";
    private int totalLength = 0;
    private int uncompressedTotalLength = 0;
    private static Log log = LogFactory.getLog(PLCData.class);
	/**
	 * @param sof
	 * @param protocolDirection
	 * @param protocolVersion
	 * @param dId
	 * @param sId
	 * @param length
	 * @param command
	 */
	public PLCData(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, byte command) {
		super(sof, protocolDirection, protocolVersion, dId, sId, length, command);
	}

	/**
	 * @param sof
	 * @param protocolDirection
	 * @param protocolVersion
	 * @param dId
	 * @param sId
	 * @param length
	 * @param command
	 * @param data
	 */
	public PLCData(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, byte command, byte[] data) {
		super(sof, protocolDirection, protocolVersion, dId, sId, length, command, data);
	}

	public PLCData(PLCDataFrame pdf) {
		super(pdf);
	}

	public PLCData() {
	}

	/**
	 * @return the ipAddr
	 */
	public String getIpAddr() {
		return ipAddr;
	}

	/**
	 * @param ipAddr the ipAddr to set
	 */
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	/**
	 * @return the totalLength
	 */
	public int getTotalLength() {
		return totalLength;
	}

	/**
	 * @param totalLength the totalLength to set
	 */
	public void setTotalLength(int totalLength) {
		this.totalLength = totalLength;
	}

	/**
	 * @return the uncompressedTotalLength
	 */
	public int getUncompressedTotalLength() {
		return uncompressedTotalLength;
	}

	/**
	 * @param uncompressedTotalLength the uncompressedTotalLength to set
	 */
	public void setUncompressedTotalLength(int uncompressedTotalLength) {
		this.uncompressedTotalLength = uncompressedTotalLength;
	}

	public Integer getServiceType() {
		return new Integer(1);
	}
    /**
     * @param pdf
     * @param ipAddr
     * @return PLCData Frame
     * @throws Exception
     */
    public static PLCData decode(PLCDataFrame pdf, String ipAddr) throws Exception
    {
    	try {
	    	//IRM Status Response / IRM Trap
	    	if(pdf.getCommand()==PLCDataConstants.COMMAND_a){
	    		AData aData = new AData(pdf);
	    		aData.setIpAddr(ipAddr);
	    		aData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		aData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return aData;
	    	}
	    	//IRM Status Response / IRM Trap
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_b){
	    		BData bData = new BData(pdf);
	    		bData.setIpAddr(ipAddr);
	    		bData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		bData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return bData;
	    	}
	    	//Response(검침 데이터)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_d){
	    		DData dData = new DData(pdf);
	    		dData.setIpAddr(ipAddr);
	    		dData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		dData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return dData;
	    	}
	    	//Response(최대 수요 데이터)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_e){
	    		EData eData = new EData(pdf);
	    		eData.setIpAddr(ipAddr);
	    		eData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		eData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return eData;
	    	}
	    	//Response(LP 데이터)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_f){
	    		FData fData = new FData(pdf);
	    		fData.setIpAddr(ipAddr);
	    		fData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		fData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return fData;
	    	}
	    	//Response(정전/복전 정보)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_g){
	    		GData gData = new GData(pdf);
	    		gData.setIpAddr(ipAddr);
	    		gData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		gData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return gData;
	    	}
	    	//Response(통신상황 정보)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_h){
	    		HData hData = new HData(pdf);
	    		hData.setIpAddr(ipAddr);
	    		hData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		hData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return hData;
	    	}
	    	//Meter Status Resonse / Meter Trap
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_i){
	    		IData iData = new IData(pdf);
	    		iData.setIpAddr(ipAddr);
	    		iData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		iData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return iData;
	    	}
	    	//Response(변압기 데이터)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_j){
	    		JData jData = new JData(pdf);
	    		jData.setIpAddr(ipAddr);
	    		jData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		jData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return jData;
	    	}
	    	//Trap info.(PLC 통신 오류 Trap)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_k){
	    		KData kData = new KData(pdf);
	    		kData.setIpAddr(ipAddr);
	    		kData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		kData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return kData;
	    	}
	    	//Trap info.(IR 통신 오류 Trap)
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_l){
	    		LData lData = new LData(pdf);
	    		lData.setIpAddr(ipAddr);
	    		lData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		lData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return lData;
	    	}
	    	//변압기 감시 Configuration data response
	    	else if(pdf.getCommand()==PLCDataConstants.COMMAND_m){
	    		MData mData = new MData(pdf);
	    		mData.setIpAddr(ipAddr);
	    		mData.setTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		mData.setUncompressedTotalLength(PLCDataConstants.SOF_LEN+PLCDataConstants.HEADER_LEN+pdf.getData().length+PLCDataConstants.CRC_LEN+PLCDataConstants.EOF_LEN);
	    		return mData;
	    	}
    	}catch (Exception ex) {
    		log.error("PLCData::decode failed : " , ex);
            throw new Exception("PLCData::decode failed :"+ ex.getMessage());
		}
    	return null;
    }

    /**
     * PLCData의 경우 Trap으로 올라오는 데이터를 Process가 처리하기 위해
     * 클래스의 상단이 PLCData인지 체크해서 PLCData이면 PLCData를 타입으로 넘김
     * @return
     */
    public final String getType()
    {
        String type = null;
        Class clazz = this.getClass();
        System.out.println(clazz);
        while(clazz != null)
        {

            String className = clazz.getName();
            int idx = className.lastIndexOf(".");
			type = className.substring(idx+1);
            if(clazz == PLCData.class) {
				break;
			}

            clazz=clazz.getSuperclass();
        }
        return "ServiceData."+type;
    }

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("PLCData ( ")
	        .append(super.toString()).append(TAB)
	        .append("ipAddr = ").append(this.ipAddr).append(TAB)
	        .append("totalLength = ").append(this.totalLength).append(TAB)
	        .append("uncompressedTotalLength = ").append(this.uncompressedTotalLength).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
