package com.aimir.model.device;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>HMU - Home Metering Unit (전기 장치나 기타 Device의 전원 공급등 에너지 공급을 차단하는 장치 댁내 Meter 공급 측에 장착되서 사용)</p>
 * 미터와는 별도로 댁내 전체 전기 사용량을 측정하고 제어하는 장치. 현재 ZEUPLS와 동일하다.<br>
 * 
 * @author elevas
 */
@Entity
@DiscriminatorValue("HMU")
public class HMU extends ZEUPLS {

	private static final long serialVersionUID = 3569028474479015826L;
}