package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * 태양열 미터의 일별 데이터
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
@Entity
@Table(name = "DAY_SPM")
@Indexes({
    @Index(name="IDX_DAY_SPM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
})
public class DaySPM extends MeteringDay {}
