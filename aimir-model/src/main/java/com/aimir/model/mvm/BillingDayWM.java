package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
/**
 * 전기 미터의 일별 빌링
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "BILLING_DAY_WM")
@Index(name="IDX_BILLING_DAY_WM_01", columnNames={"mdev_Type", "mdev_Id", "yyyymmdd"})
public class BillingDayWM extends Billing {

}
