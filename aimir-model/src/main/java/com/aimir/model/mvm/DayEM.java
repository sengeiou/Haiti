package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
/**
 * 전기 미터의 일별 데이터
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "DAY_EM")
//@Indexes({
//		@Index(name="IDX_DAY_EM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel"}) //DB정규화로 인해 해당 인덱스 불필요
//        })
public class DayEM extends MeteringDay {

}
