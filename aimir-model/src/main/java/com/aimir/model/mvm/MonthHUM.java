package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name = "MONTH_HUM")
@Indexes({
    @Index(name="IDX_MONTH_HUM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymm","channel"})
})
public class MonthHUM extends MeteringMonthTHU {

}
