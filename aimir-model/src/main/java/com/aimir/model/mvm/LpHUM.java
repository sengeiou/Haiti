package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name = "LP_HUM")
//@Indexes({
//    @Index(name="IDX_LP_HUM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel"})  //DB정규화로 인해 해당 인덱스 불필요
//})
public class LpHUM extends MeteringLPTHU{

}
