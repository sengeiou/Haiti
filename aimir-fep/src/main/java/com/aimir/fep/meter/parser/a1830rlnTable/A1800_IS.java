package com.aimir.fep.meter.parser.a1830rlnTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class A1800_IS implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6491681835638730968L;
	private Log log = LogFactory.getLog(A1800_IS.class);
	private byte[] rawData = null;
	private byte[] m61 = new byte[19];
	private byte[] m62 = new byte[0];
	private byte[] m63 = new byte[26];
	private byte[] m64 = new byte[0];
	private byte[] m65 = new byte[0];
		
	private MT61 mt61 = null;
	private MT62 mt62 = null;
	private MT63 mt63 = null;
	private MT6465 mt6465 = null;

	private double ins_scale = 0d;
	
	private int INT_NBR_BLKS_SET1 = 0;
	private int INT_NBR_BLKS_INTS_SET1 = 0;
	private int INT_NBR_CHNS_SET1 = 0;
	private int INT_INT_TIME_SET1 = 0;
	private int INT_SET1_NBR_VALID_INT = 0;
	
	private int INT_NBR_BLKS_SET2 = 0;
	private int INT_NBR_BLKS_INTS_SET2 = 0;
	private int INT_NBR_CHNS_SET2 = 0;
	private int INT_INT_TIME_SET2 = 0;
	private int INT_SET2_NBR_VALID_INT = 0;
	
	public A1800_IS(byte[] rawData, double ins_scale) {
        this.rawData = rawData;
        this.ins_scale = ins_scale;
		this.parse();
	}
	
	public void parse() {
		byte[] NBR_BLKS_SET1		= new byte[2];
		byte[] NBR_BLKS_INTS_SET1 	= new byte[2];
		byte[] NBR_CHNS_SET1		= new byte[1];
		byte[] INT_TIME_SET1		= new byte[1];
		byte[] SET1_NBR_VALID_INT	= new byte[2];
		
		byte[] NBR_BLKS_SET2		= new byte[2];
		byte[] NBR_BLKS_INTS_SET2 	= new byte[2];
		byte[] NBR_CHNS_SET2		= new byte[1];
		byte[] INT_TIME_SET2		= new byte[1];
		byte[] SET2_NBR_VALID_INT	= new byte[2];
///////////////// MT61 ///////////////////////////////////////////////		
        int pos = 0;
        System.arraycopy(rawData, pos, m61, 0, m61.length);       
       
		try {
			 ByteArrayInputStream bis = new ByteArrayInputStream(m61);
			 	bis.skip(7);
			 	bis.read(NBR_BLKS_SET1);
			 	bis.read(NBR_BLKS_INTS_SET1);
			 	bis.read(NBR_CHNS_SET1);
			 	bis.read(INT_TIME_SET1);
			 	
			 	bis.read(NBR_BLKS_SET2);
			 	bis.read(NBR_BLKS_INTS_SET2);
			 	bis.read(NBR_CHNS_SET2);
			 	bis.read(INT_TIME_SET2);
			
			 bis.close();
			
			 INT_NBR_BLKS_SET1		= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_SET1) );
			 INT_NBR_BLKS_INTS_SET1	= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_INTS_SET1) );
			 INT_NBR_CHNS_SET1		= DataUtil.getIntToBytes(NBR_CHNS_SET1);
			 INT_INT_TIME_SET1		= DataUtil.getIntToBytes(INT_TIME_SET1);
			 
			 INT_NBR_BLKS_SET2		= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_SET2) );
			 INT_NBR_BLKS_INTS_SET2	= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(NBR_BLKS_INTS_SET2));
			 INT_NBR_CHNS_SET2		= DataUtil.getIntToBytes(NBR_CHNS_SET2);
			 INT_INT_TIME_SET2		= DataUtil.getIntToBytes(INT_TIME_SET2);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        pos += m61.length;        
        this.mt61 = new MT61(m61);//DataUtil.arrayAppend(m61, 0, 7, m61, 0, m61.length));

///////////////// MT62 ///////////////////////////////////////////////
        m62 = new byte[4*mt61.getNBR_CHNS_SET1()+4*mt61.getNBR_CHNS_SET2()];
        System.arraycopy(rawData, pos, m62, 0, m62.length);
        pos += m62.length;
//        this.mt62 = new MT62(m62, INT_NBR_CHNS_SET1, INT_NBR_CHNS_SET2);
        byte[] byteSet1Scalars	= new byte[2];
        byte[] byteSet1Divisors	= new byte[2];
        byte[] byteSet2Scalars	= new byte[2];
        byte[] byteSet2Divisors	= new byte[2];
        int[] set1divisors = new int[INT_NBR_CHNS_SET1];
        int[] set2divisors = new int[INT_NBR_CHNS_SET2];
		
		try {
			ByteArrayInputStream bis62 = new ByteArrayInputStream(m62);
			for (int i = 0; i < INT_NBR_CHNS_SET1; i++){
				bis62.read(byteSet1Scalars);
			}
			for (int i = 0; i < INT_NBR_CHNS_SET1; i++){
				bis62.read(byteSet1Divisors);
				try {
					set1divisors[i] = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(byteSet1Divisors));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (int i = 0; i < INT_NBR_CHNS_SET2; i++){
				bis62.read(byteSet2Scalars);
			}
			for (int i = 0; i < INT_NBR_CHNS_SET2; i++){
				bis62.read(byteSet2Divisors);
				try {
					set2divisors[i] = DataFormat.getIntTo2Byte(DataFormat.LSB2MSB(byteSet2Divisors));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			bis62.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
//        System.arraycopy(rawData, pos, m63, 0, m63.length);
//        pos += m63.length;
//        this.mt63 = new MT63(m63);

///////////////// MT64 ///////////////////////////////////////////////
		int oneblocksize1 = 5 + (INT_NBR_BLKS_INTS_SET1+7)/8 + INT_NBR_BLKS_INTS_SET1*(1+INT_NBR_CHNS_SET1*5/2);
		
        m64 = new byte[oneblocksize1*6];//*INT_NBR_BLKS_SET1];
        pos+=6;
        System.arraycopy(rawData, pos, SET1_NBR_VALID_INT, 0, SET1_NBR_VALID_INT.length);
        
        pos+=SET1_NBR_VALID_INT.length;
        System.arraycopy(rawData, pos, m64, 0, m64.length);

		try {
			INT_SET1_NBR_VALID_INT		= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(SET1_NBR_VALID_INT) );
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        pos += m64.length;

///////////////// MT65 ///////////////////////////////////////////////
		int oneblocksize2 = 5 + (INT_NBR_BLKS_INTS_SET2+7)/8 + INT_NBR_BLKS_INTS_SET2*(1+INT_NBR_CHNS_SET2*5/2);
		pos+=6;
		System.arraycopy(rawData, pos, SET2_NBR_VALID_INT, 0, SET2_NBR_VALID_INT.length);
		pos+=SET2_NBR_VALID_INT.length;
		
		m65 = new byte[oneblocksize2*6];//INT_NBR_BLKS_SET2+8];
		System.arraycopy(rawData, pos, m65, 0, m65.length);

		try {
			INT_SET2_NBR_VALID_INT		= DataFormat.getIntTo2Byte( DataFormat.LSB2MSB(SET2_NBR_VALID_INT) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pos += m65.length;
		
		mt6465 = new MT6465( m64, 
				 INT_NBR_BLKS_SET1,
				 INT_NBR_BLKS_INTS_SET1, 
				 INT_NBR_CHNS_SET1,
				 INT_INT_TIME_SET1,
				 
				 INT_SET1_NBR_VALID_INT,
				 
			     m65, 
			     INT_NBR_BLKS_SET2,
			     INT_NBR_BLKS_INTS_SET2, 
			     INT_NBR_CHNS_SET2,
			     INT_INT_TIME_SET2,
			     
			     INT_SET2_NBR_VALID_INT,
			     
			     set1divisors,
			     set2divisors,
				 ins_scale);
	}
	
	public Instrument[] getInstruments(){
		return this.mt6465.getInstrument();
	}

}
