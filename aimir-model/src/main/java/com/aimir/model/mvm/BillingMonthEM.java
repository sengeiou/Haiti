package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

/**
 * 전기 미터의 월별 빌링 데이터를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "BILLING_MONTH_EM")
@Index(name="IDX_BILLING_MONTH_EM_01", columnNames={"mdev_type", "mdev_id", "yyyymmdd"})
public class BillingMonthEM extends BillingEM {

}
