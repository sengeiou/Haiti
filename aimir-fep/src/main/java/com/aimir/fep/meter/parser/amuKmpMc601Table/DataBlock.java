package com.aimir.fep.meter.parser.amuKmpMc601Table;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeUtil;


/**
 * KMP MC601 Data Block
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 18. 오전 10:31:46$
 */
public class DataBlock {

	private static Log log 		= LogFactory.getLog(DataBlock.class);
	
	private byte[] rawData 		= null;
	
	private byte[] RID 			= new byte[2];	// Register ID
    private byte[] UNIT 		= new byte[1];	// Register Units
    private byte[] LEN_REGVAL 	= new byte[1];	// Number of bytes
    private byte[] SIGNEXP 		= new byte[1];	// Sign & Exponent
    private byte[] COUNT 		= new byte[1];	// Count
    private byte[] DATA 		= null;			// Data
    
    private int rid 			= 0;			//	
    private int unit			= 0;			//
    private int lenRegVal 		= 0;
    private int count 			= 0;
    private int byteSiEx 		= 0;
    private int signInt 		= 0;
    private int signExp 		= 0;
    private int exp 			= 0;
    private double siEx 		= 0;
    private int pos = 0;
    private ArrayList<Object> regVal 	= null;
    
    /**
     * Constructor
     * @param rawData
     */
    public DataBlock(byte[] rawData){
    	this.rawData = rawData;
    }
    
    /**
     * Constructor .<p>
     * 
     * @param data - read data (header,crch,crcl)
     */
    public DataBlock(byte[] data, int offset) throws Exception {
        byte[] temp = new byte[data.length - offset];
        System.arraycopy(data,offset,temp,0,temp.length);        
        this.rawData = temp;
        parse();
    }
    
    /**
     * Constructor
     * @param data
     * @param offset
     * @param len
     * @throws Exception 
     */
    public DataBlock(byte[] data, int offset, int len) throws Exception{
        byte[] temp = new byte[len];
        System.arraycopy(data,offset,temp,0,len);
        log.debug(" Data Block rawData : " + Hex.decode(temp));
        this.rawData = temp;
        parse();
    }
    
    /**
     * get Register ID
     * @return
     */
    public int getRid() {
		return rid;
	}
    
    /**
     * get Register Description
     * @return
     */
	public String getRidDesc(){
		return RegisterIDTable.getDesc(rid);
	}
	/**
	 * get Unit ID
	 * @return
	 */
	public int getUnit() {
		return unit;
	}

	/**
	 * get Unit Name
	 * @return
	 */
	public String getUnitName(){
		return UnitsTable.getUnitName(unit);
	}
	
	/**
	 * get Number of Bytes
	 * @return
	 */
	public int getLenRegVal() {
		return lenRegVal;
	}

	/**
	 * get Sign & Exponent
	 * @return
	 */
	public int getSignExp() {
		return signExp;
	}
	
	/**
	 * get Sign & Exponent
	 * @return
	 */
	public byte getSIGNEXP(){
		return this.SIGNEXP[0];
	}
	
	public int getLength(){
    	return this.pos;
    }
	  
	/**
	 * get Count
	 * @return
	 */
	public int getCount(){
		return count;
	}
	
	/**
	 * get Register Value
	 * @return
	 */
	public ArrayList<Object> getRegVal() {
		return regVal;
	}

	/**
	 * Data Block Parse
	 * @throws Exception
	 */
	public void parse() throws Exception{
    	
		try{
			
			pos =0;
	    	// Register ID
	    	System.arraycopy(rawData, 0, RID, 0, RID.length);
	    	pos += RID.length;
	    	// Register Unit
	    	System.arraycopy(rawData, pos, UNIT, 0, UNIT.length);
	    	pos += UNIT.length;
	    	// Number of bytes
	    	System.arraycopy(rawData, pos, LEN_REGVAL, 0, LEN_REGVAL.length);
	    	pos += LEN_REGVAL.length;
	    	// Sign & Exponent
	    	System.arraycopy(rawData, pos, SIGNEXP, 0, SIGNEXP.length);
	    	pos += SIGNEXP.length;
	    	// Count
	    	System.arraycopy(rawData, pos, COUNT, 0, COUNT.length);
	    	pos += COUNT.length;
	    	
	    	if(RID[0] == (byte)0xFF || RID[1] == (byte)0xFF || 
	            UNIT[0] == (byte)0xFF || LEN_REGVAL[0] == (byte)0xFF || 
	            SIGNEXP[0] == (byte)0xFF ||	COUNT[0] == (byte)0xFF){
	    		return;
	        }
	    	
	    	rid	 		= DataUtil.getIntToBytes(RID);
	    	unit		= DataUtil.getIntToBytes(UNIT);
	        lenRegVal	= DataUtil.getIntToBytes(LEN_REGVAL);	// get Number of Bytes            
	        byteSiEx	= DataUtil.getIntToBytes(SIGNEXP);
	        count		= DataUtil.getIntToBytes(COUNT);
	        
	        signInt		= (byteSiEx & 128)/128;
	        signExp		= (byteSiEx & 64)/64;
	        exp			= ((byteSiEx&32) + (byteSiEx&16) + (byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
	        siEx		= Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent
	        
	        regVal 		= new ArrayList<Object>();
	        
	        log.debug("RegisterID[ "+Hex.decode(RID)+ " ] : " + rid);
	        log.debug("RegitsetID Name : " + RegisterIDTable.getName(rid));
	        log.debug("RegisterUnit[ "+Hex.decode(UNIT)+ " ] : " + unit);
	        log.debug("RegisterUnitName : " + UnitsTable.getUnitName(unit));
	        log.debug("NumberOfBytes[ "+Hex.decode(LEN_REGVAL)+ " ] : " + lenRegVal);
	        log.debug("Sign&Exponent[ "+Hex.decode(SIGNEXP)+ " ] : " + byteSiEx );
	        log.debug("NumberOfCount[ "+Hex.decode(COUNT)+ " ] : " + count);
	        
	        for(int i= 0; i < count ; i++){
	        	
	        	DATA = new byte[lenRegVal];            
		        System.arraycopy(rawData, pos, DATA, 0, DATA.length);
		        pos += DATA.length;
		        
		        log.debug("Data : " + Hex.decode(DATA));
		        
		        if(	unit == UnitsTable.clock || unit == UnitsTable.date1){
		        	
		        	String regValue = Util.frontAppendNStr('0',Integer.toString(DataUtil.getIntToBytes(DATA)),6);
		        	
		        	if(unit == UnitsTable.date1){
		        		try{
			        		int curryy = (Integer.parseInt(TimeUtil.getCurrentTime().substring(0,4))/100)*100;
			        		int year  = curryy+Integer.parseInt(((String)regValue).substring(0,2));
			        		regVal.add(i, new String(""+year+""+regValue.substring(2)));
		        		}catch(Exception e){
		                    log.error(e);
		                }
		        	}
		        	else {
		        		regVal.add(i, new String(regValue));
		        		log.debug("CLOCK : "+ regValue);
		        	}
		        }else if( unit == UnitsTable.date3) {
		        	regVal.add(i, new String(Util.frontAppendNStr('0',Integer.toString(DataUtil.getIntToBytes(DATA)),4)));
		        }else {
		        	log.debug("## regVal add Data : " + new Double(DataUtil.getIntToBytes(DATA)*siEx));
		        	regVal.add(i, new Double(DataUtil.getIntToBytes(DATA)*siEx));
		        }
	        }
		}catch(Exception e){
			log.error("Data Block Parse Failed", e);
		}
    	
    }
	
	public String toString()
    {
        log.debug(Util.getHexString(rawData));
        StringBuffer sb = new StringBuffer();
        sb.append("RID=["+rid+"]\n");
        sb.append("RID Desc =["+RegisterIDTable.getDesc(rid)+"]\n");
        sb.append("Unit ID =["+unit+"]\n");
        sb.append("UnitName=["+UnitsTable.getUnitName(unit)+"]\n");
        sb.append("Number of Bytes =["+lenRegVal+"]\n");
        sb.append("Sign&Exponent =["+signExp+"]\n");
        sb.append("Count=["+count+"]\n");
        sb.append("RegVal=["+regVal+"]\n");
        return sb.toString();
    }
}


