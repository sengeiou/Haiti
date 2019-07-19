package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
/**
 * 전기 미터의 일별 빌링 데이터를 정의한 클래스
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "BILLING_DAY_EM")
@Index(name="IDX_BILLING_DAY_EM_01", columnNames={"mdev_Type", "mdev_Id", "yyyymmdd"})
public class BillingDayEM extends BillingEM {
}
