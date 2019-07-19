package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
/**
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "LP_EM")
@Indexes({
    @Index(name="IDX_LP_EM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
})
public class LpEM extends MeteringLP{

}
