package com.aimir.model.integration;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 *
 */
@Embeddable
public class WSMeterConfigResultPk extends BasePk {

	/**
	 * 
	 */
	private static final long serialVersionUID = -444137709404695186L;

    @Column(name="REQUEST_DATE",length=14, nullable=false)
    private String requestDate;
    
	@Column(name="trId",length=40,nullable=false)
    private String trId;  

    
    public String getTrId() {
        return trId;
    }

    public void setTrId(String trId) {
        this.trId = trId;
    }
    
	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
}