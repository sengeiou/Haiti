
package com.aimir.model.vo;

import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONString;

import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.ModemConfig;

/**
 * Copyright NuriTelecom Co.Ltd. since 2009
 * 
 * DeviceModelConfigVo.java Description
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 20.   v1.0       문동규
 * </pre>
 */
public class DeviceModelConfigVo implements JSONString {

 // DeviceModel
    // 모델 ID
    private Integer modelId;

    // 제조사
    private DeviceVendor deviceVendor;

    // 모델 코드
    private Integer code;

    // 장비타입
    private Code mainDeviceType;

    // 장비타입
    private String mainDeviceTypeName;

    // 장비타입
    private Code deviceType;

    // 모델명
    private String modelName;

    // 설정정보를 찾기 위해선 모델타입을 알아야 한다.(모델타입에 따라 검색하는 테이블이 달라진다)-->상속
    private DeviceConfig deviceConfig;

    // 이미지
    private String image;

    // 설명
    private String description;

    // DeviceConfig
    // 설정 ID
    private Integer configId;

    // 설정명
    private String configName;

    // MeterConfig
    // 미터등급
    private String meterClass;
        
    // phase
    private String phase;

    // 파워공급스펙
    private String powerSupplySpec;

    // 펄스상수
    private Double pulseConst;

    // 미터채널
    private Set<ChannelConfig> channels = new HashSet<ChannelConfig>(0);

    // LP주기
    private Integer lpInterval;

    // parserName : 데이터를 해석할 parser이름
    private String parserName;

    // saverName : 검침데이타 저장 클래스
    private String saverName;
    
    private String ondemandParserName;

    private String ondemandSaverName;

    // 모뎀 SW Version
    private String swVersion;

    // 모뎀 SW Revision
    private String swRevision;
    
    private String meterProtocol;

    /**
     * @return the modelId
     */
    public Integer getModelId() {
        return modelId;
    }

    /**
     * @param modelId the modelId to set
     */
    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    /**
     * @return the deviceVendor
     */
    public DeviceVendor getDeviceVendor() {
        return deviceVendor;
    }

    /**
     * @param deviceVendor the deviceVendor to set
     */
    public void setDeviceVendor(DeviceVendor deviceVendor) {
        this.deviceVendor = deviceVendor;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the mainDeviceType
     */
    public Code getMainDeviceType() {
        return mainDeviceType;
    }

    /**
     * @param mainDeviceType the mainDeviceType to set
     */
    public void setMainDeviceType(Code mainDeviceType) {
        this.mainDeviceType = mainDeviceType;
    }

    /**
     * @return the mainDeviceTypeName
     */
    public String getMainDeviceTypeName() {
        return mainDeviceTypeName;
    }

    /**
     * @param mainDeviceTypeName the mainDeviceTypeName to set
     */
    public void setMainDeviceTypeName(String mainDeviceTypeName) {
        this.mainDeviceTypeName = mainDeviceTypeName;
    }

    /**
     * @return the deviceType
     */
    public Code getDeviceType() {
        return deviceType;
    }

    /**
     * @param deviceType the deviceType to set
     */
    public void setDeviceType(Code deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName the modelName to set
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return the deviceConfig
     */
    public DeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    /**
     * @param deviceConfig the deviceConfig to set
     */
    public void setDeviceConfig(DeviceConfig deviceConfig) {
        this.deviceConfig = deviceConfig;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the configId
     */
    public Integer getConfigId() {
        return configId;
    }

    /**
     * @param configId the configId to set
     */
    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    /**
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * @param configName the configName to set
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * @return the meterClass
     */
    public String getMeterClass() {
        return meterClass;
    }

    /**
     * @param meterClass the meterClass to set
     */
    public void setMeterClass(String meterClass) {
        this.meterClass = meterClass;
    }
    
    /**
     * @return the phase
     */
    public String getPhase() {
        return phase;
    }

    /**
     * @param phase the phase to set
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * @return the powerSupplySpec
     */
    public String getPowerSupplySpec() {
        return powerSupplySpec;
    }

    /**
     * @param powerSupplySpec the powerSupplySpec to set
     */
    public void setPowerSupplySpec(String powerSupplySpec) {
        this.powerSupplySpec = powerSupplySpec;
    }

    /**
     * @return the pulseConst
     */
    public Double getPulseConst() {
        return pulseConst;
    }

    /**
     * @param pulseConst the pulseConst to set
     */
    public void setPulseConst(Double pulseConst) {
        this.pulseConst = pulseConst;
    }

    /**
     * @return the channels
     */
    public Set<ChannelConfig> getChannels() {
        return channels;
    }

    /**
     * @param channels the channels to set
     */
    public void setChannels(Set<ChannelConfig> channels) {
        this.channels = channels;
    }

    /**
     * @return the lpInterval
     */
    public Integer getLpInterval() {
        return lpInterval;
    }

    
    /**
     * @param lpInterval the lpInterval to set
     */
    public void setLpInterval(Integer lpInterval) {
        this.lpInterval = lpInterval;
    }

    /**
     * @return the parserName
     */
    public String getParserName() {
        return parserName;
    }

    /**
     * @param parserName the parserName to set
     */
    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    /**
     * @return the saverName
     */
    public String getSaverName() {
        return saverName;
    }

    /**
     * @param saverName the saverName to set
     */
    public void setSaverName(String saverName) {
        this.saverName = saverName;
    }

    /**
     * @return the parserName
     */
    public String getOndemandParserName() {
        return ondemandParserName;
    }

    /**
     * @param parserName the parserName to set
     */
    public void setOndemandParserName(String ondemandParserName) {
        this.ondemandParserName = ondemandParserName;
    }
    
    /**
     * @return the parserName
     */
    public String getOndemandSaverName() {
        return ondemandSaverName;
    }

    /**
     * @param parserName the parserName to set
     */
    public void setOndemandSaverName(String ondemandSaverName) {
        this.ondemandSaverName = ondemandSaverName;
    }
    
    /**
     * @return the swVersion
     */
    public String getSwVersion() {
        return swVersion;
    }

    /**
     * @param swVersion the swVersion to set
     */
    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    /**
     * @return the swRevision
     */
    public String getSwRevision() {
        return swRevision;
    }

    /**
     * @param swRevision the swRevision to set
     */
    public void setSwRevision(String swRevision) {
        this.swRevision = swRevision;
    }
    
    /**
     * @return the meterProtocol
     */
    public String getMeterProtocol() {
        return meterProtocol;
    }

    /**
     * @param meterProtocol the meterProtocol to set
     */
    public void setMeterProtocol(String meterProtocol) {
        this.meterProtocol = meterProtocol;
    }

    /**
     * DeviceModel 데이터를 return
     * @return
     */
    public DeviceModel getDeviceModel() {
        DeviceModel deviceModel = new DeviceModel();
        
        deviceModel.setId(this.getModelId());
        deviceModel.setDeviceVendor(this.getDeviceVendor());
        deviceModel.setCode(this.getCode());
        deviceModel.setDeviceType(this.getDeviceType());
        deviceModel.setName(this.getModelName());
        deviceModel.setImage(this.getImage());
        deviceModel.setDeviceConfig(this.getDeviceConfig());
        deviceModel.setDescription(this.getDescription());
        return deviceModel;
    }

    /**
     * DeviceModel 데이터를 mapping
     * @param deviceModel
     */
    public void setDeviceModel(DeviceModel deviceModel) {
        this.setModelId(deviceModel.getId());
        this.setDeviceVendor(deviceModel.getDeviceVendor());
        this.setCode(deviceModel.getCode());
        this.setDeviceType(deviceModel.getDeviceType());
        this.setModelName(deviceModel.getName());
        this.setImage(deviceModel.getImage());
        this.setDeviceConfig(deviceModel.getDeviceConfig());
        this.setDescription(deviceModel.getDescription());
    }

    /**
     * ModemConfig 데이터를 return
     * @return
     */
    public ModemConfig getModemConfig() {
        ModemConfig modemConfig = new ModemConfig();

        modemConfig.setId(this.getConfigId());
        modemConfig.setName(this.getConfigName());
        modemConfig.setParserName(this.getParserName());
        modemConfig.setSaverName(this.getSaverName());
        modemConfig.setOndemandParserName(this.getOndemandParserName());
        modemConfig.setOndemandSaverName(this.getOndemandSaverName());
        modemConfig.setSwVersion(this.getSwVersion());
        modemConfig.setSwRevision(this.getSwRevision());
        return modemConfig;
    }

    /**
     * ModemConfig 데이터를 mapping
     * @param deviceModel
     */
    public void setModemConfig(ModemConfig modemConfig) {
        this.setConfigId(modemConfig.getId());
        this.setConfigName(modemConfig.getName());
        this.setParserName(modemConfig.getParserName());
        this.setSaverName(modemConfig.getSaverName());
        this.setOndemandParserName(modemConfig.getOndemandParserName());
        this.setOndemandSaverName(modemConfig.getOndemandSaverName());
        this.setSwVersion(modemConfig.getSwVersion());
        this.setSwRevision(modemConfig.getSwRevision());
    }

    /**
     * MeterConfig 데이터를 return
     * @return
     */
    public MeterConfig getMeterConfig() {
        MeterConfig meterConfig = new MeterConfig();

        meterConfig.setId(this.getConfigId());
        meterConfig.setName(this.getConfigName());
        meterConfig.setMeterClass(this.getMeterClass());
        meterConfig.setMeterProtocol(this.getMeterProtocol());
        meterConfig.setPhase(this.getPhase()); 
        meterConfig.setPowerSupplySpec(this.getPowerSupplySpec());
        meterConfig.setPulseConst(this.getPulseConst());
        meterConfig.setChannel(this.getChannels());
        meterConfig.setLpInterval(this.getLpInterval());
        meterConfig.setParserName(this.getParserName());
        meterConfig.setSaverName(this.getSaverName());
        meterConfig.setOndemandParserName(this.getOndemandParserName());
        meterConfig.setOndemandSaverName(this.getOndemandSaverName());
        meterConfig.setLpInterval(this.getLpInterval());
        return meterConfig;
    }

    /**
     * MeterConfig 데이터를 mapping
     * @param deviceModel
     */
    public void setMeterConfig(MeterConfig meterConfig) {
        this.setConfigId(meterConfig.getId());
        this.setConfigName(meterConfig.getName());
        this.setMeterClass(meterConfig.getMeterClass());
        this.setMeterProtocol(meterConfig.getMeterProtocol());
        this.setPhase(meterConfig.getPhase());
        this.setPowerSupplySpec(meterConfig.getPowerSupplySpec());
        this.setPulseConst(meterConfig.getPulseConst());
        this.setChannels(meterConfig.getChannels());
        this.setLpInterval(meterConfig.getLpInterval());
        this.setParserName(meterConfig.getParserName());
        this.setSaverName(meterConfig.getSaverName());
        this.setOndemandParserName(meterConfig.getOndemandParserName());
        this.setOndemandSaverName(meterConfig.getOndemandSaverName());
    }

    public String toJSONString() {
        StringBuilder retValue = new StringBuilder();

        retValue.append("{");
        retValue.append("modelId:'").append((this.modelId != null) ? this.modelId : "");
        retValue.append("',deviceVendor:'").append((this.deviceVendor == null) ? "null" : this.deviceVendor.getId());
        retValue.append("',code:'").append((this.code != null) ? this.code : "");
        retValue.append("',mainDeviceType:'").append((this.mainDeviceType == null) ? "null" : this.mainDeviceType.getId());
        retValue.append("',mainDeviceTypeName:'").append((this.mainDeviceTypeName != null) ? this.mainDeviceTypeName : "");
        retValue.append("',deviceType:'").append((this.deviceType == null) ? "null" : this.deviceType.getId());
        retValue.append("',modelName:'").append((this.modelName != null) ? this.modelName : "");
        retValue.append("',image:'").append((this.image != null) ? this.image : "");
        retValue.append("',description:'").append((this.description != null) ? this.description : "");
        retValue.append("',configId:'").append((this.configId != null) ? this.configId : "");
        retValue.append("',configName:'").append((this.configName != null) ? this.configName : "");
        retValue.append("',meterClass:'").append((this.meterClass != null) ? this.meterClass : "");
        retValue.append("',meterProtocol:'").append((this.meterProtocol != null) ? this.meterProtocol : "");
        retValue.append("',phase:'").append((this.phase != null) ? this.phase : "");
        retValue.append("',powerSupplySpec:'").append((this.powerSupplySpec != null) ? this.powerSupplySpec : "");
        retValue.append("',pulseConst:'").append((this.pulseConst != null) ? this.pulseConst : "");
        retValue.append("',lpInterval:'").append((this.lpInterval != null) ? this.lpInterval : "");
        retValue.append("',parserName:'").append((this.parserName != null) ? this.parserName : "");
        retValue.append("',saverName:'").append((this.saverName != null) ? this.saverName : "");
        retValue.append("',ondemandParserName:'").append((this.ondemandParserName != null) ? this.ondemandParserName : "");
        retValue.append("',ondemandSaverName:'").append((this.ondemandSaverName != null) ? this.ondemandSaverName : "");
        retValue.append("',swVersion:'").append((this.swVersion != null) ? this.swVersion : "");
        retValue.append("',swRevision:'").append((this.swRevision != null) ? this.swRevision : "");
        retValue.append("'}");

        return retValue.toString();
    }
}