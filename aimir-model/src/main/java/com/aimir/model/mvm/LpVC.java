package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * Volume Corrector 의 Load Profile을 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "LP_VC")
@Indexes({
    @Index(name="IDX_LP_VC_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel", "full_location"})
})
public class LpVC extends MeteringLP{
	
}
