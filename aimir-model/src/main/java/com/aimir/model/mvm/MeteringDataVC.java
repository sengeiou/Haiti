package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
/**
 * 통신 장비로 부터 검침시 현재 데이터(업로드당시의) 를 저장하는 클래스
 * 보정기에 해당
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "METERINGDATA_VC")
@Index(name="IDX_METERINGDATA_VC_01", columnNames={"yyyymmddhhmmss", "mdev_type", "mdev_id", "location_id"})
public class MeteringDataVC extends MeteringData {

}
