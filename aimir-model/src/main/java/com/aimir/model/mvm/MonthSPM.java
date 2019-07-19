package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * 태양열 미터의 월별 total 검침값, 일별 검침값 (1일부터 말일)을 저장하는 클래스
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
@Entity
@Table(name = "MONTH_SPM")
@Indexes({
    @Index(name="IDX_MONTH_SPM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymm","channel","full_location"})
})
public class MonthSPM extends MeteringMonth {}
