package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * Gas Meter의 Load Profile 사용량을 나타내는 클래스 
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "LP_GM")
@Indexes({
    @Index(name="IDX_LP_GM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
})
public class LpGM extends MeteringLP{

}
