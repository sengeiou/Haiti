package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

/*
 * 통신 장비로 부터 검침시 현재 데이터(업로드당시의) 를 저장하는 클래스
 * 전기미터에 해당
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "METERINGDATA_EM")
//@Index(name="IDX_METERINGDATA_EM_01", columnNames={"yyyymmddhhmmss", "mdev_type", "mdev_id", "location_id"}) //정규화로 인해 인덱스 생성 삭제 별도의 DDL에서 선언
public class MeteringDataEM extends MeteringData {

}
