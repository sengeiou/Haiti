package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BasePk;

/**
 * 대성 실시간 원격검침 키
 * 
 * @author 박종성
 *
 */
@Deprecated
@Embeddable
public class DaesungMeteringPk extends BasePk{

    private static final long serialVersionUID = 3683526938894043858L;

	@Column(name="AR_ID",length=8)
	@ColumnInfo(name="계약번호", descr="")
	private String contractNumber;
	
	@Column(name="WDV_FLAG", length=1)
	@ColumnInfo(name="WDV 구분자", descr="")
	private String wdvFlag;

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getWdvFlag() {
		return wdvFlag;
	}

	public void setWdvFlag(String wdvFlag) {
		this.wdvFlag = wdvFlag;
	}

}