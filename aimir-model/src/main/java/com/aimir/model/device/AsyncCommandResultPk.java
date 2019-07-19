package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * AsyncCommandResult 클래스의 Primary Key를 정의한 클래스
 * 
 * @author 박종성(elevas)
 *
 */
@Embeddable
public class AsyncCommandResultPk extends BasePk {

	private static final long serialVersionUID = 3801120852112519366L;

	@Column(name="trId",length=16,nullable=false)
    private Long trId;  

    @Column(name="mcuId", length=20, nullable=false)
    private String mcuId;	

	@Column(name="num",length=16,nullable=false)
	private Integer num;

    public Long getTrId() {
        return trId;
    }

    public void setTrId(Long trId) {
        this.trId = trId;
    }

    public String getMcuId() {
        return mcuId;
    }

    public void setMcuId(String mcuId) {
        this.mcuId = mcuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}