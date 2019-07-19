package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * Temperature Meter의 일별 사용량, 시간별 사용량을 정의한 클래스
 * 
 * @author 신인호(inho_shin)
 */
@Entity
@Table(name = "DAY_TM")
public class DayTM extends MeteringDayTHU {

}
