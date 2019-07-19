package com.aimir.service.system.impl;

public class EBSDataMapper {
    String MID;
    /**
     * location code 값을 var 이름으로 사용할수 있도록 특수문자 제외하고 접두어 'L' 을붙인 값
     */
    String PTYPE_ID;
    String CMID;
    String CHANNEL;
    Double IMP_ACTIVE_KWH;
    Double IMP_Q1_REAC_KVARH;
    Double IMP_Q2_REAC_KVARH;
    Double IMP_KVH;

    Double EXP_ACTIVE_KWH;
    Double EXP_Q1_REAC_KVARH;
    Double EXP_Q2_REAC_KVARH;
    Double EXP_KVH;
    
    Double THRESHOLD;
    
	public String getMID() {
		return MID;
	}
	public void setMID(String mID) {
		MID = mID;
	}
	public String getPTYPE_ID() {
		return PTYPE_ID;
	}
	public void setPTYPE_ID(String pTYPE_ID) {
		PTYPE_ID = pTYPE_ID;
	}
	public String getCMID() {
		return CMID;
	}
	public void setCMID(String cMID) {
		CMID = cMID;
	}
	
	
	public String getCHANNEL() {
		return CHANNEL;
	}
	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}
	public Double getIMP_ACTIVE_KWH() {
		return IMP_ACTIVE_KWH;
	}
	public void setIMP_ACTIVE_KWH(Double iMP_ACTIVE_KWH) {
		IMP_ACTIVE_KWH = iMP_ACTIVE_KWH;
	}
	public Double getIMP_Q1_REAC_KVARH() {
		return IMP_Q1_REAC_KVARH;
	}
	public void setIMP_Q1_REAC_KVARH(Double iMP_Q1_REAC_KVARH) {
		IMP_Q1_REAC_KVARH = iMP_Q1_REAC_KVARH;
	}
	public Double getIMP_Q2_REAC_KVARH() {
		return IMP_Q2_REAC_KVARH;
	}
	public void setIMP_Q2_REAC_KVARH(Double iMP_Q2_REAC_KVARH) {
		IMP_Q2_REAC_KVARH = iMP_Q2_REAC_KVARH;
	}
	public Double getIMP_KVH() {
		return IMP_KVH;
	}
	public void setIMP_KVH(Double iMP_KVH) {
		IMP_KVH = iMP_KVH;
	}
	public Double getEXP_ACTIVE_KWH() {
		return EXP_ACTIVE_KWH;
	}
	public void setEXP_ACTIVE_KWH(Double eXP_ACTIVE_KWH) {
		EXP_ACTIVE_KWH = eXP_ACTIVE_KWH;
	}
	public Double getEXP_Q1_REAC_KVARH() {
		return EXP_Q1_REAC_KVARH;
	}
	public void setEXP_Q1_REAC_KVARH(Double eXP_Q1_REAC_KVARH) {
		EXP_Q1_REAC_KVARH = eXP_Q1_REAC_KVARH;
	}
	public Double getEXP_Q2_REAC_KVARH() {
		return EXP_Q2_REAC_KVARH;
	}
	public void setEXP_Q2_REAC_KVARH(Double eXP_Q2_REAC_KVARH) {
		EXP_Q2_REAC_KVARH = eXP_Q2_REAC_KVARH;
	}
	public Double getEXP_KVH() {
		return EXP_KVH;
	}
	public void setEXP_KVH(Double eXP_KVH) {
		EXP_KVH = eXP_KVH;
	}
	public Double getTHRESHOLD() {
		return THRESHOLD;
	}
	public void setTHRESHOLD(Double tHRESHOLD) {
		THRESHOLD = tHRESHOLD;
	}

}
