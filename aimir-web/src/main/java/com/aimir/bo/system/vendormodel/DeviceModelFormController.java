package com.aimir.bo.system.vendormodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.ModemConfig;
import com.aimir.model.vo.DeviceModelConfigVo;
import com.aimir.service.mvm.ChannelConfigManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.MeterConfigManager;
import com.aimir.service.system.ModemConfigManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.CodeTypeEditor;
import com.aimir.support.DeviceVendorTypeEditor;
import com.aimir.support.IntegerTypeEditor;
import com.aimir.util.StringUtil;

@Controller
public class DeviceModelFormController {

	@Autowired
	DeviceModelManager deviceModelManager;

	@Autowired
	DeviceVendorManager deviceVendorManager;

	@Autowired
	SupplierManager supplierManager;

	@Autowired
	CodeManager codeManager;

	@Autowired
	AimirFilePath aimirFilePath;

    @Autowired
    ModemConfigManager modemConfigManager;

    @Autowired
    MeterConfigManager meterConfigManager;
    
    @Autowired
    ChannelConfigManager channelConfigManager;

   
//	public void setAimirFilePath(AimirFilePath aimirFilePath) {
//		this.aimirFilePath = aimirFilePath;
//	}

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Code.class, new CodeTypeEditor());
        binder.registerCustomEditor(String.class, "code", new IntegerTypeEditor());
        binder.registerCustomEditor(DeviceVendor.class, new DeviceVendorTypeEditor());
    }

    private void onBindAndValidate(int supplierId, DeviceModel deviceModel, Errors errors, boolean addCheck) {

        if (deviceModel.getDeviceType() != null) {
            Code type = codeManager.getCode(deviceModel.getDeviceType().getId());

            if (type == null)
                errors.rejectValue("deviceType", "required", "required");
        }

        if (deviceModel.getDeviceVendor() != null) {
            DeviceVendor vendor = deviceVendorManager.getDeviceVendor(deviceModel.getDeviceVendor().getId());

            if (vendor == null)
                errors.rejectValue("deviceVendor", "required", "required");
        }

        if (!StringUtils.hasLength(deviceModel.getName())) {
            errors.rejectValue("name", "required", "required");
        } else {
            List<DeviceModel> models = deviceModelManager.getDeviceModelByName(supplierId, deviceModel.getName());

            if (addCheck) {
                if (models.size() > 0)
                    errors.rejectValue("name", "duplicated", "duplicated");
            } else {
                if (models.size() > 0) {

                    if (models.size() == 1) {
                        DeviceModel model = models.get(0);

                        int modelId = model.getId();
                        int deviceModelId = deviceModel.getId();

                        if (modelId != deviceModelId) {
                            errors.rejectValue("name", "duplicated", "duplicated");
                        }
                    } else {
                        errors.rejectValue("name", "duplicated", "duplicated");
                    }
                }

            }
        }

        if (deviceModel.getCode() == null || !StringUtils.hasLength(deviceModel.getCode() + "")) {
            errors.rejectValue("code", "required", "required");
        } else {
        }

    }

    
    
    private void onBindAndValidate(int supplierId, DeviceModelConfigVo deviceModelConfig, Errors errors, boolean addCheck) {

        if (deviceModelConfig.getDeviceType() != null) {
            Code type = codeManager.getCode(deviceModelConfig.getDeviceType().getId());

            if (type == null)
                errors.rejectValue("deviceType", "required", "required");
        }

        if (deviceModelConfig.getDeviceVendor() != null) {
            DeviceVendor vendor = deviceVendorManager.getDeviceVendor(deviceModelConfig.getDeviceVendor().getId());

            if (vendor == null)
                errors.rejectValue("deviceVendor", "required", "required");
        }

        if (!StringUtils.hasLength(deviceModelConfig.getModelName())) {
            errors.rejectValue("modelName", "required", "required");
        } else {
            List<DeviceModel> models = deviceModelManager.getDeviceModelByName(supplierId, deviceModelConfig.getModelName());

            if (addCheck) {
                if (models.size() > 0)
                    errors.rejectValue("modelName", "duplicated", "duplicated");
            } else {
                if (models.size() > 0) {

                    if (models.size() == 1) {
                        DeviceModel model = models.get(0);

                        int modelId = model.getId();
                        int deviceModelId = deviceModelConfig.getModelId();

                        if (modelId != deviceModelId) {
                            errors.rejectValue("modelName", "duplicated", "duplicated");
                        }
                    } else {
                        errors.rejectValue("modelName", "duplicated", "duplicated");
                    }
                }
            }
        }

        if (deviceModelConfig.getCode() == null || !StringUtils.hasLength(deviceModelConfig.getCode() + "")) {
            errors.rejectValue("code", "required", "required");
        }

        int type = 0;   // 1:Modem, 2:Meter

        if (StringUtil.nullToBlank(deviceModelConfig.getMainDeviceTypeName()).equals("Modem")) {
            type = 1;
        } else if (StringUtil.nullToBlank(deviceModelConfig.getMainDeviceTypeName()).equals("Meter")) {
            type = 2;
        }

        if (type > 0) {
            if (!StringUtils.hasLength(deviceModelConfig.getConfigName())) {
                errors.rejectValue("configName", "required", "required");
            }

            if (type == 1) {    // Modem
                if (!StringUtils.hasLength(deviceModelConfig.getParserName())) {
                    errors.rejectValue("parserName", "required", "required");
                }

                if (!StringUtils.hasLength(deviceModelConfig.getSaverName())) {
                    errors.rejectValue("saverName", "required", "required");
                }
                if (!StringUtils.hasLength(deviceModelConfig.getOndemandParserName())) {
                    errors.rejectValue("ondemandParserName", "required", "required");
                }

                if (!StringUtils.hasLength(deviceModelConfig.getOndemandSaverName())) {
                    errors.rejectValue("ondemandSaverName", "required", "required");
                }
            }
        }
    }

    @RequestMapping(value = "/gadget/system/addDeviceModelConfig", method = RequestMethod.POST)
    public ModelAndView addDeviceModelConfig(@RequestParam("supplierId") int supplierId,
            @RequestParam("displayidlist") String displayidlist,
            @RequestParam("namelist") String namelist,
            @RequestParam("indexlist") String indexlist,
            @RequestParam("typelist") String typelist,
            @RequestParam("subType") String subType,           
            @ModelAttribute("modelForm") DeviceModelConfigVo deviceModelConfig, BindingResult result, SessionStatus status) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        DeviceFormResult bindResult = new DeviceFormResult(result.getTarget(), result.getObjectName());
        onBindAndValidate(supplierId, deviceModelConfig, bindResult, true);

        if (bindResult.hasErrors()) {
            mav.addObject("result", "failure");
            mav.addObject("errors", bindResult);
            return mav;
        }

        if (deviceModelConfig.getImage() == null || deviceModelConfig.getImage().length() == 0) {
            deviceModelConfig.setImage(aimirFilePath.getDefaultPath() + "/" + CommonConstants.DefaultImg.MCU.getDefaultImg());
        }
        deviceModelConfig.setModelId(null);
        DeviceModel deviceModel = deviceModelManager.addDeviceModel(deviceModelConfig.getDeviceModel());

        Code mainDeviceType = codeManager.getCode(deviceModelConfig.getMainDeviceType().getId());
        
        if (mainDeviceType != null) {

            if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Modem")) {
                ModemConfig modemConfig = deviceModelConfig.getModemConfig();
                modemConfig.setDeviceModel(deviceModel);
                
                modemConfigManager.addModemConfig(modemConfig);
            } else if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Meter")) {
                MeterConfig meterConfig = deviceModelConfig.getMeterConfig();
                
                if(meterConfig.getName()!=null && !meterConfig.getName().equals("")){
                	 meterConfig.setDeviceModel(deviceModel);

                    
                     meterConfigManager.addMeterConfig(meterConfig);
                     
                     if(displayidlist !=null && !"".equals(displayidlist)){
                    	 List<Object> channelList =  new ArrayList<Object>();
                         
                         String[] chdisplayidlist = displayidlist.split("@");
                         String[] chnamelist = namelist.split("@");
                         String[] chindexlist = indexlist.split("@");
                         String[] chtypelist = typelist.split("@");
                         
                        String serviceType = codeManager.getCode(Integer.parseInt(subType)).getName();
                        for (int i=0; i<chdisplayidlist.length;i++) {
                            	 Map<String,Object> condition = new HashMap<String,Object>();
                            	 condition.put("displayid", chdisplayidlist[i]);
                            	 condition.put("name", chnamelist[i]);
                            	 condition.put("channelIndex", chindexlist[i]);
                            	 condition.put("displayType", chtypelist[i]);
                            	 condition.put("serviceType", serviceType);
                            	 channelList.add(i,condition);
                        }
                         channelConfigManager.saveOrUpdateChannelConfig(meterConfig.getId(),channelList);
                     }
                    
                }               
            }
        }
        
        status.setComplete();

        mav.addObject("deviceVendor", deviceVendorManager.getDeviceVendor(deviceModel.getDeviceVendor().getId()));
        mav.addObject("deviceModel", deviceModel);
        mav.addObject("result", "success");
//        mav.addObject("viewName", viewName);
        return mav;
        // return
        // "redirect:/gadget/system/modelinfo.do?devicemodelId="+deviceModel.getId();
    }
    
    @RequestMapping(value = "/gadget/system/updateDeviceModelConfig", method = RequestMethod.POST)
    public ModelAndView updateDeviceModelConfig(@RequestParam("supplierId") int supplierId,
            @RequestParam("idlist") String idlist,
            @RequestParam("displayidlist") String displayidlist,
            @RequestParam("namelist") String namelist,
            @RequestParam("indexlist") String indexlist,
            @RequestParam("typelist") String typelist,
            @RequestParam("subType") String subType,   
            @ModelAttribute("modelForm") DeviceModelConfigVo deviceModelConfig, BindingResult result, SessionStatus status) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");
        DeviceFormResult bindResult = new DeviceFormResult(result.getTarget(), result.getObjectName());
        onBindAndValidate(supplierId, deviceModelConfig, bindResult, false);

        if (bindResult.hasErrors()) {
            mav.addObject("result", "failure");
            mav.addObject("errors", bindResult);
            return mav;
        }

        DeviceModel sessionDeviceModel = deviceModelManager.getDeviceModel(deviceModelConfig.getModelId());

        sessionDeviceModel.setDeviceType(deviceModelConfig.getDeviceType());
        sessionDeviceModel.setDeviceVendor(deviceModelConfig.getDeviceVendor());
        sessionDeviceModel.setName(deviceModelConfig.getModelName());
        sessionDeviceModel.setCode(deviceModelConfig.getCode());
        sessionDeviceModel.setDescription(deviceModelConfig.getDescription());

        if (deviceModelConfig.getImage() == null || deviceModelConfig.getImage().length() == 0) {
            sessionDeviceModel.setImage(aimirFilePath.getDefaultPath() + "/" + CommonConstants.DefaultImg.MCU.getDefaultImg().trim());
        } else {
            sessionDeviceModel.setImage(deviceModelConfig.getImage().trim());
        }

        DeviceModel deviceModel = deviceModelManager.updateDeviceModel(sessionDeviceModel);

        Code mainDeviceType = codeManager.getCode(deviceModelConfig.getMainDeviceType().getId());
        
        if (mainDeviceType != null) {

            if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Modem")) {
                ModemConfig sessionModemConfig = (ModemConfig)deviceModel.getDeviceConfig();
                ModemConfig modemConfig = deviceModelConfig.getModemConfig();
                sessionModemConfig.setName(modemConfig.getName());
                sessionModemConfig.setParserName(modemConfig.getParserName());
                sessionModemConfig.setSaverName(modemConfig.getSaverName());
                sessionModemConfig.setOndemandParserName(modemConfig.getOndemandParserName());
                sessionModemConfig.setOndemandSaverName(modemConfig.getOndemandSaverName());

                modemConfigManager.updateModemConfig(sessionModemConfig);
                
            } else if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Meter")) {
                MeterConfig sessionMeterConfig = (MeterConfig)deviceModel.getDeviceConfig();
                MeterConfig meterConfig = deviceModelConfig.getMeterConfig();
                
                if(sessionMeterConfig==null){
                	sessionMeterConfig =  new MeterConfig();
                	Integer configId = meterConfigManager.getLastId();
                	sessionMeterConfig.setId(configId);
                	sessionMeterConfig.setName(meterConfig.getName());
                	sessionMeterConfig.setDeviceModel(deviceModel);
                	meterConfigManager.addMeterConfig(sessionMeterConfig);
                	
                }
               
                sessionMeterConfig.setName(meterConfig.getName());
                sessionMeterConfig.setMeterClass(meterConfig.getMeterClass());
                sessionMeterConfig.setMeterProtocol(meterConfig.getMeterProtocol() == "" ? null : meterConfig.getMeterProtocol());
                sessionMeterConfig.setPhase(meterConfig.getPhase());
                sessionMeterConfig.setPowerSupplySpec(meterConfig.getPowerSupplySpec());
                sessionMeterConfig.setPulseConst(meterConfig.getPulseConst());
                sessionMeterConfig.setLpInterval(meterConfig.getLpInterval());
                sessionMeterConfig.setParserName(meterConfig.getParserName());
                sessionMeterConfig.setSaverName(meterConfig.getSaverName());
                sessionMeterConfig.setOndemandParserName(meterConfig.getOndemandParserName());
                sessionMeterConfig.setOndemandSaverName(meterConfig.getOndemandSaverName());

                meterConfigManager.updateMeterConfig(sessionMeterConfig);
                
                if(displayidlist !=null && !"".equals(displayidlist)){
               	 List<Object> channelList =  new ArrayList<Object>();
                    
                    String[] chidlist = idlist.split("@");
                    String[] chdisplayidlist = displayidlist.split("@");
                    String[] chnamelist = namelist.split("@");
                    String[] chindexlist = indexlist.split("@");
                    String[] chtypelist = typelist.split("@");
                    
                   String serviceType = codeManager.getCode(Integer.parseInt(subType)).getName();
                   for (int i=0; i<chdisplayidlist.length;i++) {
                       	 Map<String,Object> condition = new HashMap<String,Object>();
                       	 condition.put("id", StringUtil.nullToZero(chidlist[i]));
                       	 condition.put("displayid", chdisplayidlist[i]);
                       	 condition.put("name", chnamelist[i]);
                       	 condition.put("channelIndex", chindexlist[i]);
                       	 condition.put("displayType", chtypelist[i]);
                       	 condition.put("serviceType", serviceType);
                       	 channelList.add(i,condition);
                   }
                    channelConfigManager.saveOrUpdateChannelConfig(meterConfig.getId(),channelList);
                }
            }
        }

        status.setComplete();

        mav.addObject("deviceVendor", deviceVendorManager.getDeviceVendor(deviceModel.getDeviceVendor().getId()));
        mav.addObject("deviceModel", deviceModel);
        mav.addObject("result", "success");
        return mav;
    }
}