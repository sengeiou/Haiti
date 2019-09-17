package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * 수도 미터의 월별 total 검침값, 일별 검침값 (1일부터 말일)을 저장하는 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "MONTH_WM")
//@Indexes({
//    @Index(name="IDX_MONTH_WM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymm","channel","full_location"})
//})
public class MonthWM extends MeteringMonth {

}
