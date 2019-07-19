package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
/*
 * 통신 장비로 부터 검침시 현재 데이터(업로드당시의) 를 저장하는 클래스
 * 온습도 미터에 해당
 */
@Entity
@Table(name = "METERINGDATA_THUM")
@Index(name="IDX_METERINGDATA_THUM_01", columnNames={"yyyymmddhhmmss", "mdev_Type", "mdev_Id", "location_id"})
public class MeteringDataTHUM extends MeteringDataTHU {

}
