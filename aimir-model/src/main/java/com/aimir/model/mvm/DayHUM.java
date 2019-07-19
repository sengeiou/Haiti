package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
/**
 * 습도 미터의 일별 데이터
 * @author 신인호(inho_shin)
 */
@Entity
@Table(name = "DAY_HUM")
@Indexes({
    @Index(name="IDX_DAY_HUM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel"})
})
public class DayHUM extends MeteringDayTHU {

}
