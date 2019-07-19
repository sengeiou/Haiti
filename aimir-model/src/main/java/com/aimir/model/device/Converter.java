package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 통신 유형을 변환하는 장치이다. 컨버터의 점퍼를 이용하여 RS232/422/485로 변환한다. <br>
 * 현재 시스템에서는 미터와 RS232/422/485 타입의 인터페이스와 연결해서 검침기능을 한다.<br>
 * 
 * @author 박종성(elevas)
 *
 */
@Entity
@DiscriminatorValue("Converter")
public class Converter extends Modem {

    private static final long serialVersionUID = -3783113520426401802L;
    
    @ColumnInfo(name="port", descr="TCP 통신 포트")
    @Column(name="sys_port")
    private Integer sysPort;

    @ColumnInfo(name="sysName", descr="Converter Name")
    @Column(name="sys_name")
    private String sysName;
    
    public Integer getSysPort() {
        return sysPort;
    }

    public void setSysPort(Integer sysPort) {
        this.sysPort = sysPort;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

}