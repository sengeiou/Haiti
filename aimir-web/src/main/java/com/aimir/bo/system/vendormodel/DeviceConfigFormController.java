package com.aimir.bo.system.vendormodel;

import java.util.ArrayList;
import java.util.List;

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

import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.MeterConfig;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceConfigManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.MeterConfigManager;
import com.aimir.support.CodeTypeEditor;
import com.aimir.support.DeviceVendorTypeEditor;
import com.aimir.support.IntegerTypeEditor;

@Controller
public class DeviceConfigFormController {
	
	@Autowired
	DeviceConfigManager deviceConfigManager;
	
	@Autowired
	MeterConfigManager meterConfigManager;
	
	@Autowired
	DeviceModelManager deviceModelManager;
	
	@Autowired
	CodeManager codeManager;
	
	@Autowired
	DeviceVendorManager deviceVendorManager;
	
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Code.class, new CodeTypeEditor());
        binder.registerCustomEditor(String.class, "code", new IntegerTypeEditor());
        binder.registerCustomEditor(DeviceVendor.class, new DeviceVendorTypeEditor());
    }

    private void onBindAndValidate(int devicemodelId, MeterConfig deviceConfig, Errors errors, boolean addCheck) {

        if (!StringUtils.hasLength(deviceConfig.getName())) {
            errors.rejectValue("name", "required", "required");
        }
    }

    @RequestMapping(value = "/gadget/system/deviceconfigadd.do", method = RequestMethod.POST)
    public ModelAndView addDeviceConfig(@RequestParam("devicemodelId") int devicemodelId,
            @RequestParam("channelidarray") String channelidarray, 
            @ModelAttribute("configForm") MeterConfig meterDeviceConfig,
            BindingResult result, SessionStatus status) {
        // @ModelAttribute의 값이 자동으로 ModelMap에 담기게 된다.
        // form id와 @ModelAttribute 인자는 동일해야 한다.

        ModelAndView mav = new ModelAndView("jsonView");
        DeviceFormResult bindResult = new DeviceFormResult(result.getTarget(), result.getObjectName());
        onBindAndValidate(devicemodelId, meterDeviceConfig, bindResult, true);
        if (bindResult.hasErrors()) {
            mav.addObject("result", "failure");
            mav.addObject("errors", bindResult);
            return mav;
        }
        // TODO validator
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(devicemodelId);
        MeterConfig deviceConfig = (MeterConfig) meterDeviceConfig;
        deviceConfig.setDeviceModel(deviceModel);

        List<Code> channelList = new ArrayList<Code>();
        String[] channelIdArr = channelidarray.split("_");

        if (channelidarray.length() > 0) {
            for (String channel : channelIdArr) {
                Code channelCode = codeManager.getCode(Integer.parseInt(channel));
                if (channelCode != null)
                    channelList.add(channelCode);
            }
        }

        // deviceConfig.setChannel(channelList);

        meterConfigManager.addMeterConfig(deviceConfig);

        status.setComplete();

        mav.addObject("deviceModel", deviceModel);
        mav.addObject("configModel", deviceConfig);
        mav.addObject("result", "success");
        return mav;
    }

    @RequestMapping(value = "/gadget/system/deviceconfigedit.do", method = RequestMethod.POST)
    public ModelAndView updateDeviceConfig(@RequestParam("devicemodelId") int devicemodelId,
            @RequestParam("channelidarray") String channelidarray, @ModelAttribute("configForm") MeterConfig meterConfig,
            BindingResult result, SessionStatus status) {

        ModelAndView mav = new ModelAndView("jsonView");
        DeviceFormResult bindResult = new DeviceFormResult(result.getTarget(), result.getObjectName());
        onBindAndValidate(devicemodelId, meterConfig, bindResult, true);
        if (bindResult.hasErrors()) {
            mav.addObject("result", "failure");
            mav.addObject("errors", bindResult);
            return mav;
        }

        MeterConfig deviceConfig = (MeterConfig) meterConfig;
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(devicemodelId);
        deviceConfig.setDeviceModel(deviceModel);
        List<Code> channelList = new ArrayList<Code>();
        String[] channelIdArr = channelidarray.split("_");

        if (channelidarray.length() > 0) {
            for (String channel : channelIdArr) {
                Code channelCode = codeManager.getCode(Integer.parseInt(channel));
                if (channelCode != null)
                    channelList.add(channelCode);
            }
        }
        // deviceConfig.setChannel(channelList);
        // meterConfigManager.deleteMeterConfig(meterConfig);
        meterConfigManager.updateMeterConfig(deviceConfig);
        status.setComplete();

        status.setComplete();
        mav.addObject("deviceVendor", deviceVendorManager.getDeviceVendor(deviceConfig.getDeviceModel().getDeviceVendor()
                .getId()));
        mav.addObject("deviceModel", deviceConfig.getDeviceModel());
        mav.addObject("configModel", deviceConfig);
        mav.addObject("result", "success");
        return mav;
    }
}
