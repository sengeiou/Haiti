package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
/**
 * 보정기의 일별 데이터
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "DAY_VC")
@Indexes({
    @Index(name="IDX_DAY_VC_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
    })
public class DayVC extends MeteringDay {

}
