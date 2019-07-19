package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.VEEParam;
import com.aimir.constants.CommonConstants.VEEPeriodItem;
import com.aimir.constants.CommonConstants.VEETableItem;
import com.aimir.constants.CommonConstants.VEEThresholdItem;
import com.aimir.constants.CommonConstants.VEEType;
import com.aimir.model.BaseObject;

/**
 * VEE Check Rule Parameter 설정 모델 
 * @author YeonKyoung Park(goodjob)
 * 
 * 
 * 데이터 파라미터 체크 조건
 * 
 * 기본적으로 ruleType은 Validation은 Validation,Estimation,Editing에 대한 구분을 나타냄
 * Validation에서 Validate하지 않은 항목을 찾을 때 
 * 검색 조건 
 *
 *  condition 값이 없을때는 적용하지 않음 condition에 특정 컬럼(속성)과 조건이 같이 있으면 해당 컬럼만 적용하며
 *  없는 경우는 검침데이터 관련 모든 컬럼에 적용
 *  use Threshold값 여부를 체크하여 useThreshold가 true인 경우 threshold에 대한 조건값을 적용하며 
 *  item은 테이블 이름을 의미
 *  
 *  thresholdItem과 thresholdPeriod는 각각 적용 기간 및 최대최소 등의 항목을 의미한다.
 *  예를들면 AbnormalHighUsage인 경우
 *  item이 Day이면 Day_XX에서 지정날짜로 데이터를 조회하고
 *  비교 근거가 되는 같은 DAY_XX의 지난달의 VALUE_XX에 평균값을 구하여
 *  그 값의 threshold 범위(50% ~500%)사이의 기준값 예를들면 지난달 평균이 10이면
 *  임계치 범위가 50% ~ 500%이므로 임계치범위가 5 ~ 50이 되며
 *  지정날짜기간의 DAY_XX의 VALUE_XX 값들과 비교하여 임계치 범위에 해당하면
 *  Validate하지 않은 조건으로 값이 검색된다.
 *  
 *  
 *  AbnormalHighDemand의 경우는 아래와 같은 조건이면
 *  ruleType="Validation"
 *  parameter="AbnormalHighDemand"
 *  localName="Abnormal High Demand" 
 *  useThreshold="true"  
 *  item="Day"  
 *  threshold1="50"
 *  thresholdCondition1="&gt;"
 *  threshold2="500"
 *  thresholdCondition2="&lt;"
 *  thresholdItem="Maximum"
 *  thresholdPeriod="LastMonth"
 *   
 *  1.DAY_XX에서 지정기간의 VALUE_XX값들을 구한다.
 *  2.DAY_XX에서 지난달 동안의 VALUE_XX의 최대값을 구한다.
 *  3. 임계치 구간 50% ~ 500%해당하는 구간을 구한다. 
 *  3번의 구간과 1번의 값들을 비교하여 해당 구간에 속하면 결과값을 보여준다.
 *
 */
@Entity
@Table(name="VEE_PARAMETER")
public class VEEParameter extends BaseObject {

	private static final long serialVersionUID = 1576552466862276353L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VEE_PARAMETER_SEQ")
    @SequenceGenerator(name="VEE_PARAMETER_SEQ", sequenceName="VEE_PARAMETER_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(name="RULE_TYPE", nullable=false)
	@ColumnInfo(descr="VEE RULE TYPE, Validation Rule, Editing Rule, Estimation Rule ")
	@Enumerated(EnumType.STRING)
	private VEEType ruleType;
	
	@Column(name="PARAMETER", nullable=false)
	@ColumnInfo(descr="항목 코드 ")
	@Enumerated(EnumType.STRING)
	private VEEParam parameter;
	
	@Column(name="LOCAL_NAME", nullable=false)
	@ColumnInfo(descr="항목 이름")
	private String localName;
	
	@Column(name="USE_THRESHOLD")
	@ColumnInfo(descr="임계치 사용(적용) 여부")
	private Boolean useThreshold;
	
	@Column(name="ITEM", nullable=false)
	@ColumnInfo(descr="항목 이름")
	@Enumerated(EnumType.STRING)
	private VEETableItem item;
	
	@Column(name="CONDITION_ITEM", length=1000)
	@ColumnInfo(descr="ITEM의 체크 조건")
	private String condition;
	
	@Column(name="THRESHOLD1")
	@ColumnInfo(descr="임계치 값 1")
	private Integer threshold1;
	
	@Column(name="THRESHOLD_CONDITION1")
	@ColumnInfo(descr="임계치 조건 1")
	private String thresholdCondition1;
	
	@Column(name="THRESHOLD2")
	@ColumnInfo(descr="임계치 값 2")
	private Integer threshold2;
	
	@Column(name="THRESHOLD_CONDITION2")
	@ColumnInfo(descr="임계치 조건 2")
	private String thresholdCondition2;
	
	@Column(name="THRESHOLD_ITEM")
	@ColumnInfo(descr="임계치 설정 항목")
	@Enumerated(EnumType.STRING)
	private VEEThresholdItem thresholdItem;
	
	@Column(name="THRESHOLD_PERIOD")
	@ColumnInfo(descr="임계치 설정 항목 중 기간")
	@Enumerated(EnumType.STRING)
	private VEEPeriodItem thresholdPeriod;

    public VEEType getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = VEEType.valueOf(ruleType);
	}

	public VEEParam getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = VEEParam.valueOf(parameter);
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public Boolean getUseThreshold() {
		return useThreshold;
	}

	public void setUseThreshold(Boolean useThreshold) {
		this.useThreshold = useThreshold;
	}

	public VEETableItem getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = VEETableItem.valueOf(item);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getThreshold1() {
		return threshold1;
	}

	public void setThreshold1(Integer threshold1) {
		this.threshold1 = threshold1;
	}

	public String getThresholdCondition1() {
		return thresholdCondition1;
	}

	public void setThresholdCondition1(String thresholdCondition1) {
		this.thresholdCondition1 = thresholdCondition1;
	}

	public Integer getThreshold2() {
		return threshold2;
	}

	public void setThreshold2(Integer threshold2) {
		this.threshold2 = threshold2;
	}

	public String getThresholdCondition2() {
		return thresholdCondition2;
	}

	public void setThresholdCondition2(String thresholdCondition2) {
		this.thresholdCondition2 = thresholdCondition2;
	}

	public VEEThresholdItem getThresholdItem() {
		return thresholdItem;
	}

	public void setThresholdItem(String thresholdItem) {
		this.thresholdItem = VEEThresholdItem.valueOf(thresholdItem);
	}

	public VEEPeriodItem getThresholdPeriod() {
		return thresholdPeriod;
	}

	public void setThresholdPeriod(String thresholdPeriod) {
		this.thresholdPeriod = VEEPeriodItem.valueOf(thresholdPeriod);
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	@Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}
