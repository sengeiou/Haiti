package com.aimir.fep.protocol.nip.command;

public class ResponseResult {
    public enum Status {
        Success (new byte[]{(byte)0x00, (byte)0x00}),
        FormatError (new byte[]{(byte)0x10, (byte)0x01}),
        ParameterError (new byte[]{(byte)0x10, (byte)0x02}),
        ValueOverflow (new byte[]{(byte)0x10, (byte)0x03}),
        InvalidAttrId (new byte[]{(byte)0x10, (byte)0x04}),
        AuthorizationError (new byte[]{(byte)0x10, (byte)0x05}),
        NoDataError (new byte[]{(byte)0x10, (byte)0x06}),
        MeteringBusy (new byte[]{(byte)0x20, (byte)0x00}),
        Unknown (new byte[]{(byte)0xFF, (byte)0x00});
        
        private byte[] code;
        
        Status(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
    
    public enum ObisStatus {
    	Success ((byte)0x00),
        Data_Error ((byte)0x01),
        Data_Overlap ((byte)0x02);
        
        private byte code;
        
        ObisStatus(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public enum SnmpTrapStatus {
        Off (new byte[]{(byte)0x00}),
        On (new byte[]{(byte)0x01});
        
        private byte[] code;
        
        SnmpTrapStatus(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
 
    public enum PowerSourceType {
        Unknown (new byte[]{(byte)0x00}),
        Electric (new byte[]{(byte)0x01}),
        Battery (new byte[]{(byte)0x02}),
        Soloar (new byte[]{(byte)0x03}),
        Super_Cap (new byte[]{(byte)0x04});
        
        private byte[] code;
        
        PowerSourceType(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }

    public enum TypeMain {

    	Ethernet (new byte[]{(byte)0x00}),
    	Mobile (new byte[]{(byte)0x01});
        private byte[] code;
        
        TypeMain(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }

    public enum TypeType {
        ZigBee  (new byte[]{(byte)0x00}),
        PLC  (new byte[]{(byte)0x01}),
        Subgiga  (new byte[]{(byte)0x02});
        
        private byte[] code;
        
        TypeType(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }

    public enum ModemMode {
        PushMode (new byte[]{(byte)0x00}),
        PollMode (new byte[]{(byte)0x01});
        
        private byte[] code;
        
        ModemMode(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
    public enum NetworkSpeedType {
        S48 (new byte[]{(byte)0x01}),
        S384 (new byte[]{(byte)0x02}),
        S500 (new byte[]{(byte)0x03}),
        S1000 (new byte[]{(byte)0x04}),
        S1500 (new byte[]{(byte)0x05});
        
        private byte[] code;
        
        NetworkSpeedType(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
    public enum CertificateStatus {
        Success (new byte[]{(byte)0x00}),
        Fail (new byte[]{(byte)0x01});
        
        private byte[] code;
        
        CertificateStatus(byte[] code) {
            this.code = code;
        }
        
        public byte[] getCode() {
            return this.code;
        }
    }
    public Status status;
    public ObisStatus obiusStatus;
    public SnmpTrapStatus snmpTrapStatus;
    public PowerSourceType powerSourceType;
    public TypeMain typeMain;
    public TypeType typeType;
    public ModemMode modemMode;
    public NetworkSpeedType networkSpeedType;
    public CertificateStatus certificateStatus;

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public ObisStatus getObiusStatus() {
		return obiusStatus;
	}
	public void setObiusStatus(ObisStatus obiusStatus) {
		this.obiusStatus = obiusStatus;
	}
	public SnmpTrapStatus getSnmpTrapStatus() {
		return snmpTrapStatus;
	}
	public void setSnmpTrapStatus(SnmpTrapStatus snmpTrapStatus) {
		this.snmpTrapStatus = snmpTrapStatus;
	}
	public PowerSourceType getPowerSourceType() {
		return powerSourceType;
	}
	public void setPowerSourceType(PowerSourceType powerSourceType) {
		this.powerSourceType = powerSourceType;
	}
	public TypeMain getTypeMain() {
		return typeMain;
	}
	public void setTypeMain(TypeMain typeMain) {
		this.typeMain = typeMain;
	}
	public TypeType getTypeType() {
		return typeType;
	}
	public void setTypeType(TypeType typeType) {
		this.typeType = typeType;
	}
	public ModemMode getModemMode() {
		return modemMode;
	}
	public void setTypeType(ModemMode modemMode) {
		this.modemMode = modemMode;
	}
	public NetworkSpeedType getNetworkSpeedType() {
		return networkSpeedType;
	}
	public void setNetworkSpeedType(NetworkSpeedType networkSpeedType) {
		this.networkSpeedType = networkSpeedType;
	}
	public CertificateStatus getCertificateStatus() {
		return certificateStatus;
	}
	public void setCertificateStatus(CertificateStatus certificateStatus) {
		this.certificateStatus = certificateStatus;
	}
}
