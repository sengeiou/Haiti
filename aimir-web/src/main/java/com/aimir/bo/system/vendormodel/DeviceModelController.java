package com.aimir.bo.system.vendormodel;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.MeterProtocol;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.mvm.DisplayChannel;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.ModemConfig;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.model.vo.DeviceModelConfigVo;
import com.aimir.service.device.OperationListManager;
import com.aimir.service.mvm.ChannelConfigManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceConfigManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.MeterConfigManager;
import com.aimir.service.system.MeterProgramManager;
import com.aimir.service.system.ModemConfigManager;
import com.aimir.service.system.ObisCodeManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;

import edu.emory.mathcs.backport.java.util.Collections;

@Controller
public class DeviceModelController {

    Log logger = LogFactory.getLog(DeviceModelController.class);

    @Autowired
    DeviceModelManager deviceModelManager;

    @Autowired
    DeviceVendorManager deviceVendorManager;

    @Autowired
    DeviceConfigManager deviceConfigManager;

    @Autowired
    ModemConfigManager modemConfigManager;

    @Autowired
    MeterConfigManager meterConfigManager;

    @Autowired
    ChannelConfigManager channelConfigManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    OperatorManager operatorManager;

    @Autowired
    MeterProgramManager meterProgramManager;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    ObisCodeManager obisCodeManager;
    
    @Autowired
    OperationListManager operationListManager;

    /******** 2011. 10. 19 문동규 수정 전 소스 Start ************************************************
    // Welcome page
    @RequestMapping(value = "/gadget/system/devicemodelMax.do")
    public ModelMap deviceModelMaxView() {
        return new ModelMap("supplierlist", supplierManager.getSuppliers());
    }
    ******** 2011. 10. 19 문동규 수정 전 소스 End ************************************************/
    /******** 2011. 10. 19 문동규 수정 후 소스 Start ************************************************/
    // Welcome page
    @RequestMapping(value = "/gadget/system/devicemodelMax.do")
    public ModelAndView deviceModelMaxView() {
        ModelAndView mav = new ModelAndView("/gadget/system/devicemodelMax");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("cmdAuth", authMap.get("command"));  // 수정권한(write/command = true)
        mav.addObject("roleName", role.getName());  // role 이름
        mav.addObject("roleId", role.getId());  // role Id
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        return mav;
    }
    /******** 2011. 10. 19 문동규 수정 후 소스 End ************************************************/

    @RequestMapping(value = "/gadget/system/devicemodelMini.do")
    public ModelMap deviceModelMiniView() {
        return new ModelMap("supplierlist", supplierManager.getSuppliers());
    }

    // 공급사 목록
    @RequestMapping(value = "/gadget/system/getSupplierList")
    public ModelAndView getSuppliers() {
        ModelAndView mav = new ModelAndView("jsonView");
//        List<Supplier> supplierList = new ArrayList<Supplier>();
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI
                .authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Operator operator = operatorManager.getOperatorByLoginId(user
                .getLoginId());

        mav.addObject("supplier",operator.getSupplier());
        mav.addObject("supplierList", supplierManager.getSuppliers());
        return mav;
    }

    // 제조사 목록
    @RequestMapping(value = "/gadget/system/vendorlist.do")
    public ModelAndView vendors(@RequestParam("supplierId") int supplierId,
            String treeType) {
        // reference Data 인자 ;
        ModelMap model = new ModelMap();

        List<DeviceVendor> deviceVendorList = deviceVendorManager
                .getDeviceVendorsBySupplierId(supplierId);
        // List<DeviceVendor> deviceModelVendorList = new
        // ArrayList<DeviceVendor>();
        // for (DeviceVendor deviceVendor : deviceVendorList) {
        // List<DeviceModel> deviceModelList = deviceVendor.getDeviceModels();
        // for (DeviceModel deviceModel : deviceModelList) {
        // Code code = deviceModel.getDeviceType();
        // if (code != null && code.getCode().startsWith("1")) {
        //
        // deviceModelVendorList.add(deviceVendor);
        // break;
        // }
        // }
        // }

        model.addAttribute("deviceVendors", deviceVendorList);
        // model.addAttribute("deviceModelVendorList", deviceModelVendorList);

        return new ModelAndView("jsonView", model);
    }


    // 제조사에 따른 장비 목록
    @RequestMapping(value = "/gadget/system/getDeviceModelsByVenendorId.do")
    public ModelAndView getDeviceModelsByVenendorId(@RequestParam("vendorId") int vendorId) {

        List<DeviceModel> deviceModelList    = deviceModelManager.getDeviceModels(vendorId);

        ModelMap model = new ModelMap();
        model.addAttribute("deviceModels", deviceModelList);

        return new ModelAndView("jsonView", model);
    }
    
    // 제조사에 따른 장비 목록
    @RequestMapping(value = "/gadget/system/getDeviceModelsByVenendorName.do")
    public ModelAndView getDeviceModelsByVenendorId(
    		@RequestParam("supplierId") int supplierId,
    		@RequestParam("vendorName") String vendorName) {

        List<DeviceModel> deviceModelList    = deviceModelManager.getDeviceModels(vendorName);
        DeviceModel all = new DeviceModel();
        all.setId(0);
        all.setName("All");
        deviceModelList.add(all);
        
    	Collections.sort(deviceModelList, new Comparator<DeviceModel>(){
    		
 			@Override
 			public int compare(DeviceModel o1, DeviceModel o2) {
				 String firstValue =  String.valueOf(o1.getId());
				 String secondValue = String.valueOf(o2.getId());
			    return firstValue.compareToIgnoreCase(secondValue);
 			}
    	});	
        
        ModelMap model = new ModelMap();
        model.addAttribute("deviceModels", deviceModelList);

        return new ModelAndView("jsonView", model);
    }

    // 제조사에 따른 장비 목록
    @RequestMapping(value = "/gadget/system/getDeviceModelsByDevice.do")
    public ModelAndView getDeviceModelsByVenendorId(@RequestParam("vendorId") int vendorId
                                                  , @RequestParam("deviceType")   String deviceType
                                                  , @RequestParam("subDeviceType")   String subDeviceType) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("vendorId"        , vendorId);
        condition.put("deviceType"      , deviceType);
        condition.put("subDeviceType"   , subDeviceType);

        List<DeviceModel> deviceModelList    = deviceModelManager.getDeviceModels(condition);

        ModelMap model = new ModelMap();
        model.addAttribute("deviceModels", deviceModelList);

        return new ModelAndView("jsonView", model);
    }


    // 장비타입 목록
    @RequestMapping(value = "/gadget/system/modeltypelist.do")
    public ModelAndView types(@RequestParam("code") String code) {
        ModelMap model = new ModelMap();
        model.addAttribute("deviceTypes", codeManager.getChildCodes(code));

        return new ModelAndView("jsonView", model);
    }

    // 모델별 장비 트리
    @RequestMapping(value = "/gadget/system/metermodellist.do")
    public ModelAndView getConfigModelList(
            @RequestParam("supplierId") int supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        List<Code> children = codeManager.getChildCodes("1.3.1");

        List<DeviceModel> meterModelList = new ArrayList<DeviceModel>();

        for (Code code : children) {
            List<DeviceModel> modelList = deviceModelManager
                    .getDeviceModelByTypeId(supplierId, code.getId());

            if (modelList != null) {
                for (DeviceModel model : modelList) {
                    meterModelList.add(model);
                }
            }
        }
        mav.addObject("meterModel", meterModelList);

        return mav;
    }

    // 모델별 장비 트리
    @RequestMapping(value = "/gadget/system/codelist.do")
    public ModelAndView getCodeList(@RequestParam("code") String code) {

        ModelAndView mav = new ModelAndView("jsonView");

        List<Code> meterChannelList = codeManager.getChildCodes(code);

        mav.addObject("meterChannel", meterChannelList);

        return mav;
    }
    
    @RequestMapping(value="/gadget/system/getMeterProtocol.do")
    public ModelAndView getMeterProtocol() {
    	ModelAndView mav = new ModelAndView("jsonView");
    	/*
		List<String> meterProtocolList = new ArrayList<String>();
    	MeterProtocol[] meterProtocolValue = MeterProtocol.values();
    	for (MeterProtocol meterProtocol : meterProtocolValue) {
			meterProtocolList.add(meterProtocol.getName());
		}
		*/
		MeterProtocol[] meterProtocolList = MeterProtocol.values(); 
    	mav.addObject("data",meterProtocolList);
    	
    	return mav;
    }

    // 모델별 장비 트리
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/gadget/system/modeltree.do")
    public ModelAndView getModelTree(@RequestParam("supplierId") int supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        List<JsonTree> jsonTrees = new ArrayList<JsonTree>();
        List<Code> children = codeManager.getChildCodes("1");

        List modelList = null;
//      boolean itemAdd = false;

        for (Code code : children) {
            JsonTree jsonTree = new JsonTree();
            List devicetypeList = getChildList(code);

            if(devicetypeList.size() ==0)
                continue;

            jsonTree.setData(code);
            modelList = getChildList(supplierId, devicetypeList);

            List vendors = new ArrayList();
            List models = new ArrayList();
//            List configs = new ArrayList();
//            getChildList(modelList, vendors, models, configs);
            getChildList(modelList, vendors, models);

            //jsonTree.setChildren3(configs);
            jsonTree.setChildren2(models);
            jsonTree.setChildren1(vendors);
            jsonTree.setChildren(devicetypeList);

            jsonTrees.add(jsonTree);

        }
        JsonTree unknownJsonTree = getUnknown(supplierId);

        jsonTrees.add(unknownJsonTree);

        // if (itemAdd) {
        mav.addObject("jsonTrees", jsonTrees);
        // } else {
        // mav.addObject("jsonTrees", new ArrayList<JsonTree>());
        // }
        return mav;
    }

    @SuppressWarnings("rawtypes")
    private JsonTree getUnknown(int supplierId) {
        JsonTree jsonTree = new JsonTree();
//        List config = new ArrayList();
        Code code = new Code();
        code.setName("Unknown");
        code.setId(999999);

        List<DeviceModel> unknownList = deviceModelManager
                .getDeviceModelByTypeIdUnknown(supplierId);

        List vendors = new ArrayList();
        List models = new ArrayList();
//        List configs = new ArrayList();
//        getChildList(unknownList, vendors, models, configs);
        getChildList(unknownList, vendors, models);

        jsonTree.setData(code);
        jsonTree.setChildren(addArrayList(code));
        jsonTree.setChildren1(addArrayList(vendors));
        jsonTree.setChildren2(addArrayList(models));
        //jsonTree.setChildren3(addArrayList(configs));

        return jsonTree;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List addArrayList(Object list) {
        List arrayList = new ArrayList();
        arrayList.add(list);
        return arrayList;
    }

    /*
     * private void getModelSplitList(List models, List resultVendor, List
     * resultModel, List resultConfig) { int oldVendorId = -1; List modelList =
     * new ArrayList(); List configs = new ArrayList(); List vendors = new
     * ArrayList();
     *
     * for (int i = 0; i < models.size(); i++) { DeviceModel modelType =
     * (DeviceModel) models.get(i); int vendorId =
     * modelType.getDeviceVendor().getId();
     *
     * if (vendorId != oldVendorId) {
     * resultVendor.add(modelType.getDeviceVendor()); if (modelList.isEmpty()) {
     * modelList.add(modelType); configs.add(modelType.getDeviceConfig()); }
     * else { resultConfig.add(configs); resultModel.add(modelList);
     *
     * modelList = new ArrayList(); configs = new ArrayList();
     *
     * modelList.add(modelType); configs.add(modelType.getDeviceConfig()); } }
     * else { modelList.add(modelType);
     * configs.add(modelType.getDeviceConfig()); } oldVendorId = vendorId; }
     * resultConfig.add(configs); resultModel.add(modelList);
     *
     * }
     */

//    private void getModelSplitList(List models, List resultVendor, List resultModel, List resultConfig) {
//        int oldVendorId = -1;
//        List modelList = new ArrayList();
//        List configs = new ArrayList();
//
//        HashMap vendorsHashMap = new HashMap();
//
//        int index = 0;
//        for (int i = 0; i < models.size(); i++) {
//            DeviceModel modelType = (DeviceModel) models.get(i);
//            if (!vendorsHashMap
//                    .containsKey(modelType.getDeviceVendor().getId())) {
//                vendorsHashMap.put(modelType.getDeviceVendor().getId(), index);
//                resultVendor.add(modelType.getDeviceVendor());
//                index++;
//            }
//        }
//
//        List[] modelListArray = new ArrayList[vendorsHashMap.size()];
//        List[] configsArray = new ArrayList[vendorsHashMap.size()];
//
//        for (int i = 0; i < models.size(); i++) {
//            DeviceModel modelType = (DeviceModel) models.get(i);
//
//            int vendorIndex = (Integer) vendorsHashMap.get(modelType
//                    .getDeviceVendor().getId());
//
//
//            if (modelListArray[vendorIndex] != null) {
//                modelListArray[vendorIndex].add(modelType);
//                configsArray[vendorIndex].add(modelType.getDeviceConfig());
//            } else {
//                modelListArray[vendorIndex] = new ArrayList();
//                configsArray[vendorIndex] = new ArrayList();
//                modelListArray[vendorIndex].add(modelType);
//                configsArray[vendorIndex].add(modelType.getDeviceConfig());
//            }
//        }
//        for (int i = 0; i < resultVendor.size(); i++) {
//            resultConfig.add(configsArray[i]);
//            resultModel.add(modelListArray[i]);
//        }
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void getModelSplitList(List models, List resultVendor, List resultModel) {
//        int oldVendorId = -1;
//        List modelList = new ArrayList();
//        List configs = new ArrayList();

        HashMap vendorsHashMap = new HashMap();

        int index = 0;
        for (int i = 0; i < models.size(); i++) {
            DeviceModel modelType = (DeviceModel) models.get(i);
            if (!vendorsHashMap.containsKey(modelType.getDeviceVendor().getId())) {
                vendorsHashMap.put(modelType.getDeviceVendor().getId(), index);
                resultVendor.add(modelType.getDeviceVendor());
                index++;
            }
        }

        List[] modelListArray = new ArrayList[vendorsHashMap.size()];
//        List[] configsArray = new ArrayList[vendorsHashMap.size()];

        for (int i = 0; i < models.size(); i++) {
            DeviceModel modelType = (DeviceModel) models.get(i);

            int vendorIndex = (Integer) vendorsHashMap.get(modelType.getDeviceVendor().getId());

            if (modelListArray[vendorIndex] != null) {
                modelListArray[vendorIndex].add(modelType);
//                configsArray[vendorIndex].add(modelType.getDeviceConfig());
            } else {
                modelListArray[vendorIndex] = new ArrayList();
//                configsArray[vendorIndex] = new ArrayList();
                modelListArray[vendorIndex].add(modelType);
//                configsArray[vendorIndex].add(modelType.getDeviceConfig());
            }
        }
        for (int i = 0; i < resultVendor.size(); i++) {
//            resultConfig.add(configsArray[i]);
            resultModel.add(modelListArray[i]);
        }
    }

//    private void getChildList(List models, List resultVendor, List resultModel, List resultConfig) {
//        if (models == null)
//            return;
//
//        if (models.size() > 0) {
//
//            if (models.get(0) instanceof List) {
//
//                for (int i = 0; i < models.size(); i++) {
//                    List modelType = (List) models.get(i);
//                    List modelList = new ArrayList();
//                    List vendors = new ArrayList();
//                    List configs = new ArrayList();
//                    resultVendor.add(vendors);
//                    resultModel.add(modelList);
//                    resultConfig.add(configs);
//                    getChildList(modelType, vendors, modelList, configs);
//                }
//
//            } else if (models.get(0) instanceof DeviceModel) {
//                getModelSplitList(models, resultVendor, resultModel, resultConfig);
//            }
//        } else {
//
//        }
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void getChildList(List models, List resultVendor, List resultModel) {
        if (models == null)
            return;

        if (models.size() > 0) {

            if (models.get(0) instanceof List) {

                for (int i = 0; i < models.size(); i++) {
                    List modelType = (List) models.get(i);
                    List modelList = new ArrayList();
                    List vendors = new ArrayList();
//                    List configs = new ArrayList();
                    resultVendor.add(vendors);
                    resultModel.add(modelList);
//                    resultConfig.add(configs);
                    getChildList(modelType, vendors, modelList);
                }

            } else if (models.get(0) instanceof DeviceModel) {
                getModelSplitList(models, resultVendor, resultModel);
            }
        }
    }

    // 제조사별 장비 트리
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/gadget/system/vendortree.do")
    public ModelAndView getVendorTree(@RequestParam("supplierId") int supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        List<JsonTree> jsonTrees = new ArrayList<JsonTree>();
        List<DeviceVendor> vendorList = deviceVendorManager.getDeviceVendorsBySupplierId(supplierId);

        for (DeviceVendor vendor : vendorList) {
            JsonTree jsonTree = new JsonTree();
            List models = getChildList(vendor);
            //List deviceConfigList = getChildList(models);

            //jsonTree.setChildren1(deviceConfigList);
            jsonTree.setData(vendor);
            jsonTree.setChildren(models);
            jsonTrees.add(jsonTree);
        }

        mav.addObject("jsonTrees", jsonTrees);
        return mav;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getChildList(int supplierId, Object parent) {
        List retList = new ArrayList();
        if (parent instanceof List) {
            List parents = (List) parent;
            for (int i = 0; i < parents.size(); i++) {
                Object parentObj = parents.get(i);
                retList.add(getChildList(supplierId, parentObj));
            }
        } else if (parent instanceof Code) {
            Code code = (Code) parent;
            retList = deviceModelManager.getDeviceModelByTypeId(supplierId,
                    code.getId());
        }
        return retList;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getChildList(Object parent) {
        List retList = new ArrayList();
        if (parent instanceof List) {
            List parents = (List) parent;
            for (int i = 0; i < parents.size(); i++) {
                Object parentObj = parents.get(i);
                retList.add(getChildList(parentObj));
            }

        } else if (parent instanceof DeviceVendor) {
            DeviceVendor vendor = (DeviceVendor) parent;
            retList = deviceModelManager.getDeviceModels(vendor.getId());
        } else if (parent instanceof DeviceModel) {
            DeviceModel model = (DeviceModel) parent;
            retList.add(model.getDeviceConfig());
        } else if (parent instanceof Code) {
            Code code = (Code) parent;
            Set<Code> codeChild = code.getChildren();
            Iterator iterator = codeChild.iterator();
            boolean add = false;
            while(iterator.hasNext()){
                Code iCode = (Code)iterator.next();

                if(iCode.getName().endsWith("Type")){
                    add = true;
                    break;
                }
            }
            if(add)
                retList = codeManager.getChildCodes(code.getCode() + ".1");
        }
        return retList;
    }

    // 제조사 정보조회
    @RequestMapping(value = "/gadget/system/vendorinfo.do")
    public ModelAndView getDeviceVendorInfo(
            @RequestParam("devicevendorId") int devicevendorId) {
        ModelMap model = new ModelMap("deviceVendor", deviceVendorManager
                .getDeviceVendor(devicevendorId));
        return new ModelAndView("jsonView", model);
    }

    // 제조사 정보조회
    @RequestMapping(value = "/gadget/system/configinfo.do")
    public ModelAndView getDeviceConfigInfo(
            @RequestParam("deviceconfigId") int deviceconfigId) {

        DeviceConfig deviceConfig = deviceConfigManager
                .getDeviceConfig(deviceconfigId);
        DeviceModel deviceModel = deviceConfig.getDeviceModel();

        // List<MeterConfig> meterConfig = deviceConfig.getMeterConfig();

        MeterConfig meterConfig = meterConfigManager
                .getMeterConfig(deviceconfigId);
        ModelMap model = new ModelMap();
        // System.out.println("meterConfig:"+meterConfig);
        // for( MeterConfig meter:meterConfig ){
        // System.out.println("############");
        // System.out.println(meter.getId());
        // System.out.println(meter.getLpInterval());
        // }
        model.addAttribute("deviceModel", deviceModel);
        model.addAttribute("meterConfig", meterConfig);
        model.addAttribute("channels", meterConfig.getChannels());

        return new ModelAndView("jsonView", model);
    }

    // 장비모델 정보조회
    @RequestMapping(value = "/gadget/system/modelinfo.do")
    public ModelAndView getDeviceModelInfo(@RequestParam("devicemodelId") int devicemodelId, @RequestParam("supplierId") int supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(devicemodelId);
        int lastchannelIndex=0;
        List<Object> getchannelList = new ArrayList<Object>();
        DeviceModelConfigVo deviceModelConfig = new DeviceModelConfigVo();
        if (deviceModel != null) {
            // 추가
            deviceModelConfig.setDeviceModel(deviceModel);

            if (deviceModel.getDeviceType() != null) {
                Code mainDeviceType = deviceModel.getDeviceType().getParent()
                        .getParent();

                if (mainDeviceType != null) {
                    deviceModelConfig.setMainDeviceType(mainDeviceType);
                    deviceModelConfig.setMainDeviceTypeName(mainDeviceType
                            .getName());
                    mav.addObject("mainDeviceTypeDesc", mainDeviceType.getDescr());

                    if (StringUtil.nullToBlank(mainDeviceType.getName())
                            .equals("Modem")) {
                        ModemConfig modemConfig = (ModemConfig) deviceModel
                                .getDeviceConfig();

                        if (modemConfig != null) {
                            deviceModelConfig.setModemConfig(modemConfig);
                        }
                    } else if (StringUtil.nullToBlank(mainDeviceType.getName())
                            .equals("Meter")) {
                        MeterConfig meterConfig = (MeterConfig) deviceModel
                                .getDeviceConfig();

                        if (meterConfig != null) {
                            deviceModelConfig.setMeterConfig(meterConfig);
                            
                            Supplier supplier = supplierManager.getSupplier(supplierId);
                            DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

                            Set<ChannelConfig> channelList = meterConfig
                                    .getChannels();
                            int count = 1;

                            for (ChannelConfig channelconfig : channelList) {
                                Map<String, Object> channelMap = new HashMap<String, Object>();
                                DisplayChannel displaychannel = channelConfigManager
                                        .getDisplayChannel(channelconfig
                                                .getChannelId());
                                channelMap.put("no", count);
                                channelMap.put("id", channelconfig.getId());
                                channelMap.put("displayid", displaychannel.getId());
                                channelMap.put("name", displaychannel.getName());
                                channelMap.put("serviceType",displaychannel.getServiceType());
                                channelMap.put("unit", displaychannel.getUnit());
                                channelMap.put("channelIndex",dfMd.format(channelconfig.getChannelIndex()));
                                channelMap.put("displayType",channelconfig.getDisplayType());
                                channelMap.put("datatype",channelconfig.getDataType());

                                int tempIndex = channelconfig.getChannelIndex();

                                if (lastchannelIndex < tempIndex) {
                                    lastchannelIndex = tempIndex;
                                }

                                getchannelList.add(channelMap);
                                count++;
                            }

                            // mav.addObject("channels",
                            // meterConfig.getChannels());

                        }
                    }
                }
            }
        }
        mav.addObject("lastchannelIndex", lastchannelIndex);
        mav.addObject("channels", getchannelList);
        mav.addObject("deviceModelConfig", deviceModelConfig);

        String[] namesOfContain = CommonConstants.EnableCommandModel.getNameOfContain(deviceModelConfig.getModelName());

        mav.addObject("namesOfContain",namesOfContain);

        return mav;
    }

    // 장비모델 정보조회
    @RequestMapping(value = "/gadget/system/vendormodeltypelist.do")
    public ModelAndView getVendorModelTypes() {
        List<Code> children = codeManager.getChildCodes("1");
        List<Code> deviceTypeList = new ArrayList<Code>();
        for (Code code : children) {
            List<Code> codeList = codeManager.getChildCodes(code.getCode()
                    + ".1");
            for (Code deviceType : codeList) {
                deviceTypeList.add(deviceType);
            }
        }

        ModelMap model = new ModelMap();

        model.addAttribute("deviceType", deviceTypeList);
        return new ModelAndView("jsonView", model);
    }

    // 제조사 정보삭제
    @RequestMapping(value = "/gadget/system/devicevendordelete.do")
    public ModelAndView deleteDeviceVendor(
            @RequestParam("devicevendorId") int devicevendorId) {
        ModelAndView mav = new ModelAndView("jsonView");
        DeviceVendor deviceVendor = deviceVendorManager
                .getDeviceVendor(devicevendorId);
        mav.addObject("id", deviceVendor.getId());

        deviceVendorManager.deleteDeviceVendor(deviceVendor);

        mav.addObject("result", "success");
        return mav;
    }

    // 장비모델 정보삭제
    @RequestMapping(value = "/gadget/system/deleteDeviceModelConfig")
    public ModelAndView deleteDeviceModelConfig(@RequestParam("modelId") int modelId) {
        ModelAndView mav = new ModelAndView("jsonView");
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(modelId);

        deviceModelManager.deleteDeviceModel(deviceModel);

        mav.addObject("modelId", modelId);
        mav.addObject("result", "success");
        return mav;
    }

    // 장비설정 정보삭제
    @RequestMapping(value = "/gadget/system/deviceconfigdelete.do")
    public ModelAndView deleteDeviceConfig(
            @RequestParam("deviceconfigId") int deviceconfigId,
            @RequestParam("devicemodelId") int devicemodelId) {
        // DeviceModel deviceModel = deviceModelManager
        // .getDeviceModel(devicemodelId);
        // deviceModel.setDeviceConfig(null);

        // List<DeviceConfig> deviceConfig =
        // deviceConfigManager.getDeviceConfigByModelId(devicemodelId);
        MeterConfig config = (MeterConfig) meterConfigManager
                .getMeterConfig(deviceconfigId);
        // config.setDeviceConfig(deviceConfig.get(0));
        meterConfigManager.deleteMeterConfig(config);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("id", deviceconfigId);
        mav.addObject("result", "success");
        return mav;
    }

    /******** 2011. 10. 18 문동규 추가 소스 Start ************************************************/
    /**
     * method name : getMainDeviceTypeComboData<b/>
     * method Desc : Device Vendor Model
     *
     * @param supplierId
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/gadget/system/getMainDeviceTypeComboData")
    public ModelAndView getMainDeviceTypeComboData(@RequestParam("supplierId") int supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Code> children = codeManager.getChildCodes("1");
        List<Code> result = new ArrayList<Code>();

        for (Code code : children) {
            List devicetypeList = getChildList(code);

            if (devicetypeList.size() ==0) {
                continue;
            }

            result.add(code);
        }

        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getSubDeviceTypeComboData<b/>
     * method Desc :
     *
     * @param codeName
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/system/getSubDeviceTypeComboData")
    public ModelAndView getSubDeviceTypeComboData(@RequestParam("mainTypeCode") String mainTypeCode) {

        ModelAndView mav = new ModelAndView("jsonView");
        Code code = codeManager.getCodeByCode(mainTypeCode);

        List<Code> subTypeList = getChildList(code);

        mav.addObject("result", subTypeList);
        return mav;
    }
    /******** 2011. 10. 18 문동규 추가 소스 End ************************************************/

    /**
     * method name : getMeterProgramLogList<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param configId
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/gadget/system/getMeterProgramLogList")
    @Deprecated
    public ModelAndView getMeterProgramLogList(@RequestParam("configId") String configId,
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Integer supplierId = null;
        conditionMap.put("configId", Integer.parseInt(configId));
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);

        // ESAPI.setAuthenticator(new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            supplierId = user.getRoleData().getSupplier().getId();
        } else {
            supplierId = 0;
        }

        conditionMap.put("supplierId", supplierId);

        List<Map<String, Object>> result = meterProgramManager.getMeterProgramLogList(conditionMap);

        mav.addObject("gridDatas", result);
        mav.addObject("total", meterProgramManager.getMeterProgramLogListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getMeterProgramLogListRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param supplierId
     * @param configId
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/gadget/system/getMeterProgramLogListRenew")
    public ModelAndView getMeterProgramLogListRenew(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("configId") String configId,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit) {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer totalCount = 0;

        if (!configId.isEmpty()) {
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("supplierId", supplierId);
            conditionMap.put("configId", Integer.parseInt(configId));
            conditionMap.put("page", page);
            conditionMap.put("limit", limit);

            result = meterProgramManager.getMeterProgramLogListRenew(conditionMap);
            totalCount = meterProgramManager.getMeterProgramLogListTotalCountRenew(conditionMap);
        }

        mav.addObject("result", result);
        mav.addObject("totalCount", totalCount);

        return mav;
    }

    /**
     * method name : getMeterProgramSettingsData<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Settings 값을 조회한다.
     *
     * @param meterProgramId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getMeterProgramSettingsData")
    public ModelAndView getMeterProgramSettingsData(@RequestParam("meterProgramId") Integer meterProgramId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("meterProgramId", meterProgramId);

        String result = meterProgramManager.getMeterProgramSettingsData(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : saveMeterProgram<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program 정보를 저장한다.
     *
     * @param configId
     * @param settings
     * @return
     */
    @RequestMapping(value = "/gadget/system/saveMeterProgram")
    public ModelAndView saveMeterProgram(@RequestParam("configId") Integer configId,
            @RequestParam("settings") String settings,
            @RequestParam("meterProgramKind") String meterProgramKind) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("configId", configId);
        conditionMap.put("settings", settings);
        conditionMap.put("kind", meterProgramKind);

        try {
            meterProgramManager.saveMeterProgram(conditionMap);
            mav.addObject("result", "success");
        } catch(Exception e) {
            mav.addObject("result", "fail");
        }


        return mav;
    }

    /**
     * method name : getMeterProgramKindComboData<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Kind Combo 데이터를 조회한다.
     *
     * @return
     */
    @SuppressWarnings("unused")
    @RequestMapping(value = "/gadget/system/getMeterProgramKindComboData")
    public ModelAndView getMeterProgramKindComboData(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("jsonView");

        //resource 에 접근하기 위해 Context 를 구한다.
        ServletContext servletContext = request.getSession().getServletContext();
        WebApplicationContext webContext =  WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        //FIXME:일단 TOU Calendar와 Display Item Select 만 보이도록 설정한다.
        List<MeterProgramKind> constant = new ArrayList<CommonConstants.MeterProgramKind>();
        constant.add(MeterProgramKind.DisplayItemSetting);
        constant.add(MeterProgramKind.TOUCalendar);

        for (int i = 0; i < constant.size(); i++) {
             Map<String, Object> map = new HashMap<String, Object>();
             map.put("id", constant.get(i).name());
             map.put("name", constant.get(i).getName());
             map.put("template", "");

             result.add(map);
        }

        mav.addObject("result", result);

        return mav;
    }

    /**
     * CSV Template 파일을 classpath 에서 검색해서 읽어낸다.
     * @param name
     * @param webContext
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private String readTemplate(String name, WebApplicationContext webContext) throws IOException {
        StringBuilder classPath = new StringBuilder();
        classPath.append("classpath*:/CSVTemplate/");
        classPath.append(name);
        classPath.append("*");

        StringBuilder sbFileContent = new StringBuilder();
        Resource[] resource = webContext.getResources(classPath.toString());

        if (resource != null && resource.length != 0) {
            InputStream inputStream = resource[0].getInputStream();
            int ch;
            while((ch=inputStream.read()) != -1) {
                sbFileContent.append((char)ch);
            }
        }

        return sbFileContent.toString();
    }

    /*
     * displaychannel에 정보를 조회한다.*/
    @RequestMapping(value="/gadget/system/getExceptDisplayChannelList")
    public ModelAndView getExceptDisplayChannelList(
            @RequestParam("id") Integer id,  @RequestParam("supplierId") Integer supplierId)
    {

        List<Object> displaychannelList = new ArrayList<Object>();
        if(id != null){
            displaychannelList = channelConfigManager.getExceptDisplayChannelList(id, supplierId);
        }else{
            displaychannelList = channelConfigManager.getDisplayChannelList(null, supplierId);
        }
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("exceptdisplaychannelList", displaychannelList)
           .addObject("result","success");

        return mav;
    }

    /*
     * displaychannel에 정보를 조회한다.*/
    @RequestMapping(value="/gadget/system/getSingleExceptDisplayChannel")
    public ModelAndView getSingleExceptDisplayChannel(
            @RequestParam("id") Integer id, @RequestParam("supplierId") Integer supplierId)
    {

        List<Object> displaychannelList = new ArrayList<Object>();
        if(id != null){
            displaychannelList = channelConfigManager.getDisplayChannelList(id, supplierId);
        }
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("singleExceptChannelData", displaychannelList.get(0))
           .addObject("result","success");

        return mav;
    }
    
    // 장비모델 정보조회
    @RequestMapping(value = "/gadget/system/modelinfo2.do")
    public ModelAndView getDeviceModelInfo2(@RequestParam("devicemodelId") int devicemodelId, 
    		@RequestParam("supplierId") int supplierId,
    		@RequestParam("roleId") int roleId) {
        ModelAndView mav = new ModelAndView("jsonView");
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(devicemodelId);
        int lastchannelIndex=0;
        List<Object> getchannelList = new ArrayList<Object>();
        DeviceModelConfigVo deviceModelConfig = new DeviceModelConfigVo();
        if (deviceModel != null) {
            // 추가
            deviceModelConfig.setDeviceModel(deviceModel);

            if (deviceModel.getDeviceType() != null) {
                Code mainDeviceType = deviceModel.getDeviceType().getParent().getParent();

                if (mainDeviceType != null) {
                    deviceModelConfig.setMainDeviceType(mainDeviceType);
                    deviceModelConfig.setMainDeviceTypeName(mainDeviceType
                            .getName());
                    mav.addObject("mainDeviceTypeDesc", mainDeviceType.getDescr());

                    if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Modem")) {
                        ModemConfig modemConfig = (ModemConfig) deviceModel.getDeviceConfig();

                        if (modemConfig != null) {
                            deviceModelConfig.setModemConfig(modemConfig);
                        }
                    } else if (StringUtil.nullToBlank(mainDeviceType.getName())
                            .equals("Meter")) {
                        MeterConfig meterConfig = (MeterConfig) deviceModel.getDeviceConfig();

                        if (meterConfig != null) {
                            deviceModelConfig.setMeterConfig(meterConfig);
                            
                            Supplier supplier = supplierManager.getSupplier(supplierId);
                            DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

                            Set<ChannelConfig> channelList = meterConfig
                                    .getChannels();
                            int count = 1;

                            for (ChannelConfig channelconfig : channelList) {
                                Map<String, Object> channelMap = new HashMap<String, Object>();
                                DisplayChannel displaychannel = channelConfigManager.getDisplayChannel(channelconfig.getChannelId());
                                channelMap.put("no", count);
                                channelMap.put("id", channelconfig.getId());
                                channelMap.put("displayid", displaychannel.getId());
                                channelMap.put("name", displaychannel.getName());
                                channelMap.put("serviceType",displaychannel.getServiceType());
                                channelMap.put("unit", displaychannel.getUnit());
                                channelMap.put("channelIndex",dfMd.format(channelconfig.getChannelIndex()));
                                channelMap.put("displayType",channelconfig.getDisplayType());
                                channelMap.put("datatype",channelconfig.getDataType());

                                int tempIndex = channelconfig.getChannelIndex();

                                if (lastchannelIndex < tempIndex) {
                                    lastchannelIndex = tempIndex;
                                }

                                getchannelList.add(channelMap);
                                count++;
                            }

                        }
                    }
                }
            }
        }
        List<String> namesOfContain = operationListManager.getAvailableOperationList(devicemodelId, roleId);
        mav.addObject("lastchannelIndex", lastchannelIndex);
        mav.addObject("channels", getchannelList);
        mav.addObject("deviceModelConfig", deviceModelConfig);
//        String[] namesOfContain = CommonConstants.EnableCommandModel.getNameOfContain(deviceModelConfig.getModelName());

        mav.addObject("namesOfContain",namesOfContain);
        
        return mav;
    }

}