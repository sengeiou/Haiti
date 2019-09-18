package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
/**
 * 통신 장비로 부터 검침시 현재 데이터(업로드당시의) 를 저장하는 클래스
 * 태양열 미터에 해당
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
@Entity
@Table(name = "METERINGDATA_SPM")
//@Index(name="IDX_METERINGDATA_SPM_01", columnNames={"yyyymmddhhmmss", "mdev_type", "mdev_id", "location_id"}) //정규화로 인해 인덱스 생성 삭제 별도의 DDL에서 선언
public class MeteringDataSPM extends MeteringData {}
