package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Temperature Meter의 월별 total 검침값, 일별 검침값 (1일부터 말일)을 저장하는 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "MONTH_TM")
public class MonthTM extends MeteringMonthTHU {

}
