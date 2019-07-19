package com.aimir.test.fep.pattern.metering;

public abstract class TestMetering implements Runnable {

    protected String MCUID = null;
    protected int nodeCount = 1;
    protected String targetIp = null;
    protected int targetPort = 8000;
    protected int lpCnt = 12;
    protected int lpPeriod = 60;

    public String getMCUID() {
		return MCUID;
	}

	public void setMCUID(String mCUID) {
		MCUID = mCUID;
	}	

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public String getTargetIp() {
		return targetIp;
	}

	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public int getLpCnt() {
		return lpCnt;
	}

	public void setLpCnt(int lpCnt) {
		this.lpCnt = lpCnt;
	}

	public int getLpPeriod() {
		return lpPeriod;
	}

	public void setLpPeriod(int lpPeriod) {
		this.lpPeriod = lpPeriod;
	}	
	
}