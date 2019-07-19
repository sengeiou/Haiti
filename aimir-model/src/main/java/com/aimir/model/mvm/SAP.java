/**
 * 태국 MEA&PEA 프로젝트.<br>
 * MX2 미터 검침 데이터정보를 SAP System 과의 연동 파일 출력 기능.
 * <br>
 * 2012-04-18
 * @author kskim
 */

package com.aimir.model.mvm;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONString;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.device.Meter;

/**
 * 태국 MEA&PEA 요구사항중 SAP 포멧으로 검침 정보를 출력하는 기능있다. 출력시 필요한 정보를 담고있는 데이터 구조. 
 * <br>Mitsubshi MX2 미터에 종속적이다. SAP 파일포멧 출력 기능 자체가 종속적임.</br>
 * @author kskim
 *
 */
@Entity
@Table(name = "SAP")
public class SAP extends BaseObject implements JSONString{
	private static Log log = LogFactory.getLog(SAP.class);
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAP_SEQ")
    @SequenceGenerator(name="SAP_SEQ", sequenceName="SAP_SEQ", allocationSize=1) 
	private Integer id;
	
    @OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="METER_ID", nullable=false)
    @ReferencedBy(name="meter")
	private Meter meter;
	
	/**
	 * @deprecated 단일 파일 생성기능 제외됨에 따라 필요 없어짐
	 */
	@ColumnInfo(descr="파일 생성여부")
	private Boolean isExport;
	
	@ColumnInfo(descr="입력 날짜 yyyyMMddHHmmss")
	private String writeDate;
	
	/**
	 * @deprecated 미터 단일 정보를 output하지 않고 특정 미터 그룹 정보를 통합해서 출력할것임.
	 */
	@ColumnInfo(descr="출력 파일 내용")
	private String outputData;
	
	/**
	 * phase 값에 따라 출력 파일 포멧이 변경됨.
	 */
	@ColumnInfo(descr="phaseWires  (0 = 1P2W, 1 = 1P3W, 2 = 3P3W, 3 = 3P4W)")
	@Column(name="PHASE_WIRES", nullable=false, unique=false, columnDefinition="int default 2")
	private Integer phaseWires;
	
	/**
	 * 아래 필드들은 SAP 포멧에 필요한 미터 테이블 값을 저장한다.
	 */
	
	@ColumnInfo(descr="1.MEA Number (000000000009XXXXXX)")
	private String MeaNumber;
	
	@ColumnInfo(descr="3.Daylight saving present time")
	private String saveTime;
	
	@ColumnInfo(descr="18~20.(kW, kvar, kWh) Register Multiplier(Numeric Code)")
	private Integer multiplier;
	
	@ColumnInfo(descr="21.Error code/ Error Note")
	private String errorCode;
	
	
	
	/**
	 * @deprecated 미터 단일 정보를 output하지 않고 특정 미터 그룹 정보를 통합해서 출력할것임.
	 * @return
	 */
	public String getOutputData() {
		return outputData;
	}

	/**
	 * @deprecated 미터 단일 정보를 output하지 않고 특정 미터 그룹 정보를 통합해서 출력할것임.
	 * @param outputData
	 */
	public void setOutputData(String outputData) {
		this.outputData = outputData;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(String saveTime) {
		this.saveTime = saveTime;
	}

	public Integer getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Integer multiplier) {
		this.multiplier = multiplier;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Boolean getIsExport() {
		return isExport;
	}

	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public String getMeaNumber() {
		return MeaNumber;
	}

	public void setMeaNumber(String meaNumber) {
		MeaNumber = meaNumber;
	}
	
	public Integer getPhaseWires() {
		return phaseWires;
	}

	public void setPhaseWires(Integer phaseWires) {
		this.phaseWires = phaseWires;
	}

	@Override
	public boolean equals(Object o) {

		boolean theSame = true;
		
		//RawUDR class 의 모든 field 정보를 불러온다.
		Field[] f = this.getClass().getFields();
		for (Field field : f) {
			field.setAccessible(true);
			
			try {
				
				//비교 하려는 객체의 필드값중 같은 이름의 필드값을 읽어온다. 없으면 NoSuchFieldException 발생해 false리턴
				Field targetField = o.getClass().getField(field.getName());
				targetField.setAccessible(true);
				
				Object value = field.get(this);
				Object targetValue = targetField.get(o);
				
				//두값이 같은 값인지 확인한다.
				if(!value.equals(targetValue)){
					theSame = false;
					//하나라도 틀리면 같은 객체가 아니다.
					break;
				}
			
			} catch (NoSuchFieldException e){
				log.error(e,e);
				return false;
			} catch (IllegalArgumentException e) {
				log.error(e,e);
				return false;
			} catch (IllegalAccessException e) {
				log.error(e,e);
				return false;
			}
		}
		return theSame;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 0;
        
		Field[] fields = this.getClass().getFields();
		
		for (Field field : fields) {
			field.setAccessible(true);
			
			Object obj = null;
			try {
				obj = field.get(this);
			} catch (Exception e) {
				log.error(e,e);
			} 
			
			result = prime * result + (obj==null ? 0 : obj.hashCode());
			
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" [");
		
		Field[] fields = this.getClass().getFields();
		
		for (Field field : fields) {
			sb.append(field.getName());
			sb.append("=");
			try {
				Object obj = field.get(this);
				if(obj==null){
					sb.append("");
					continue;
				}
				
				if(field.getName().equals("meter")){
					sb.append(this.meter.getMdsId());
				}else{				
					sb.append(obj);
				}
			} catch (Exception e) {
				log.error(e,e);
				sb.append("null");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1); //마지막 ,(콤마) 삭제
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toJSONString() {
		JSONStringer js = null;
    	try {
    		js = new JSONStringer();
    		JSONBuilder builder = js.object();
    		
    		Field[] fields = this.getClass().getFields();
    		
    		for (Field field : fields) {
    			
    			Object value = field.get(this);
    			
    			if(value==null){
    				builder.key(field.getName()).value("");
    				continue;
    			}
    			
				if(field.getName().equals("meter")){
					value = new String(this.meter.getMdsId());
				}
				
				builder.key(field.getName()).value(value == null ? "":value.toString());
				
    		}
    		builder.endObject();
    	} catch (Exception e) {
    		log.error(e,e);
    		return null;
    	}
    	return js.toString();
	}
	
	
}
