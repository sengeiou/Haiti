package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.protocol.mrp.protocol.MX2_DataConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * Billing Data<br>
 * 미터에서 받은 데이터를 BillingData로 파싱하는 역할이다.<br>
 * 
 * @author kskim
 * @see CommonTable
 * @see <참고문서><br>
 *      <ul>
 *      <li>MX2_AMR_Communication_Specification-2011-06-16_Signed.pdf</li>
 *      <li>NAMR_P213GP(2011)_Protocol.doc</li>
 *      </ul>
 */
@SuppressWarnings("serial")
public class MX2BillingData extends CommonTable {
	private static Log log = LogFactory.getLog(MX2BillingData.class);

	/**
	 * DB와 매칭되는 Billing Data 구조체
	 */
	private BillingData billingData;
	
	private BillingData currentBillingData;

	private double multiplier;
	
	private String errorCode;

	// //////////////////////////////////////////////////

	public final int LEN_BILLING_DATE = 6;
	public final int LEN_PRESENTENERGY = 4;
	public final int LEN_PREVIOUSMAXDM = 3;
	public final int LEN_PREVIOUSENERGY = 4;
	// public final int LEN_PREVIOUSCUMDM = 3;
	public final int LEN_MULTIPLIER = 2;
	public final int LEN_ERRORCODE = 9;
	public final int CNT_RATE = 4; // Maximum 4
	
	private byte[] presentEnergy;
	private byte[] previousEnergy;
	private byte[] previousDemand;

	/**
	 * 저장할 Billing data 개수를 설정하여 생성한다.
	 * @param data
	 *            저장된 값에 Multiplier 곱해야 원래 값이 된다.
	 */
	public MX2BillingData(byte[] data) {
		this.billingData = new BillingData();
		currentBillingData = new BillingData();
		parseBillingData(data);
	}

	public MX2BillingData() {
	}

	/**
	 * Multiplier 값을 설정한다.<br>
	 * billing data 에 곱하여 원래 값으로 만드는 역할을 한다.<br>
	 * 곱해야 할 수를 설정 한다.<br>
	 * 
	 * @param multiplier
	 */
	private void parseBillingData(byte[] data) {

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		try {
			// multiplier 위치로 스킵한다.
			bis.skip(data.length - 11);

			// Multiplier
			byte[] bMultiplier = new byte[2];
			bis.read(bMultiplier);
			this.multiplier = getMultiplier(bMultiplier);
			log.info("MULTIPLIER[" + multiplier + "]");
			
			byte[] bErrorCode = new byte[9]; //ascii
			bis.read(bErrorCode);
			this.errorCode = new String(bErrorCode);

			// 스트림 리셋
			bis.reset();

			// Billing Date & Time
			byte[] date = new byte[6];
			bis.read(date);
			setBillingDate(date);

			// Present Energy
			byte[] presentEnergy = new byte[(LEN_PRESENTENERGY * CNT_RATE)
					* MX2_DataConstants.CNT_CHANNEL];
			bis.read(presentEnergy);
			log.debug("presentEnergy : " + Hex.getHexDump(presentEnergy));
			setPresentEnergy(presentEnergy);
			
			

			// Previous Max DM
			// 각 3byte 의 데이터가 total Rate1~3 (12byte) 까지 있고 w(imp), w(exp),
			// w(imp+exp), q1~4 까지 7개의 종류가 있다.
			byte[] previousMaxDM = new byte[(LEN_PREVIOUSMAXDM * CNT_RATE)
					* MX2_DataConstants.CNT_CHANNEL];
			bis.read(previousMaxDM);
			log.debug("previousMaxDM : " + Hex.getHexDump(previousMaxDM));
			setPreviousMaxDemand(previousMaxDM);

			// PreviousEnergy
			byte[] previousEnergy = new byte[(LEN_PREVIOUSENERGY * CNT_RATE)
					* MX2_DataConstants.CNT_CHANNEL];
			bis.read(previousEnergy);
			log.debug("previousEnergy : " + Hex.getHexDump(previousEnergy));
			setPreviousEnergy(previousEnergy);

		} catch (Exception e) {
			log.debug(e);
		}
	}
	
	private void setPresentEnergy(byte[] data) {
	    this.presentEnergy = data;
	}

	/**
	 * Current Billing Data <br>
	 * 데이터 형식은  privouse Energy 와 같지만 최근 데이터가 들어있다.
	 * @param presentEnergy
	 */
	public void setPresentEnergy(double unit, double decimal) {
		ByteArrayInputStream bis = new ByteArrayInputStream(presentEnergy);
		try {
			byte[] presentEnergyRateData = new byte[LEN_PREVIOUSENERGY
					* CNT_RATE];

			//saver 에서 이 값을 가져가 yymmdd 컬럼에 저장한다. 현재 날짜로 지정한다
			String writeDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			this.currentBillingData.setActivePwrDmdMaxTimeImportRate1(writeDate);
			this.currentBillingData.setWriteDate(writeDate);
			
			// ImportRate
			String[] activeImpMethods = new String[] {
					"setActiveEnergyImportRateTotal",
					"setActiveEnergyImportRate1", "setActiveEnergyImportRate2",
					"setActiveEnergyImportRate3" };
			bis.read(presentEnergyRateData);
			log.debug("ImportRate : " + Hex.getHexDump(presentEnergyRateData));
			Double[] importValues = invokeSetterMethods(activeImpMethods,
					this.currentBillingData, Double.class, presentEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// Export rate
			String[] activeExpMethods = new String[] {
					"setActiveEnergyExportRateTotal",
					"setActiveEnergyExportRate1", "setActiveEnergyExportRate2",
					"setActiveEnergyExportRate3" };
			bis.read(presentEnergyRateData);
			Double[] exportValues = invokeSetterMethods(activeExpMethods,
					this.currentBillingData, Double.class, presentEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// import + export
			String[] activeImpPluseExpMethods = new String[] {
					"setActiveEnergyRateTotal", "setActiveEnergyRate1",
					"setActiveEnergyRate2", "setActiveEnergyRate3" };
			Double[] sumImpExp = sumDoubleArrays(importValues, exportValues);
			invokeSetterMethods(activeImpPluseExpMethods, this.currentBillingData,
					Double.class, sumImpExp);

			// q1
			String[] Q1Methods = new String[] {
					"setReactiveEnergyLagImportRateTotal",
					"setReactiveEnergyLagImportRate1",
					"setReactiveEnergyLagImportRate2",
					"setReactiveEnergyLagImportRate3" };
			bis.read(presentEnergyRateData);
			Double[] Q1Values = invokeSetterMethods(Q1Methods,
					this.currentBillingData, Double.class, presentEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q2
			String[] Q2Methods = new String[] {
					"setReactiveEnergyLeadExportRateTotal",
					"setReactiveEnergyLeadExportRate1",
					"setReactiveEnergyLeadExportRate2",
					"setReactiveEnergyLeadExportRate3" };
			bis.read(presentEnergyRateData);
			Double[] Q2Values = invokeSetterMethods(Q2Methods,
					this.currentBillingData, Double.class, presentEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q3
			String[] Q3Methods = new String[] {
					"setReactiveEnergyLagExportRateTotal",
					"setReactiveEnergyLagExportRate1",
					"setReactiveEnergyLagExportRate2",
					"setReactiveEnergyLagExportRate3" };
			bis.read(presentEnergyRateData);
			Double[] Q3Values = invokeSetterMethods(Q3Methods,
					this.currentBillingData, Double.class, presentEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q4
			String[] Q4Methods = new String[] {
					"setReactiveEnergyLeadImportRateTotal",
					"setReactiveEnergyLeadImportRate1",
					"setReactiveEnergyLeadImportRate2",
					"setReactiveEnergyLeadImportRate3" };
			bis.read(presentEnergyRateData);
			Double[] Q4Values = invokeSetterMethods(Q4Methods,
					this.currentBillingData, Double.class, presentEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q1+q2+q3+q4
			String[] QsumMethods = new String[] { "setReactiveEnergyRateTotal",
					"setReactiveEnergyRate1", "setReactiveEnergyRate2",
					"setReactiveEnergyRate3" };
			Double[] Q12Values = sumDoubleArrays(Q1Values, Q2Values);
			Double[] Q34Values = sumDoubleArrays(Q3Values, Q4Values);
			Double[] QValues = sumDoubleArrays(Q12Values, Q34Values);
			invokeSetterMethods(QsumMethods, this.currentBillingData, Double.class,
					QValues);

		} catch (Exception e) {
			log.debug(e);
		}
	}

	/**
	 * Multiplier 를 구한다.
	 * 
	 * @param multiplier
	 * @return
	 */
	private double getMultiplier(byte[] multiplier) {
	    log.debug("MUTIPLIER_RAW[" + Hex.decode(multiplier) + "]");
		// Exponent : 0 ~ 3 (bit 3 ~ 0), 비트 3번째부터 0번째까지
	    /*
		byte exponent = multiplier[1];

		// bit 3 ~ 0 까지가 exponent 이다. 0x0f 와 AND 연산을 하면 7 ~ 4 까지의 bit 가 0으로 채워져
		// bit 3 ~ 0 까지 값을 구할수 있다.
		exponent &= 0x0F;

		int nExponent = (int) exponent;

		// significant 001h ~ 999h (bit15 ~ 4)
		byte[] significant = multiplier;
		// significant 를 구하기 위해서는 exponent 비트만큼 전체 비트를 오른쪽으로 시프트 하여 int를 구한다.
		significant[1] = (byte) ((significant[1] >> 4) | (significant[0] << 4));
		significant[0] = (byte) (significant[0] >> 4);

		int nSignificant = byteArrayToInt(significant);

		// 구하는 식.
		return nSignificant * Math.pow(10, nExponent);
		*/
	    String s = Hex.decode(multiplier);
	    int exponent = Integer.parseInt(s.substring(3));
	    int significant = Integer.parseInt(s.substring(0, 3));
	    return significant * Math.pow(10, exponent);
	}

	/**
	 * 값에 Multiplier 를 곱한다.
	 * 20120927 : 멀티플라이어는 무시하고 킬로 단위로 바꿔야한다.
	 * 
	 * @param baValue
	 * @return baValue * Multiplier
	 */
	public Double getMultiplierValue(byte[] baValue, double unit, double decimal) {
		String bcdCode = DataUtil.getBCDtoBytes(baValue);
		//log.debug("BCD : " + Hex.getHexDump(baValue));
		int nValue = Integer.parseInt(bcdCode);
		//log.debug("BCD => int : " + nValue);
		//return (nValue * this.multiplier);
		
		//FIXME : 지금은 일단 2digit 으로 고정이지만 나중에는 DECIMAL PLACE 에서 읽어 처리해야한다. 
		return (double) (nValue) * unit * decimal;
	}

	/**
	 * 설정된 byte array 는 자동으로 Billing Date 로 파싱된다.
	 * 
	 * @param billingDate
	 *            {@link #BillingDate}
	 */
	public void setBillingDate(byte[] billingDate) {
		String strDate = getYyyyMMddHHmmss(billingDate);
		this.billingData.setWriteDate(strDate);
		this.billingData.setBillingTimestamp(strDate);
		this.billingData.setActivePwrDmdMaxTimeImportRate1(strDate);
		log.debug("billingDate : " + this.billingData.getWriteDate());
	}

	
	
	/**
	 * Write Date 설정.
	 * 
	 * @param billingDate
	 */
	public void setBillingDate(String billingDate) {
		this.billingData.setWriteDate(billingDate);
	}

	/**
	 * billing date 값을 yyyyMMddHHmmss 포멧으로 변환한다.
	 * 
	 * @param date
	 *            변환할 billing date
	 * @return yyyyMMddHHmmss 포멧
	 */
	public String getYyyyMMddHHmmss(byte[] date) {
		ByteArrayInputStream bis = new ByteArrayInputStream(date);
		int YY = 0;
		int MM = 0;
		int DD = 0;
		int hh = 0;
		int mm = 0;
		int ss = 0;
		try {
			YY = bis.read();
			MM = bis.read();
			DD = bis.read();
			hh = bis.read();
			mm = bis.read();
			ss = bis.read();
			bis.close();
		} catch (IOException e) {
			return null;
		}
		

		String dateString = String
				.format("%02x%02x%02x%02x%02x%02x", YY, MM, DD, hh, mm, ss);
		
		return convertDateFormat(dateString,"yyMMddHHmmss","yyyyMMddHHmmss");
	}
	
	private void setPreviousEnergy(byte[] data) {
	    this.previousEnergy = data;
	}
	
	/**
	 * previousEnergy 데이터를 BillingData에 파싱하는 작업.
	 * 
	 * @param previousEnergy
	 * @see 0 = Active import <br>
	 *      1 = Active export <br>
	 *      (import + export) <br>
	 *      2 = Reactive Import Lagging(Q1) <br>
	 *      3 = Reactive Export Lagging(Q3) <br>
	 *      4 = Reactive Import Leading(Q4) <br>
	 *      5 = Reactive Export Leading(Q2) <br>
	 *      (q1 + q2 + q3 + q4)<br>
	 */
	public void setPreviousEnergy(double unit, double decimal) {
		ByteArrayInputStream bis = new ByteArrayInputStream(previousEnergy);
		try {
			byte[] previousEnergyRateData = new byte[LEN_PREVIOUSENERGY
					* CNT_RATE];

			// ImportRate
			String[] activeImpMethods = new String[] {
					"setActiveEnergyImportRateTotal",
					"setActiveEnergyImportRate1", "setActiveEnergyImportRate2",
					"setActiveEnergyImportRate3" };
			bis.read(previousEnergyRateData);
			Double[] importValues = invokeSetterMethods(activeImpMethods,
					this.billingData, Double.class, previousEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// Export rate
			String[] activeExpMethods = new String[] {
					"setActiveEnergyExportRateTotal",
					"setActiveEnergyExportRate1", "setActiveEnergyExportRate2",
					"setActiveEnergyExportRate3" };
			bis.read(previousEnergyRateData);
			Double[] exportValues = invokeSetterMethods(activeExpMethods,
					this.billingData, Double.class, previousEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// import + export
			String[] activeImpPluseExpMethods = new String[] {
					"setActiveEnergyRateTotal", "setActiveEnergyRate1",
					"setActiveEnergyRate2", "setActiveEnergyRate3" };
			Double[] sumImpExp = sumDoubleArrays(importValues, exportValues);
			invokeSetterMethods(activeImpPluseExpMethods, this.billingData,
					Double.class, sumImpExp);

			// q1
			String[] Q1Methods = new String[] {
					"setReactiveEnergyLagImportRateTotal",
					"setReactiveEnergyLagImportRate1",
					"setReactiveEnergyLagImportRate2",
					"setReactiveEnergyLagImportRate3" };
			bis.read(previousEnergyRateData);
			Double[] Q1Values = invokeSetterMethods(Q1Methods,
					this.billingData, Double.class, previousEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q2
			String[] Q2Methods = new String[] {
					"setReactiveEnergyLeadExportRateTotal",
					"setReactiveEnergyLeadExportRate1",
					"setReactiveEnergyLeadExportRate2",
					"setReactiveEnergyLeadExportRate3" };
			bis.read(previousEnergyRateData);
			Double[] Q2Values = invokeSetterMethods(Q2Methods,
					this.billingData, Double.class, previousEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q3
			String[] Q3Methods = new String[] {
					"setReactiveEnergyLagExportRateTotal",
					"setReactiveEnergyLagExportRate1",
					"setReactiveEnergyLagExportRate2",
					"setReactiveEnergyLagExportRate3" };
			bis.read(previousEnergyRateData);
			Double[] Q3Values = invokeSetterMethods(Q3Methods,
					this.billingData, Double.class, previousEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q4
			String[] Q4Methods = new String[] {
					"setReactiveEnergyLeadImportRateTotal",
					"setReactiveEnergyLeadImportRate1",
					"setReactiveEnergyLeadImportRate2",
					"setReactiveEnergyLeadImportRate3" };
			bis.read(previousEnergyRateData);
			Double[] Q4Values = invokeSetterMethods(Q4Methods,
					this.billingData, Double.class, previousEnergyRateData,
					LEN_PREVIOUSENERGY, unit, decimal);

			// q1+q2+q3+q4
			String[] QsumMethods = new String[] { "setReactiveEnergyRateTotal",
					"setReactiveEnergyRate1", "setReactiveEnergyRate2",
					"setReactiveEnergyRate3" };
			Double[] Q12Values = sumDoubleArrays(Q1Values, Q2Values);
			Double[] Q34Values = sumDoubleArrays(Q3Values, Q4Values);
			Double[] QValues = sumDoubleArrays(Q12Values, Q34Values);
			invokeSetterMethods(QsumMethods, this.billingData, Double.class,
					QValues);

		} catch (Exception e) {
			log.debug(e);
		}
	}

	private void setPreviousMaxDemand(byte[] data) {
	    this.previousDemand = data;
	}
	
	/**
	 * PreviousMaxDemand 데이터를 BillingData에 파싱하는 작업.
	 * 
	 * @param maxDemand
	 * @see 0 = Active import <br>
	 *      1 = Active export <br>
	 *      (import + export) <br>
	 *      2 = Reactive Import Lagging(Q1) <br>
	 *      3 = Reactive Export Lagging(Q3) <br>
	 *      4 = Reactive Import Leading(Q4) <br>
	 *      5 = Reactive Export Leading(Q2) <br>
	 *      (q1 + q2 + q3 + q4)<br>
	 */
	public void setPreviousMaxDemand(double unit, double decimal) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(previousDemand);

			byte[] maxDemandRate = new byte[LEN_PREVIOUSMAXDM * CNT_RATE];

			// ImportRate
			String[] activeImpMethods = new String[] {
					"setActivePwrDmdMaxImportRateTotal",
					"setActivePwrDmdMaxImportRate1",
					"setActivePwrDmdMaxImportRate2",
					"setActivePwrDmdMaxImportRate3" };
			bis.read(maxDemandRate);
			Double[] importValues = invokeSetterMethods(activeImpMethods,
					this.billingData, Double.class, maxDemandRate,
					LEN_PREVIOUSMAXDM, unit, decimal);

			// Export rate
			String[] activeExpMethods = new String[] {
					"setActivePwrDmdMaxExportRateTotal",
					"setActivePwrDmdMaxExportRate1",
					"setActivePwrDmdMaxExportRate2",
					"setActivePwrDmdMaxExportRate3" };
			bis.read(maxDemandRate);
			Double[] exportValues = invokeSetterMethods(activeExpMethods,
					this.billingData, Double.class, maxDemandRate,
					LEN_PREVIOUSMAXDM, unit, decimal);

			// import + export
			String[] activeImpPluseExpMethods = new String[] {
					"setActivePowerMaxDemandRateTotal",
					"setActivePowerMaxDemandRate1",
					"setActivePowerMaxDemandRate2",
					"setActivePowerMaxDemandRate3" };
			Double[] sumImpExp = sumDoubleArrays(importValues, exportValues);
			invokeSetterMethods(activeImpPluseExpMethods, this.billingData,
					Double.class, sumImpExp);

			// q1
			String[] Q1Methods = new String[] {
					"setReactivePwrDmdMaxLagImportRateTotal",
					"setReactivePwrDmdMaxLagImportRate1",
					"setReactivePwrDmdMaxLagImportRate2",
					"setReactivePwrDmdMaxLagImportRate3" };
			bis.read(maxDemandRate);
			Double[] Q1Values = invokeSetterMethods(Q1Methods,
					this.billingData, Double.class, maxDemandRate,
					LEN_PREVIOUSMAXDM, unit, decimal);

			// q2
			String[] Q2Methods = new String[] {
					"setReactivePwrDmdMaxLeadExportRateTotal",
					"setReactivePwrDmdMaxLeadExportRate1",
					"setReactivePwrDmdMaxLeadExportRate2",
					"setReactivePwrDmdMaxLeadExportRate3" };
			bis.read(maxDemandRate);
			Double[] Q2Values = invokeSetterMethods(Q2Methods,
					this.billingData, Double.class, maxDemandRate,
					LEN_PREVIOUSMAXDM, unit, decimal);

			// q3
			String[] Q3Methods = new String[] {
					"setReactivePwrDmdMaxLagExportRateTotal",
					"setReactivePwrDmdMaxLagExportRate1",
					"setReactivePwrDmdMaxLagExportRate2",
					"setReactivePwrDmdMaxLagExportRate3" };
			bis.read(maxDemandRate);
			Double[] Q3Values = invokeSetterMethods(Q3Methods,
					this.billingData, Double.class, maxDemandRate,
					LEN_PREVIOUSMAXDM, unit, decimal);

			// q4
			String[] Q4Methods = new String[] {
					"setReactivePwrDmdMaxLeadImportRateTotal",
					"setReactivePwrDmdMaxLeadImportRate1",
					"setReactivePwrDmdMaxLeadImportRate2",
					"setReactivePwrDmdMaxLeadImportRate3" };
			bis.read(maxDemandRate);
			Double[] Q4Values = invokeSetterMethods(Q4Methods,
					this.billingData, Double.class, maxDemandRate,
					LEN_PREVIOUSMAXDM, unit, decimal);

			// q1+q2+q3+q4
			String[] QsumMethods = new String[] {
					"setReactivePowerMaxDemandRateTotal",
					"setReactivePowerMaxDemandRate1",
					"setReactivePowerMaxDemandRate2",
					"setReactivePowerMaxDemandRate3" };
			Double[] Q12Values = sumDoubleArrays(Q1Values, Q2Values);
			Double[] Q34Values = sumDoubleArrays(Q3Values, Q4Values);
			Double[] QValues = sumDoubleArrays(Q12Values, Q34Values);
			invokeSetterMethods(QsumMethods, this.billingData, Double.class,
					QValues);

		} catch (Exception e) {
			log.debug(e);
		}

	}

	/**
	 * 두 Double Array의 각각 요소의 합을 구한다.
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	private Double[] sumDoubleArrays(Double[] value1, Double[] value2) {
		Double[] value = new Double[value1.length];
		for (int i = 0; i < value1.length; i++) {
			value[i] = value1[i] + value2[i];
		}
		return value;
	}

	/**
	 * 1개 이상의 Setter 메소드 들을 일괄 처리하기위해 만들어졌다.<br>
	 * byte array 에서 BCD 값을 읽어와 Double 로 변환하며,<br>
	 * 변환 과정에서 Multiplier 값이 적용된다.<br>
	 * 변환된 모든 값들은 Setter 메소드를 호출하여 값을 설정하고<br>
	 * 값들은 다시 호출된곳으로 리턴한다.<br>
	 * 
	 * @param methodsName
	 *            호출할 메소드 들의 목록.
	 * @param obj
	 *            메소드가 있는 클래스 Object.
	 * @param paramType
	 *            Setter 메소드의 파라미터 타입.
	 * @param data
	 *            Setter 메소드에 일괄 적용될 데이터 Array.
	 * @param dataPieceLength
	 *            데이터 Array 의 각각의 값을 추출하기위한 1개의 값의 길이.
	 * @return 일괄 적용된 파라미터 값들이다.
	 * @throws Exception
	 */
	public Double[] invokeSetterMethods(String[] methodsName, Object obj,
			Class<?> paramType, byte[] data, int dataPieceLength, double unit, double decimal)
			throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		byte[] pData = new byte[dataPieceLength];
		Double[] dValues = new Double[methodsName.length];
		for (int i = 0; i < methodsName.length; i++) {
			bis.read(pData);
			dValues[i] = getMultiplierValue(pData, unit, decimal);
			Method method = obj.getClass().getMethod(methodsName[i], paramType);
			method.invoke(obj, dValues[i]);
			log.debug("Name[" + methodsName[i] + "] Value[" + dValues[i] + "]");
		}
		bis.close();
		return dValues;
	}

	/**
	 * Double Array 값들을 Setter 메소드에 일괄 적용한다.
	 * 
	 * @see #invokeSetterMethods(String[], Object, Class, byte[], int)
	 * @param methodsName
	 * @param obj
	 * @param paramType
	 * @param data
	 *            Double Array 값으로 Multiplier 미적용된다.
	 * @throws Exception
	 */
	public void invokeSetterMethods(String[] methodsName, Object obj,
			Class<?> paramType, Double[] data) throws Exception {
		for (int i = 0; i < methodsName.length; i++) {
			Method method = obj.getClass().getMethod(methodsName[i], paramType);
			method.invoke(obj, data[i]);
		}
	}

	public Double getMeteringValue() {
		// import 값을 설정한다.
		return this.currentBillingData.getActiveEnergyImportRateTotal();
	}

	public BillingData getBillingData() {
		return billingData;
	}

	public BillingData getCurrentBillingData() {
		return currentBillingData;
	}

	public Object getBillingDataTime() {
		
		return this.billingData.getWriteDate();
	}

	public double getMultiplier() {
		return this.multiplier;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

}
