package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * 열량 미터의 일별 데이터
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "DAY_HM")
@Indexes({
			@Index(name="IDX_DAY_HM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
		})
public class DayHM extends MeteringDay {

}
