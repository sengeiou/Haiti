package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Temperature Meter의 LoadProfile을 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "LP_TM")
public class LpTM extends MeteringLPTHU{

}
