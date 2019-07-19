package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * Solar Meter의 Load Profile 사용량 정보를 정의한 클래스
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
@Entity
@Table(name = "LP_SPM")
@Indexes({
    @Index(name="IDX_LP_SPM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
})
public class LpSPM extends MeteringLP {}
