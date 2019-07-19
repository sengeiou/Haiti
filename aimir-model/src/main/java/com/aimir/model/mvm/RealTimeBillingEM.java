package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 전기 미터의 실시간 사용량 (TOU를 적용한) 빌링 데이터 
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "REALTIME_BILLING_EM")
@Indexes({
    @Index(name="IDX_REALTIME_BILLING_EM_01", columnNames={"mdev_type", "mdev_id", "yyyymmdd"})
})
public class RealTimeBillingEM  extends BillingEM {

}
