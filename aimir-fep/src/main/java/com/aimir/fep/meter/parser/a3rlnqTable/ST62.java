package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

public class ST62 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1127352220168275268L;

	private final int LEN_LP_SEL_SET1 = 3;
	private final int LEN_INT_FMT_CDE1 = 1;
	private final int LEN_SCALARS_SET1 = 2;
	private final int LEN_DIVISOR_SET1 = 2;

	private byte[] data;
	private byte[] SCALARS;
	private byte[] DIVISORS;

	private Log logger = LogFactory.getLog(getClass());
	private int channels;

	/**
	 * Constructor .
	 * <p>
	 * 
	 * @param data
	 *            - read data (header,crch,crcl)
	 */
	public ST62() {
	}

	public ST62(byte[] data, int channels) {
		this.data = data;
		this.channels = channels;
	}

	public int[] getSourceID() {

		int[] srcid = new int[this.channels];

		int offset = 0;

		try {
			for (int i = 0; i < this.channels; i++) {
				byte[] temp;
				temp = DataFormat.select(data, offset, LEN_LP_SEL_SET1);
				offset += LEN_LP_SEL_SET1;
				srcid[i] = temp[1] & 0xFF;// source_select
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return srcid;

	}

	public int getSourceID(int chanid) {

		int[] srcid = new int[this.channels];

		int offset = 0;

		try {
			for (int i = 0; i < this.channels; i++) {
				byte[] temp;
				temp = DataFormat.select(data, offset, LEN_LP_SEL_SET1);
				offset += LEN_LP_SEL_SET1;
				srcid[i] = temp[1] & 0xFF;// source_select
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		return srcid[chanid];

	}

	public int getSCALAR(int chanid) throws Exception {

		int offset = LEN_LP_SEL_SET1 * this.channels + LEN_SCALARS_SET1
				* chanid;

		return DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(
				data, offset, LEN_SCALARS_SET1)));

	}

	public int getDIVISOR(int chanid) throws Exception {

		int offset = LEN_LP_SEL_SET1 * this.channels + LEN_SCALARS_SET1
				* this.channels + LEN_DIVISOR_SET1 * chanid;

		return DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(
				data, offset, LEN_DIVISOR_SET1)));

	}

	public byte[] parseSCALAR(int chanid) throws Exception {

		int offset = LEN_LP_SEL_SET1 * this.channels + LEN_SCALARS_SET1
				* chanid;

		return DataFormat.dec2hex((char) (DataFormat.hex2unsigned16(DataFormat
				.LSB2MSB(DataFormat.select(data, offset, LEN_SCALARS_SET1)))));

	}

	public byte[] parseDIVISOR(int chanid) throws Exception {

		int offset = LEN_LP_SEL_SET1 * this.channels + LEN_SCALARS_SET1
				* this.channels + LEN_DIVISOR_SET1 * chanid;

		return DataFormat.dec2hex((char) (DataFormat.hex2unsigned16(DataFormat
				.LSB2MSB(DataFormat.select(data, offset, LEN_DIVISOR_SET1)))));

	}

	public void setSCALARst64(byte[] Scalar) throws Exception {

		this.SCALARS = Scalar;

	}

	public void setDIVISORst64(byte[] Divisor) throws Exception {

		this.DIVISORS = Divisor;

	}

	public int getSCALARst64(int chanid) throws Exception {

		int offset = LEN_SCALARS_SET1 * (chanid - 1);

		return DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(
				this.SCALARS, offset, LEN_SCALARS_SET1)));

	}

	public int getDIVISORst64(int chanid) throws Exception {

		int offset = LEN_DIVISOR_SET1 * (chanid - 1);

		return DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(
				this.DIVISORS, offset, LEN_DIVISOR_SET1)));

	}

}