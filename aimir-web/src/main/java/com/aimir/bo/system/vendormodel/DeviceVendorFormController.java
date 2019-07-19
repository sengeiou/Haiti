package com.aimir.bo.system.vendormodel;

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
import org.springframework.web.servlet.ModelAndView;

import com.aimir.model.system.DeviceVendor;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.support.IntegerTypeEditor;

@Controller
public class DeviceVendorFormController {

    @Autowired
    DeviceVendorManager deviceVendorManager;

    @Autowired
    SupplierManager supplierManager;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "code",
                new IntegerTypeEditor());

    }

    private void onBindAndValidate(int supplierId, DeviceVendor vendor,
            Errors errors, boolean addCheck) {

        if (!StringUtils.hasLength(vendor.getName())) {

            errors.rejectValue("name", "required", "required");
        } else {
            List<DeviceVendor> vendors = deviceVendorManager
                    .getDeviceVendorByName(supplierId, vendor.getName());

            
            if (addCheck) {
                if (vendors.size() > 0)
                    errors.rejectValue("name", "duplicated", "duplicated");
            } else {
                if (vendors.size() > 0) {
                    
                    if (vendors.size() == 1) {
                        DeviceVendor deviceVendor = vendors.get(0);
                        
                        int vendorId = vendor.getId();
                        int deviceVendorId = deviceVendor.getId();
                        
                        if (vendorId != deviceVendorId) {
                            errors.rejectValue("name", "duplicated",
                                    "duplicated");
                        }
                    } else {
                        errors.rejectValue("name", "duplicated", "duplicated");
                    }
                }

            }

        }

        if (vendor.getCode() == null
                || !StringUtils.hasLength(vendor.getCode() + "")) {
            errors.rejectValue("code", "required", "required");
        } else {
            /*List<DeviceVendor> vendors = deviceVendorManager
                    .getDeviceVendorByCode(supplierId, vendor.getCode());
            if (addCheck) {
                if (vendors.size() > 0)
                    errors.rejectValue("code", "duplicated", "duplicated");
            }else{
                if (vendors.size() > 0) {
                    if (vendors.size() > 0) {
                        if (vendors.size() == 1) {
                            
                            DeviceVendor deviceVendor = vendors.get(0);
                            int vendorId = vendor.getId();
                            int deviceVendorId = deviceVendor.getId();
                            
                            if (vendorId != deviceVendorId) {
                                errors.rejectValue("code", "duplicated",
                                        "duplicated");
                            }
                        } else {
                            errors.rejectValue("code", "duplicated", "duplicated");
                        }
                    }
                }
                
            }*/
        }

    }

    @RequestMapping(value = "/gadget/system/devicevendoradd.do", method = RequestMethod.POST)
    public ModelAndView addDeviceVendor(
            @RequestParam("supplierId") int supplierId,
            @ModelAttribute("vendorForm") DeviceVendor deviceVendor,
            BindingResult result) {

        // @ModelAttribute의 값이 자동으로 ModelMap에 담기게 된다.
        // form id와 @ModelAttribute 인자는 동일해야 한다.
        ModelAndView mav = new ModelAndView("jsonView");

        DeviceFormResult bindResult = new DeviceFormResult(result.getTarget(),
                result.getObjectName());
        onBindAndValidate(supplierId, deviceVendor, bindResult, true);
         
        if (bindResult.hasErrors()) {

            mav.addObject("result", "failure");

            mav.addObject("errors", bindResult);

            return mav;
        }

        // new DeviceVendorValidator().validate(deviceVendor, result);
        // if (result.hasErrors()) {
        // mav.addObject("errors", result.getAllErrors());
        // return mav;
        // }
        deviceVendorManager.addDeviceVendor(deviceVendor);

        mav.addObject("result", "success");
        return mav;
    }

    @RequestMapping(value = "/gadget/system/devicevendoredit.do", method = RequestMethod.POST)
    public ModelAndView updateDeviceVendor(
            @RequestParam("supplierId") int supplierId,
            @ModelAttribute("vendorForm") DeviceVendor deviceVendor,
            BindingResult result) {
        // @ModelAttribute의 값이 자동으로 ModelMap에 담기게 된다.

        ModelAndView mav = new ModelAndView("jsonView");
        DeviceFormResult bindResult = new DeviceFormResult(result.getTarget(),
                result.getObjectName());
        onBindAndValidate(supplierId, deviceVendor, bindResult, false);

        if (bindResult.hasErrors()) {

            mav.addObject("result", "failure");
            mav.addObject("errors", bindResult);
            return mav;
        }
        // new DeviceVendorValidator().validate(deviceVendor, result);
        // if (result.hasErrors()) {
        // mav.addObject("errors", result.getAllErrors());
        // return mav;
        // }
        // 생성자를
        // 이용한
        // fakeSupplier
        DeviceVendor vendor = deviceVendorManager.getDeviceVendor(deviceVendor.getId());
        vendor.setAddress(deviceVendor.getAddress());
        vendor.setCode(deviceVendor.getCode());
        vendor.setDescr(deviceVendor.getDescr());
        vendor.setName(deviceVendor.getName());

        deviceVendorManager.updateDeviceVendor(vendor);

        mav.addObject("result", "success");
        return mav;
    }
}
