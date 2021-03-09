package com.aimir.service.device.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.BatteryStatus;
import com.aimir.constants.CommonConstants.CustomerSearchType;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemNetworkType;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.UsingMCUType;
import com.aimir.constants.DeviceRegistrationFormat.McuEnum;
import com.aimir.constants.DeviceRegistrationFormat.MeterEnum;
import com.aimir.constants.DeviceRegistrationFormat.ModemEnum;
import com.aimir.dao.device.ACDDao;
import com.aimir.dao.device.ConverterDao;
import com.aimir.dao.device.DeviceRegistrationDao;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.GasMeterDao;
import com.aimir.dao.device.HMUDao;
import com.aimir.dao.device.HeatMeterDao;
import com.aimir.dao.device.IEIUDao;
import com.aimir.dao.device.IHDDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MCUVarDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterMapperDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.PLCIUDao;
import com.aimir.dao.device.SimCardDao;
import com.aimir.dao.device.SolarPowerMeterDao;
import com.aimir.dao.device.SubGigaDao;
import com.aimir.dao.device.VolumeCorrectorDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.device.ZBRepeaterDao;
import com.aimir.dao.device.ZEUMBusDao;
import com.aimir.dao.device.ZEUPLSDao;
import com.aimir.dao.device.ZMUDao;
import com.aimir.dao.device.ZRUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.ACD;
import com.aimir.model.device.Converter;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HMU;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.IHD;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUCodiBinding;
import com.aimir.model.device.MCUCodiDevice;
import com.aimir.model.device.MCUCodiMemory;
import com.aimir.model.device.MCUCodiNeighbor;
import com.aimir.model.device.MCUVar;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterMapper;
import com.aimir.model.device.Modem;
import com.aimir.model.device.PLCIU;
import com.aimir.model.device.SimCard;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service(value = "deviceRegistrationManager")
public class DeviceRegistrationManagerImpl implements DeviceRegistrationManager {

	private static Log logger = LogFactory.getLog(DeviceRegistrationManagerImpl.class);

	@Autowired
	DeviceRegistrationDao deviceRegistrationDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	MCUCodiDao mcuCodiDao;

	@Autowired
	MCUVarDao mcuVarDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	EnergyMeterDao energyMeterDao;

	@Autowired
	WaterMeterDao waterMeterDao;

	@Autowired
	GasMeterDao gasMeterDao;

	@Autowired
	HeatMeterDao heatMeterDao;

	@Autowired
	VolumeCorrectorDao volumeCorrectorDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	ZRUDao zRUDao;

	@Autowired
	ZMUDao zMUDao;

	@Autowired
	ZEUPLSDao zEUPLSDao;

	@Autowired
	MMIUDao mMIUDao;

	@Autowired
	IEIUDao iEIUDao;

	@Autowired
	ZEUMBusDao zEUMBusDao;

	@Autowired
	IHDDao iHDDao;

	@Autowired
	ACDDao aCDDao;

	@Autowired
	HMUDao hMUDao;

	@Autowired
	PLCIUDao pLCIUDao;

	@Autowired
	ZBRepeaterDao zBRepeaterDao;

	@Autowired
	ConverterDao converterDao;

	@Autowired
	SubGigaDao subGigaDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	EndDeviceDao endDeviceDao;

	@Autowired
	SolarPowerMeterDao solarPowerMeterDao;

	@Autowired
	CustomerDao customerDao;

	@Autowired
	ContractDao contractDao;
	
	@Autowired
	SimCardDao simCardDao;
	
	@Autowired
	MeterMapperDao meterMapperDao;

	// 포맷 파일 저장 위치
	private String ctxRoot;

	public List<Object> getMiniChart(Map<String, Object> condition) {

		String viewType = StringUtil.nullToBlank(condition.get("viewType"));

		List<Object> result = new ArrayList<Object>();

		StringBuffer inCondition = new StringBuffer("''");

		// MCU
		if (DeviceType.MCU.toString().equals(viewType)) {
			for (UsingMCUType _mcuType : UsingMCUType.values()) {
				inCondition.append(", '" + _mcuType.toString() + "'");
			}
			inCondition.append(", '" + DeviceType.MCU.toString() + "'");

		}

		// MODEM
		if (DeviceType.Modem.toString().equals(viewType))
			for (ModemType _modemType : ModemType.values()) {
				for (TargetClass _targetClass : TargetClass.values()) {
					if (_targetClass.toString().equals(_modemType.toString())) {
						inCondition.append(", '" + _targetClass + "'");
						break;
					}
				}
			}

		// METER
		if (DeviceType.Meter.toString().equals(viewType))
			for (MeterType _meterType : MeterType.values()) {
				for (TargetClass _targetClass : TargetClass.values()) {
					if (_targetClass.toString().equals(_meterType.toString())) {
						inCondition.append(", '" + _targetClass + "'");
						break;
					}
				}
			}

		// Customer
		if (TargetClass.Customer.toString().equals(viewType))
			for (CustomerSearchType _customerSearchType : CustomerSearchType.values()) {
				for (TargetClass _targetClass : TargetClass.values()) {
					if (_targetClass.toString().equals(_customerSearchType.toString())) {
						inCondition.append(", '" + _targetClass + "'");
						break;
					}
				}
			}

		condition.put("inCondition", inCondition.toString());

		result = deviceRegistrationDao.getMiniChart(condition);
		return result;
	}

	public Map<String, Object> getAssetMiniChart(Map<String, String> condition) {

		Map<String, Object> result = new HashMap<String, Object>();

		int mcuCount = mcuDao.getMCUCountByCondition(condition);
		int modemCount = modemDao.getModemCount(condition);
		int meterCount = meterDao.getActiveMeterCount(condition);
		int customerCount = customerDao.getCustomerCount(condition);
		int contractCount = contractDao.getContractCount(condition);

		result.put("mcuCount", mcuCount);
		result.put("modemCount", modemCount);
		result.put("meterCount", meterCount);
		result.put("customerCount", customerCount);
		result.put("contractCount", contractCount);

		return result;
	}

	public List<DeviceVendor> getVendorListBySubDeviceType(Map<String, Object> condition) {

		String deviceType = StringUtil.nullToBlank(condition.get("deviceType"));
		String subDeviceType = StringUtil.nullToBlank(condition.get("subDeviceType"));

		if (subDeviceType.length() == 0) {

			List<Code> typeList = new ArrayList<Code>();

			// MCU
			if (DeviceType.MCU.toString().equals(deviceType))
				typeList = codeDao.getChildCodes(Code.MCU_TYPE);

			// MODEM
			if (DeviceType.Modem.toString().equals(deviceType))
				typeList = codeDao.getChildCodes(Code.MODEM_TYPE);

			// METER
			if (DeviceType.Meter.toString().equals(deviceType))
				typeList = codeDao.getChildCodes(Code.METER_TYPE);

			int typeListlen = 0;
			StringBuffer deviceCode = new StringBuffer();

			if (typeList != null)
				typeListlen = typeList.size();

			for (int i = 0; i < typeListlen; i++) {
				Code code = typeList.get(i);

				if (i == 0)
					deviceCode.append(code.getId().toString());
				else
					deviceCode.append("," + code.getId().toString());
			}

			condition.put("subDeviceType", deviceCode.toString());

		}

		return deviceRegistrationDao.getVendorListBySubDeviceType(condition);
	}

	public List<Object> getDeviceRegLog(Map<String, Object> condition) {

		return deviceRegistrationDao.getDeviceRegLog(condition);
	}

	public String getTitleName(String excel, String ext) {

		StringBuffer sb = new StringBuffer();

		try {
			// check file
			File file = new File(excel.trim()); // jhkim
			Row titles = null;

			if ("xls".equals(ext)) {
				// Workbook
				HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
				titles = (wb.getSheetAt(0)).getRow(0);
			} else if ("xlsx".equals(ext)) {
				// Workbook
				XSSFWorkbook wb = new XSSFWorkbook(excel.trim()); // jhkim trim
																	// 추가
				titles = (wb.getSheetAt(0)).getRow(0);
			}

			for (Cell cell : titles) {

				if (cell.getColumnIndex() > 0)
					sb.append(',');

				sb.append(cell.getRichStringCellValue().getString());
			}

		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return sb.toString();
	}

	public Map<String, Object> readExcelXLS(String excel, String fileType, int supplierId, String detailType) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

			// check file
			File file = new File(excel.trim()); // jhkim trim() 추가

			// Workbook
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

			// Text Extraction
			ExcelExtractor extractor = new ExcelExtractor(wb);
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			result = makeExcelToObject(wb.getSheetAt(0), fileType, supplierId, detailType, "xls");

		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}
	
	@Transactional(readOnly=false)
	public Map<String, Object> readExcelXLSX(String excel, String fileType, int supplierId, String detailType) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

			// check file
			File file = new File(excel.trim()); // jhkim trim 추가
			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(excel.trim()); // jhkim trim 추가
			}
			String fileName = file.getName();
			
			// Workbook
			XSSFWorkbook wb = new XSSFWorkbook(excel.trim()); // jhkim trim 추가

			// Text Extraction
			XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);

			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);
			if("ModemAndMeterMapper_template.xlsx".equals(fileName)) {
				result = insertBulkModemMeterforHaiti(excel, supplierId);
			}else {
				result = makeExcelToObject(wb.getSheetAt(0), fileType, supplierId, detailType, "xlsx");
			}
		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}
	
	private Map<String, Object> insertBulkModemMeterforHaiti(String excel, int supplierId) {
		Map<String, Object> result = new HashMap<String, Object>();
        List<List<Object>> errorList = new ArrayList<List<Object>>();

//        Supplier supplier = supplierDao.get(supplierId);

        logger.debug("excel file:" + excel);

        // Workbook
        XSSFWorkbook wb = null;
        OPCPackage pkg = null;

        try {
            pkg = OPCPackage.open(excel.trim());
            wb = new XSSFWorkbook(pkg);
        } catch (FileNotFoundException e1) {
            logger.error(e1, e1);
        } catch (IOException e1) {
            logger.error(e1, e1);
        } catch (InvalidFormatException e) {
            logger.error(e, e);
        }

        XSSFSheet sheet = wb.getSheetAt(0);

        // Getting cell contents
        List<Object> errs = null;
        String ModemSerial = null;
        String MeterSerial = null;

        for (Row row : sheet) {
            // header row skip
            if (row.getRowNum() == 0) {
                continue;
            }

            ModemSerial = getCellValue(row.getCell(0)).trim();
            MeterSerial = getCellValue(row.getCell(1)).trim();

            // 비어있는 cell 이 있으면 에러처리
            if (ModemSerial.isEmpty() || MeterSerial.isEmpty()) {
                errorList.add(getErrorRecord(ModemSerial, MeterSerial, "Please input all cells"));
                continue;
            }
            
            // ModemSerial 중복체크
            /*MeterMapper chkModem = meterMapperDao.findByCondition("modemDeviceSerial", ModemSerial);
            meterMapperDao.clear();

            if (chkModem != null && chkModem.getId() != null) {
                errorList.add(getErrorRecord(ModemSerial, MeterSerial, "There is a duplicate Modem : " + ModemSerial));
                continue;
            }
            
            // MeterSerial 중복체크
            MeterMapper chkMeter = meterMapperDao.findByCondition("meterPrintedMdsId", MeterSerial);
            meterMapperDao.clear();

            if (chkMeter != null && chkMeter.getId() != null) {
                errorList.add(getErrorRecord(ModemSerial, MeterSerial, "There is a duplicate Meter : " + MeterSerial));
                continue;
            }*/

            // Add
        	try {
        		MeterMapper newMeter = new MeterMapper();
        		newMeter.setModemDeviceSerial(ModemSerial);
        		newMeter.setMeterPrintedMdsId(MeterSerial);
        		meterMapperDao.merge(newMeter);
        		meterMapperDao.flushAndClear();
    			
    		} catch (Exception e) {
    			logger.error(e.getMessage(), e);
//    			errorList.add(getErrorRecord(ModemSerial, MeterSerial, "There is a duplicate Modem : " + ModemSerial + " Meter : " + MeterSerial));
    			continue;
    		}
            
        } // for end : Row

        if (errorList.size() <= 0) {
            result.put("resultMsg", "success");
        } else {
            result.put("resultMsg", "failure");
        }

        result.put("errorList", errorList);
        result.put("errorListSize", errorList.size());

        // close OPCPackage
        try {
            if (pkg != null) {
                pkg.close();
            }
        } catch (IOException e) {
            logger.error(e, e);
        }

        // delete uploaded file
        File file = new File(excel.trim());
        file.delete();

        return result;
	}
	
	/**
     * method name : getErrorRecord<b/>
     * method Desc :
     *
     * @param customerNo
     * @param customerName
     * @param contractNumber
     * @param mobileNo
     * @param errMsg
     * @return
     */
    private List<Object> getErrorRecord(String ModemSerial, String MeterSerial, String errMsg) {
        List<Object> errs = new ArrayList<Object>();
        errs.add(ModemSerial);
        errs.add(MeterSerial);
        errs.add(errMsg);
        return errs;
    }

	public Map<String, Object> readShipmentExcelXLS(String excel, String fileType, int supplierId, String detailType) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			String[] tempFileName = excel.split("/");
			String fileName = tempFileName[excel.split("/").length-1];

			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));
			File file = new File(excel.trim());
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

			ExcelExtractor extractor = new ExcelExtractor(wb);
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			result = makeShipmentExcelToObject(wb.getSheetAt(0), fileType, supplierId, detailType, "xls", fileName);
		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}
	
	/**
     * method name : getCellValue<b/>
     * method Desc :
     *
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
    	if (cell == null) {
    		return "";
    	}

        String value = null;

        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = Boolean.toString(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue().toString();
                } else {
                    Long roundVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (doubleVal.equals(roundVal.doubleValue())) {
                        value = String.valueOf(roundVal);
                    } else {
                        value = String.valueOf(doubleVal);
                    }
                }
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
        }
        return value;
    }

	public Map<String, Object> readShipmentExcelXLSX(String excel, String fileType, int supplierId, String detailType) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			String[] tempFileName = excel.split("/");
			String fileName = tempFileName[excel.split("/").length-1];

			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));
			File file = new File(excel.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(excel.trim());
			}

			XSSFWorkbook wb = new XSSFWorkbook(excel.trim());
			XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			result = makeShipmentExcelToObject(wb.getSheetAt(0), fileType, supplierId, detailType, "xlsx", fileName);
		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}

	private Map<String, Object> makeShipmentExcelToObject(
			Object sheet,
			String fileType,
			int supplierId,
			String detailType,
			String flag,
			String fileName) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			List<String> mcuList = null;
			List<String> updateMcuList = null;

			List<String> meterList = null;
			List<String> updateMeterList = null;

			List<String> modemList = null;
			List<String> updateModemList = null;
			
			List<String> msisdnList = null;
			List<String> updateMsisdnList = null;
			
			List<String> simCardList = null;
			List<String> updateSimCardList = null;
			
			TargetClass deviceType = null;

			if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
				mcuList = new ArrayList<String>();
				updateMcuList = new ArrayList<String>();

				deviceType = TargetClass.DCU;
			} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
				modemList = new ArrayList<String>();
				updateModemList = new ArrayList<String>();
				
				if (detailType.equals(CommonConstants.ShipmentTargetType.EthernetModem.getName())) {
					deviceType = TargetClass.EthernetModem;
				} else if (detailType.equals(CommonConstants.ShipmentTargetType.EthernetConverter.getName())) {
					deviceType = TargetClass.EthernetConverter;
				} else if (detailType.equals(CommonConstants.ShipmentTargetType.MBBModem.getName())) {
					deviceType = TargetClass.MBBModem;
				} else if (detailType.equals(CommonConstants.ShipmentTargetType.RFModem.getName())) {
					deviceType = TargetClass.RFModem;
				}
			} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
				meterList = new ArrayList<String>();
				updateMeterList = new ArrayList<String>();

				if (detailType.equals(TargetClass.EnergyMeter.name())) {
					deviceType = TargetClass.EnergyMeter;
				} else if (detailType.equals(TargetClass.WaterMeter.name())) {
					deviceType = TargetClass.WaterMeter;
				} else if (detailType.equals(TargetClass.GasMeter.name())) {
					deviceType = TargetClass.GasMeter;
				} else if (detailType.equals(TargetClass.HeatMeter.name())) {
					deviceType = TargetClass.HeatMeter;
				}
			} else if (fileType.equals("MSISDN")) {
				msisdnList = new ArrayList<String>();
				updateMsisdnList = new ArrayList<String>();
			} else if (fileType.equals("SimCard")) {
				simCardList = new ArrayList<String>();
				updateSimCardList = new ArrayList<String>();
			}

			Row titles = null;

			// Getting cell contents
			int totCnt = 0;
			int overLapCnt = 0;
			int excelLine = 1;
			List<Integer> excelLineList = new ArrayList<Integer>();
			
			for (Row row : (flag.equals("xls") ? (HSSFSheet) sheet : (XSSFSheet) sheet)) {
				if (row.getRowNum() == 0) {
					titles = row;
					continue;
				}

				totCnt++;
				excelLine++;

				if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
					Map<String, Object> map = (Map<String, Object>) shipmentUpdate(titles, row, supplierId, fileType, detailType);

					if (map != null) {
						String euiId = (String) map.get("euiId");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateMcuList.add(euiId);
						} else {
							mcuList.add(euiId);
							excelLineList.add(excelLine);
							overLapCnt++;
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
					Map<String, Object> map = (Map<String, Object>) shipmentUpdate(titles, row, supplierId, fileType, detailType);

					if (map != null) {
						String euiId = (String) map.get("euiId");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateModemList.add(euiId);
						} else {
							modemList.add(euiId);
							excelLineList.add(excelLine);
							overLapCnt++;
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
					Map<String, Object> map = (Map<String, Object>) shipmentUpdate(titles, row, supplierId, fileType, detailType);

					if (map != null) {
						String euiId = (String) map.get("euiId");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateMeterList.add(euiId);
						} else {
							meterList.add(euiId);
							excelLineList.add(excelLine);
							overLapCnt++;
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (fileType.equals("MSISDN")) {
					Map<String, Object> map = (Map<String, Object>) msisdnUpdate(titles, row, supplierId, fileType, detailType);
					
					if (map != null) {
						String imsi = (String) map.get("imsi");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateMsisdnList.add(imsi);
						} else {
							msisdnList.add(imsi);
							excelLineList.add(excelLine);
							overLapCnt++;
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (fileType.equals("SimCard")) {
					Map<String, Object> map = (Map<String, Object>) simCardUpdate(titles, row, supplierId, fileType, detailType);

					if (map != null) {
						String simCard = (String) map.get("simCard");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateSimCardList.add(simCard);
						} else {
							simCardList.add(simCard);
							excelLineList.add(excelLine);
							overLapCnt++;
						}
					} else {
						overLapCnt++;
						continue;
					}
				}
			}
			
			if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
				result.put("device", mcuList);
			} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
				result.put("device", modemList);
			} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
				result.put("device", meterList);
			} else if (fileType.equals("MSISDN")) {
				result.put("device", msisdnList);
			} else if (fileType.equals("SimCard")) {
				result.put("device", simCardList);
			}
			
			// Import History Update Logic (S) - File Import 내역을 DEVICEREG_LOG 테이블에 저장한다.
			if (fileType != "MSISDN" && fileType != "SimCard") {
				Map<String, Object> insertDataMap = new HashMap<String, Object>();
				Supplier supplier = supplierDao.getSupplierById(supplierId);

				insertDataMap.put("supplier", supplier);
				insertDataMap.put("regType", RegType.Shipment);
				insertDataMap.put("deviceType", deviceType);	
				insertDataMap.put("fileName", fileName);
				insertDataMap.put("totalCount", Integer.toString(totCnt));
				insertDataMap.put("successCount", Integer.toString(totCnt - overLapCnt));
				insertDataMap.put("failCount", Integer.toString(overLapCnt));

				insertShipmentImportLog(insertDataMap);
			}
			// Import History Update Logic (E)

			result.put("excelLineList", excelLineList);
			result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt) + ", Fail :" + overLapCnt);
		} catch (Exception e) {
			logger.error(e, e);
		}
		
		return result;
	}

	/**
	 * method name : makeExcelToObject<b/> method Desc :
	 *
	 * @param sheet
	 * @param fileType
	 * @param supplierId
	 * @param detailType
	 * @param flag
	 * @return
	 * @throws IOException
	 */
	private Map<String, Object> makeExcelToObject(Object sheet, String fileType, int supplierId, String detailType,
			String flag) throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();

		List<Object> resultList = new ArrayList<Object>();
		List<MCU> mcuList = null;
		List<MCU> updateMcuList = null;

		List<Meter> meterList = null;
		List<EnergyMeter> energyMeterList = null;
		List<WaterMeter> waterMeterList = null;
		List<SolarPowerMeter> solarPowerMeterList = null;
		List<HeatMeter> heatMeterList = null;
		List<GasMeter> gasMeterList = null;
		List<VolumeCorrector> vcMeterList = null;

		List<Meter> updateMeterList = null;
		List<EnergyMeter> updateEnergyMeterList = null;
		List<WaterMeter> updateWaterMeterList = null;
		List<SolarPowerMeter> updateSolarPowerMeterList = null;
		List<HeatMeter> updateHeatMeterList = null;
		List<GasMeter> updateGasMeterList = null;
		List<VolumeCorrector> updateVcMeterList = null;

		List<Modem> modemList = null;
		List<ZRU> zruModemList = null;
		List<ZMU> zmuModemList = null;
		List<ZEUPLS> zeuplsModemList = null;
		List<ZEUMBus> zeumbusModemList = null;
		List<ZBRepeater> zbrepeaterModemList = null;
		List<PLCIU> plciuModemList = null;
		List<MMIU> mmiuModemList = null;
		List<IHD> ihdModemList = null;
		List<IEIU> ieiuModemList = null;
		List<HMU> hmuModemList = null;
		List<ACD> acdModemList = null;
		List<Converter> converterModemList = null;
		List<SubGiga> subGigaModemList = null;

		List<Modem> updateModemList = null;
		List<ZRU> updateZruModemList = null;
		List<ZMU> updateZmuModemList = null;
		List<ZEUPLS> updateZeuplsModemList = null;
		List<ZEUMBus> updateZeumbusModemList = null;
		List<ZBRepeater> updateZbrepeaterModemList = null;
		List<PLCIU> updatePlciuModemList = null;
		List<MMIU> updateMmiuModemList = null;
		List<IHD> updateIhdModemList = null;
		List<IEIU> updateIeiuModemList = null;
		List<HMU> updateHmuModemList = null;
		List<ACD> updateAcdModemList = null;
		List<Converter> updateConverterModemList = null;
		List<SubGiga> updateSubGigaModemList = null;

		if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
			mcuList = new ArrayList<MCU>();
			updateMcuList = new ArrayList<MCU>();
		} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
			if (detailType.equals("EnergyMeter")) {
				energyMeterList = new ArrayList<EnergyMeter>();
				updateEnergyMeterList = new ArrayList<EnergyMeter>();
			} else if (detailType.equals("WaterMeter")) {
				waterMeterList = new ArrayList<WaterMeter>();
				updateWaterMeterList = new ArrayList<WaterMeter>();
			} else if (detailType.equals("HeatMeter")) {
				heatMeterList = new ArrayList<HeatMeter>();
				updateHeatMeterList = new ArrayList<HeatMeter>();
			} else if (detailType.equals("GasMeter")) {
				gasMeterList = new ArrayList<GasMeter>();
				updateGasMeterList = new ArrayList<GasMeter>();
			} else if (detailType.equals("VolumeCorrector")) {
				vcMeterList = new ArrayList<VolumeCorrector>();
				updateVcMeterList = new ArrayList<VolumeCorrector>();
			} else if (detailType.equals("SolarPowerMeter")) {
				solarPowerMeterList = new ArrayList<SolarPowerMeter>();
				updateSolarPowerMeterList = new ArrayList<SolarPowerMeter>();
			} else {
				meterList = new ArrayList<Meter>();
				updateMeterList = new ArrayList<Meter>();
			}
		} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
			if (detailType.equals("ZRU")) {
				zruModemList = new ArrayList<ZRU>();
				updateZruModemList = new ArrayList<ZRU>();
			} else if (detailType.equals("ZMU")) {
				zmuModemList = new ArrayList<ZMU>();
				updateZmuModemList = new ArrayList<ZMU>();
			} else if (detailType.equals("ZEUPLS")) {
				zeuplsModemList = new ArrayList<ZEUPLS>();
				updateZeuplsModemList = new ArrayList<ZEUPLS>();
			} else if (detailType.equals("ZEUMBus")) {
				zeumbusModemList = new ArrayList<ZEUMBus>();
				updateZeumbusModemList = new ArrayList<ZEUMBus>();
			} else if (detailType.equals("ZBRepeater")) {
				zbrepeaterModemList = new ArrayList<ZBRepeater>();
				updateZbrepeaterModemList = new ArrayList<ZBRepeater>();
			} else if (detailType.equals("PLCIU")) {
				plciuModemList = new ArrayList<PLCIU>();
				updatePlciuModemList = new ArrayList<PLCIU>();
			} else if (detailType.equals("MMIU")) {
				mmiuModemList = new ArrayList<MMIU>();
				updateMmiuModemList = new ArrayList<MMIU>();
			} else if (detailType.equals("IHD")) {
				ihdModemList = new ArrayList<IHD>();
				updateIhdModemList = new ArrayList<IHD>();
			} else if (detailType.equals("IEIU")) {
				ieiuModemList = new ArrayList<IEIU>();
				updateIeiuModemList = new ArrayList<IEIU>();
			} else if (detailType.equals("HMU")) {
				hmuModemList = new ArrayList<HMU>();
				updateHmuModemList = new ArrayList<HMU>();
			} else if (detailType.equals("ACD")) {
				acdModemList = new ArrayList<ACD>();
				updateAcdModemList = new ArrayList<ACD>();
			} else if (detailType.equals("Converter")) {
				converterModemList = new ArrayList<Converter>();
				updateConverterModemList = new ArrayList<Converter>();
			} else if (detailType.equals("SubGiga")) {
				subGigaModemList = new ArrayList<SubGiga>();
				updateSubGigaModemList = new ArrayList<SubGiga>();
			} else {
				modemList = new ArrayList<Modem>();
				updateModemList = new ArrayList<Modem>();
			}
		}

		Row titles = null;

		// Getting cell contents
		int totCnt = 0;
		int overLapCnt = 0;
		for (Row row : (flag.equals("xls") ? (HSSFSheet) sheet : (XSSFSheet) sheet)) {

			if (row.getRowNum() == 0) {
				titles = row;
				continue;
			}

			totCnt++;// 전체 데이터수 구함.
			resultList.add(getFileMap(titles, row));

			if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
				Map<String, Object> map = (Map<String, Object>) getAllMCU(titles, row, supplierId, detailType);

				if (map != null) {
					MCU tmpMcu = (MCU) map.get("MCU");
					Boolean isUpdate = (Boolean) map.get("isUpdate");

					if (isUpdate) {
						updateMcuList.add(tmpMcu);
					} else {
						mcuList.add(tmpMcu);
					}
				} else {
					overLapCnt++;
					continue;
				}
			} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {

				if (detailType.equals("EnergyMeter")) {
					Map<String, Object> map = getAllMeter(titles, row, supplierId, detailType);

					if (map != null) {
						EnergyMeter tmpMeter = (EnergyMeter) map.get("Meter");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							if (tmpMeter.getInstallDate().equalsIgnoreCase("false")) { // 기존에
																						// 등록된
																						// Meter가
																						// 있고
																						// false로
																						// 등록시
																						// 기존
																						// 설치일유지
								tmpMeter.setInstallDate(null);
							}

							updateEnergyMeterList.add(tmpMeter);
						} else {
							if (tmpMeter.getInstallDate().equalsIgnoreCase("false")) { // 기존에
																						// 등록된
																						// Meter가
																						// 없고
																						// false로
																						// 등록시
																						// 오늘날짜로등록
								try {
									tmpMeter.setInstallDate(TimeUtil.getCurrentTime());
								} catch (ParseException e) {
									logger.debug(e);
								}
							}

							energyMeterList.add(tmpMeter);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("WaterMeter")) {
					Map<String, Object> map = getAllMeter(titles, row, supplierId, detailType);

					if (map != null) {
						WaterMeter tmpMeter = (WaterMeter) map.get("Meter");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateWaterMeterList.add(tmpMeter);
						} else {
							waterMeterList.add(tmpMeter);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("SolarPowerMeter")) {
					SolarPowerMeter tmpMeter = (SolarPowerMeter) getAllMeter(titles, row, supplierId, detailType);
					if (tmpMeter != null) {
						solarPowerMeterList.add(tmpMeter);
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("HeatMeter")) {
					Map<String, Object> map = getAllMeter(titles, row, supplierId, detailType);

					if (map != null) {
						HeatMeter tmpMeter = (HeatMeter) map.get("Meter");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateHeatMeterList.add(tmpMeter);
						} else {
							heatMeterList.add(tmpMeter);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("GasMeter")) {
					Map<String, Object> map = getAllMeter(titles, row, supplierId, detailType);

					if (map != null) {
						GasMeter tmpMeter = (GasMeter) map.get("Meter");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateGasMeterList.add(tmpMeter);
						} else {
							gasMeterList.add(tmpMeter);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("VolumeCorrector")) {
					Map<String, Object> map = getAllMeter(titles, row, supplierId, detailType);

					if (map != null) {
						VolumeCorrector tmpMeter = (VolumeCorrector) map.get("Meter");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateVcMeterList.add(tmpMeter);
						} else {
							vcMeterList.add(tmpMeter);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else {
					Meter tmpMeter = getMeter(titles, row, supplierId, detailType);
					if (tmpMeter != null) {
						meterList.add(tmpMeter);
					} else {
						overLapCnt++;
						continue;
					}
				}
			} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
				if (detailType.equals("ZRU")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						ZRU tmpModem = (ZRU) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateZruModemList.add(tmpModem);
						} else {
							zruModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("ZMU")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						ZMU tmpModem = (ZMU) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateZmuModemList.add(tmpModem);
						} else {
							zmuModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("ZEUPLS")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						ZEUPLS tmpModem = (ZEUPLS) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateZeuplsModemList.add(tmpModem);
						} else {
							zeuplsModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("ZEUMBus")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						ZEUMBus tmpModem = (ZEUMBus) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateZeumbusModemList.add(tmpModem);
						} else {
							zeumbusModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("ZBRepeater")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						ZBRepeater tmpModem = (ZBRepeater) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateZbrepeaterModemList.add(tmpModem);
						} else {
							zbrepeaterModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("PLCIU")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						PLCIU tmpModem = (PLCIU) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updatePlciuModemList.add(tmpModem);
						} else {
							plciuModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("MMIU")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						MMIU tmpModem = (MMIU) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateMmiuModemList.add(tmpModem);
						} else {
							mmiuModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("IHD")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						IHD tmpModem = (IHD) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateIhdModemList.add(tmpModem);
						} else {
							ihdModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("IEIU")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						IEIU tmpModem = (IEIU) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateIeiuModemList.add(tmpModem);
						} else {
							ieiuModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("HMU")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						HMU tmpModem = (HMU) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateHmuModemList.add(tmpModem);
						} else {
							hmuModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("ACD")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						ACD tmpModem = (ACD) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateAcdModemList.add(tmpModem);
						} else {
							acdModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("Converter")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						Converter tmpModem = (Converter) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateConverterModemList.add(tmpModem);
						} else {
							converterModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else if (detailType.equals("SubGiga")) {
					Map<String, Object> map = getAllModem(titles, row, supplierId, detailType);

					if (map != null) {
						SubGiga tmpModem = (SubGiga) map.get("Modem");
						Boolean isUpdate = (Boolean) map.get("isUpdate");

						if (isUpdate) {
							updateSubGigaModemList.add(tmpModem);
						} else {
							subGigaModemList.add(tmpModem);
						}
					} else {
						overLapCnt++;
						continue;
					}
				} else {
					Modem tmpModem = getModem(titles, row, supplierId, detailType);
					if (tmpModem != null) {
						modemList.add(tmpModem);
					} else {
						overLapCnt++;
						continue;
					}
				}
			}
		} // for end : Row

		result.put("file", resultList);
		List<Object> headerList = new ArrayList<Object>();
		int cnt = titles.getPhysicalNumberOfCells();
		headerList.add("Status");
		for (int i = 0; i < cnt; i++) {
			headerList.add(titles.getCell(i).toString());
		}
		result.put("header", headerList);
		result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt));
		if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
			result.put("device", mcuList);
			result.put("updateDevice", updateMcuList);
		} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
			if (detailType.equals("EnergyMeter")) {
				result.put("device", energyMeterList);
				result.put("updateDevice", updateEnergyMeterList);
			} else if (detailType.equals("WaterMeter")) {
				result.put("device", waterMeterList);
				result.put("updateDevice", updateWaterMeterList);
			} else if (detailType.equals("HeatMeter")) {
				result.put("device", heatMeterList);
				result.put("updateDevice", updateHeatMeterList);
			} else if (detailType.equals("GasMeter")) {
				result.put("device", gasMeterList);
				result.put("updateDevice", updateGasMeterList);
			} else if (detailType.equals("SolarPowerMeter")) {
				result.put("device", solarPowerMeterList);
				result.put("updateDevice", updateSolarPowerMeterList);
			} else if (detailType.equals("VolumeCorrector")) {
				result.put("device", vcMeterList);
				result.put("updateDevice", updateVcMeterList);
			} else {
				result.put("device", meterList);
				result.put("updateDevice", updateMeterList);
			}
		} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
			if (detailType.equals("ZRU")) {
				result.put("device", zruModemList);
				result.put("updateDevice", updateZruModemList);
			} else if (detailType.equals("ZMU")) {
				result.put("device", zmuModemList);
				result.put("updateDevice", updateZmuModemList);
			} else if (detailType.equals("ZEUPLS")) {
				result.put("device", zeuplsModemList);
				result.put("updateDevice", updateZeuplsModemList);
			} else if (detailType.equals("ZEUMBus")) {
				result.put("device", zeumbusModemList);
				result.put("updateDevice", updateZeumbusModemList);
			} else if (detailType.equals("ZBRepeater")) {
				result.put("device", zbrepeaterModemList);
				result.put("updateDevice", updateZbrepeaterModemList);
			} else if (detailType.equals("PLCIU")) {
				result.put("device", plciuModemList);
				result.put("updateDevice", updatePlciuModemList);
			} else if (detailType.equals("MMIU")) {
				result.put("device", mmiuModemList);
				result.put("updateDevice", updateMmiuModemList);
			} else if (detailType.equals("IHD")) {
				result.put("device", ihdModemList);
				result.put("updateDevice", updateIhdModemList);
			} else if (detailType.equals("IEIU")) {
				result.put("device", ieiuModemList);
				result.put("updateDevice", updateIeiuModemList);
			} else if (detailType.equals("HMU")) {
				result.put("device", hmuModemList);
				result.put("updateDevice", updateHmuModemList);
			} else if (detailType.equals("ACD")) {
				result.put("device", acdModemList);
				result.put("updateDevice", updateAcdModemList);
			} else if (detailType.equals("Converter")) {
				result.put("device", converterModemList);
				result.put("updateDevice", updateConverterModemList);
			} else if (detailType.equals("SubGiga")) {
				result.put("device", subGigaModemList);
				result.put("updateDevice", updateSubGigaModemList);
			} else {
				result.put("device", modemList);
				result.put("updateDevice", updateModemList);
			}
		}
		return result;
	}

	public Map<String, Object> readOnlyExcelXLS(String excel, String fileType, int supplierId, String detailType) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

			// check file
			File file = new File(excel.trim()); // jhkim trim() 추가

			// Workbook
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

			// Text Extraction
			ExcelExtractor extractor = new ExcelExtractor(wb);
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			List<Object> resultList = new ArrayList<Object>();
			Row titles = null;

			// Getting cell contents
			for (Row row : wb.getSheetAt(0)) {

				if (row.getRowNum() == 0) {
					titles = row;
					continue;
				}

				resultList.add(getFileMapRead(titles, row, detailType));
			} // for end : Row

			result.put("file", resultList);
			List<Object> headerList = new ArrayList<Object>();
			int cnt = titles.getPhysicalNumberOfCells();
			headerList.add("Status");
			for (int i = 0; i < cnt; i++) {
				headerList.add(titles.getCell(i).toString());
			}
			result.put("header", headerList);
			result.put("index", cnt);

		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}

	public Map<String, Object> readOnlyExcelXLSX(String excel, String fileType, int supplierId, String detailType) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

			// check file
			File file = new File(excel.trim()); // jhkim trim 추가
			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(excel.trim()); // jhkim trim 추가
			}

			// Workbook
			XSSFWorkbook wb = new XSSFWorkbook(excel.trim()); // jhkim trim 추가

			// Text Extraction
			XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);

			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			List<Object> resultList = new ArrayList<Object>();

			Row titles = null;

			// Getting cell contents
			for (Row row : wb.getSheetAt(0)) {

				if (row.getRowNum() == 0) {
					titles = row;
					continue;
				}

				resultList.add(getFileMapRead(titles, row, detailType));
			} // for end : Row

			result.put("file", resultList);
			List<Object> headerList = new ArrayList<Object>();
			int cnt = titles.getPhysicalNumberOfCells();
			headerList.add("Status");
			for (int i = 0; i < cnt; i++) {
				headerList.add(titles.getCell(i).toString());
			}
			result.put("header", headerList);
			result.put("index", cnt);
		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}
	
	public Map<String, Object> readDeviceIdExcelXLSX(String excel, String targetDeviceType, String modelId, String sType, String supplierId) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

			// check file
			File file = new File(excel.trim()); // jhkim trim 추가
			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(excel.trim()); // jhkim trim 추가
			}

			// Workbook
			XSSFWorkbook wb = new XSSFWorkbook(excel.trim()); // jhkim trim 추가

			// Text Extraction
			XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);

			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);
			
			String resultVersionList="";
			String resultList = "";
			String invalidSerial = "";
			int totalCount = 0 ;
			int invalidCount = 0 ;
			Row titles = null;
			
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<String> validList = new ArrayList<String>();
			List<String> versionList = new ArrayList<String>();
			List<Object> invalidList = new ArrayList<Object>();
            String deviceList="";
			String version="";
            
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("targetDeviceType", targetDeviceType);
			condition.put("modelId", modelId);
			condition.put("sType", sType);
			condition.put("supplierId", supplierId);
			
			// Getting cell contents
			for (Row row : wb.getSheetAt(0)) {

				if (row.getRowNum() == 0) {
					titles = row;
					continue;
				}
				
				String deviceSerial = getFileMapRead2(titles, row);
				if(!deviceSerial.equals("") && deviceSerial != null){
					deviceList += deviceSerial+",";											//전체 입력된 list를 String으로 저장
					invalidList.add(deviceSerial);
				}
				// 여기서 유효성 체크하기 ibk
/*				if(!deviceSerial.equals("") && deviceSerial != null){
					if(targetDeviceType.contains("dcu")){
						if(mcuDao.get(deviceSerial) != null){
							resultList += deviceSerial + ",";
							totalCount++;
						}else{
							invalidSerial += deviceSerial + "\n";
							totalCount++;
							invalidCount++;
						}
					}else if(targetDeviceType.equals("modem")){
						deviceList += deviceSerial+",";											//전체 입력된 list를 String으로 저장
						invalidList.add(deviceSerial);
						if(modemDao.get(deviceSerial) != null){
							resultList += deviceSerial + ",";
							totalCount++;
						}else{
							invalidSerial += deviceSerial + "\n";
							totalCount++;
							invalidCount++;
						}
					}else if(targetDeviceType.equals("meter")){
						if(meterDao.get(deviceSerial) != null){
							resultList += deviceSerial + ",";
							totalCount++;
						}else{
							invalidSerial += deviceSerial + "\n";
							totalCount++;
							invalidCount++;
						}
					}
				}*/
			} // for end : Row
			
			
			if(deviceList.equals("")) {
				deviceList="-";
			}
			condition.put("deviceList", deviceList);
			//targetDeviceType으
			if(targetDeviceType.equals("dcu-coordinate")){
				list = mcuDao.getValidCodiList(condition);
			
			}else if(targetDeviceType.contains("dcu")){
				list = mcuDao.getValidMCUList(condition);
				
			}else if(targetDeviceType.equals("modem")){
				list = modemDao.getValidModemList(condition);
				
			}else if(targetDeviceType.equals("meter")){
				list = meterDao.getValidMeterList(condition);
			}
			
			if(targetDeviceType.equals("dcu-coordinate")) {
				//list의 device_id, FW ver , dcu-codi의 fwVer하기 위해 => null도 필요
				for(Map<String, Object> map : list) {
					validList.add(map.get("DEVICE_ID").toString());
					version = (map.get("VERSION") != null) ? map.get("VERSION").toString() : "null";
					if(!versionList.contains(version))
						versionList.add(version);
				}
			}else {
				//list의 device_id, FW ver 
				for(Map<String, Object> map : list) {
					validList.add(map.get("DEVICE_ID").toString());
					version = (map.get("VERSION") != null) ? map.get("VERSION").toString() : "null";
					if(!versionList.contains(version) && !version.equals("null"))
						versionList.add(version);
				}
			}
			
			
			totalCount = invalidList.size();	//전체 device_id 리스트 개수
			invalidList.removeAll(validList);	//전체 device_id- 유효한 device_id= 없는 device_id
			invalidCount = invalidList.size();	//없는 device_id의 크기
			resultList = validList.toString().substring(1, validList.toString().length()-1);
			
			for (Object s : invalidList)
			{
				invalidSerial += s + "\n";
			}
			
			result.put("versionList", versionList);
			result.put("file", resultList);
			result.put("invalidSerial", invalidSerial);
			result.put("count", invalidCount+"/"+totalCount);
			
			List<Object> headerList = new ArrayList<Object>();
			int cnt = titles.getPhysicalNumberOfCells();
			/*headerList.add("Status");
			for (int i = 0; i < cnt; i++) {
				headerList.add(titles.getCell(i).toString());
			}
			result.put("header", headerList);
			result.put("index", cnt);*/
		} catch (IOException ie) {
			logger.error(ie,ie);
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}

	private Map<String, Object> getFormat(DeviceType deviceType, String type) {

		Map<Integer, Object> requiredList = new HashMap<Integer, Object>();
		Map<Integer, Object> unrequiredList = new HashMap<Integer, Object>();

		String filePath = null;
		if (deviceType.equals(CommonConstants.DeviceType.MCU)) {
			filePath = ctxRoot + "/DCU_format.xls";
		} else if (deviceType.equals(CommonConstants.DeviceType.Meter)) {
			if (type.equals("EnergyMeter")) {
				filePath = ctxRoot + "/EnergyMeter_format.xls";
			} else if (type.equals("GasMeter")) {
				filePath = ctxRoot + "/GasMeter_format.xls";
			} else if (type.equals("HeatMeter")) {
				filePath = ctxRoot + "/HeatMeter_format.xls";
			} else if (type.equals("VolumeCorrector")) {
				filePath = ctxRoot + "/VolumeCorrector_format.xls";
			} else if (type.equals("WaterMeter")) {
				filePath = ctxRoot + "/WaterMeter_format.xls";
			} else if (type.equals("SolarPowerMeter")) {
				filePath = ctxRoot + "/SolarPowerMeter_format.xls";
			}
		} else if (deviceType.equals(CommonConstants.DeviceType.Modem)) {
			if (type.equals("ZRU")) {
				filePath = ctxRoot + "/ZRU_format.xls";
			} else if (type.equals("ZMU")) {
				filePath = ctxRoot + "/ZMU_format.xls";
			} else if (type.equals("ZEUPLS")) {
				filePath = ctxRoot + "/ZEUPLS_format.xls";
			} else if (type.equals("ZEUMBus")) {
				filePath = ctxRoot + "/ZEUMBus_format.xls";
			} else if (type.equals("ZBRepeater")) {
				filePath = ctxRoot + "/ZBRepeater_format.xls";
			} else if (type.equals("PLCIU")) {
				filePath = ctxRoot + "/PLCIU_format.xls";
			} else if (type.equals("MMIU")) {
				filePath = ctxRoot + "/MMIU_format.xls";
			} else if (type.equals("IHD")) {
				filePath = ctxRoot + "/IHD_format.xls";
			} else if (type.equals("IEIU")) {
				filePath = ctxRoot + "/IEIU_format.xls";
			} else if (type.equals("HMU")) {
				filePath = ctxRoot + "/HMU_format.xls";
			} else if (type.equals("ACD")) {
				filePath = ctxRoot + "/ACD_format.xls";
			} else if (type.equals("Converter")) {
				filePath = ctxRoot + "/Converter_format.xls";
			} else if (type.equals("SubGiga")) {
				filePath = ctxRoot + "/SubGiga_format.xls";
			}
		}

		try {
			File formatFile = new File(filePath.trim());
			if (!formatFile.exists()) {
				throw new IOException(filePath.trim()); // jhkim trim 추가
			}

			// Workbook
			// XSSFWorkbook wb = new XSSFWorkbook(filePath.trim()); //jhkim trim
			// 추가
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(formatFile));

			String itemName = null;
			String itemType = null;
			String requiredFlg = null;

			int rowCnt = 0;

			// Getting cell contents
			for (Row row : wb.getSheetAt(0)) {

				if (row.getRowNum() == 0)
					continue;

				itemName = row.getCell(0).getRichStringCellValue().getString();
				if (type != null) {
					itemType = row.getCell(1).getRichStringCellValue().getString();
					requiredFlg = row.getCell(2).getRichStringCellValue().getString();

					if (itemType.equals(type) || itemType.equals(deviceType.name())) {
						if (requiredFlg.equals("Y")) {
							requiredList.put(rowCnt++, itemName);
						} else {
							unrequiredList.put(rowCnt++, itemName);
						}
					}
				} else {
					requiredFlg = row.getCell(1).getRichStringCellValue().getString();

					if (requiredFlg.equals("Y")) {
						requiredList.put(rowCnt++, itemName);
					} else {
						unrequiredList.put(rowCnt++, itemName);
					}
				}
			}
		} catch (IOException e) {
			logger.error(e,e);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("required", requiredList);
		result.put("unrequired", unrequiredList);

		return result;
	}

	private Map<String, Object> getFileMap(Row titles, Row row) throws IOException {

		Map<String, Object> returnData = new HashMap<String, Object>();
		// 상태 데이터 초기화
		String colName = null;
		String colValue = null;

		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}

			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}

			returnData.put(colName, colValue.trim());

		} // for end : Cell

		return returnData;
	}

	private Map<String, Object> getFileMapRead(Row titles, Row row, String detailType) throws IOException {

		Map<String, Object> returnData = new HashMap<String, Object>();
		// 상태 데이터 초기화
		int tmpCount = 0;
		String colName = null;
		String colValue = null;
		String tmpStatus = "Success"; // Status (Success or Failure)
		String status = "Success"; // Status (Success or Failure)
		Supplier tmpSupplier = new Supplier();
		ModemType tmpModemType = null;

		returnData.put("Status", "");
		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}

			colValue = colValue.trim();

			// ///////////////////////////////////// 데이터 검증
			// ############################################### 미터
			// ///////////////////////////////////////////////// 미터타입별 객체 생성
			if (detailType.equals("EnergyMeter")) {
				try {
					if (colName.equals("ct")) {
						tmpStatus = doubleCheck(colValue.toString()); // Double형
																		// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("pt")) {
						tmpStatus = doubleCheck(colValue.toString()); // Double형
																		// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("dstApplyOn")) {
						tmpStatus = boolCheck(colValue.toString()); // boolean
																	// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("dstSeasonOn")) {
						tmpStatus = boolCheck(colValue.toString()); // boolean
																	// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterElement")) { // code 검사
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("switchActivateStatus")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("switchStatus")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("transformerRatio")) {
						tmpStatus = doubleCheck(colValue.toString()); // Double형
																		// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("vt")) {
						tmpStatus = doubleCheck(colValue.toString()); // Double형
																		// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
					// ////////////////////////////////////////////////공통
					else if (colName.equals("installDate")) {
						tmpCount++;
						if (StringUtil.nullToBlank(colValue).isEmpty()) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("Location")) {
						tmpCount++;
						Location tmpLocation = locationDao.getLocationByName(colValue).get(0);
						if (colValue == null || colValue.length() == 0 || tmpLocation == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("mdsId")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							Meter tmpMdsId = meterDao.get(colValue.toString());
							if (status == "Success" && tmpMdsId != null) {
								status = "Update";
							}
							tmpCount++;
						}
					} else if (colName.equals("meterType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						tmpCount++;
						tmpSupplier = supplierDao.getSupplierByName(colValue);
						if (colValue == null || colValue.length() == 0 || tmpSupplier.getId() == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("model")) {
						tmpCount++;
						DeviceModel tmpDeviceModel = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(), colValue)
								.get(0);
						if (colValue == null || colValue.length() == 0 || tmpDeviceModel == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("prepaymentMeter")) {
						tmpStatus = boolCheck(colValue.toString()); // boolean
																	// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("endDevice")) {
						if (intCheck(colValue.toString()).equals("Failure")
								|| endDeviceDao.get(Integer.parseInt(colValue.toString())) == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastMeteringValue")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpInterval")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterStatus")) {
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("pulseConstant")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("timeDiff")) {
						tmpStatus = longCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("usageThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
				} catch (Exception e) {
					status = "Failure";
				}
				// ////// EnergyMeter End ///////
				// ////// WaterMeter Start ///////
			} else if (detailType.equals("WaterMeter")) {
				try {
					if (colName.equals("correctPulse")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| doubleCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentPulse")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("initPulse")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterSize")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("Qmax")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("underGround")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////////////////////////////////////////공통
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("Location")) {
						tmpCount++;
						Location tmpLocation = locationDao.getLocationByName(colValue).get(0);
						if (colValue == null || colValue.length() == 0 || tmpLocation == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("mdsId")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Meter tmpMdsId = meterDao.get(colValue.toString());

							if (status == "Success" && tmpMdsId != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("meterType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						tmpCount++;
						tmpSupplier = supplierDao.getSupplierByName(colValue);
						if (colValue == null || colValue.length() == 0 || tmpSupplier == null
								|| tmpSupplier.getId() == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("model")) {
						tmpCount++;
						DeviceModel tmpDeviceModel = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(), colValue)
								.get(0);
						if (colValue == null || colValue.length() == 0 || tmpDeviceModel == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("prepaymentMeter")) {
						tmpStatus = boolCheck(colValue.toString()); // boolean
																	// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("endDevice")) {
						if (intCheck(colValue.toString()).equals("Failure")
								|| endDeviceDao.get(Integer.parseInt(colValue.toString())) == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastMeteringValue")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpInterval")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterStatus")) {
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("pulseConstant")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("timeDiff")) {
						tmpStatus = longCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("usageThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
				} catch (Exception e) {
					status = "Failure";
				}
				// ////// WaterMeter End ///////
				// ////// GasMeter Start ///////
			} else if (detailType.equals("GasMeter")) {
				try {
					if (colName.equals("correctPulse")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| doubleCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("currentPulse")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| doubleCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("initPulse")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("valveStatus")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////////////////////////////////////////공통
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("Location")) {
						tmpCount++;
						Location tmpLocation = locationDao.getLocationByName(colValue).get(0);
						if (colValue == null || colValue.length() == 0 || tmpLocation == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("mdsId")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Meter tmpMdsId = meterDao.get(colValue.toString());

							if (status == "Success" && tmpMdsId != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("meterType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						tmpCount++;
						tmpSupplier = supplierDao.getSupplierByName(colValue);
						if (colValue == null || colValue.length() == 0 || tmpSupplier.getId() == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("model")) {
						tmpCount++;
						DeviceModel tmpDeviceModel = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(), colValue)
								.get(0);
						if (colValue == null || colValue.length() == 0 || tmpDeviceModel == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("prepaymentMeter")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("endDevice")) {
						if (intCheck(colValue.toString()).equals("Failure")
								|| endDeviceDao.get(Integer.parseInt(colValue.toString())) == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastMeteringValue")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpInterval")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterStatus")) {
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("pulseConstant")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("timeDiff")) {
						tmpStatus = longCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("usageThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
				} catch (Exception e) {
					status = "Failure";
				}
				// ////// GasMeter End ///////
				// ////// HeatMeter Start ///////
			} else if (detailType.equals("HeatMeter")) {
				try {
					if (colName.equals("apparatusRoomNumber")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("flowPerUnitPulse")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("heatingArea")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("heatType")) {
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode(colValue.toString()));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("installedPressSensor")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("numOfRoom")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////////////////////////////////////////공통
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("Location")) {
						tmpCount++;
						Location tmpLocation = locationDao.getLocationByName(colValue).get(0);
						if (colValue == null || colValue.length() == 0 || tmpLocation == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("mdsId")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Meter tmpMdsId = meterDao.get(colValue.toString());

							if (status == "Success" && tmpMdsId != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("meterType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						tmpCount++;
						tmpSupplier = supplierDao.getSupplierByName(colValue);
						if (colValue == null || colValue.length() == 0 || tmpSupplier.getId() == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("model")) {
						tmpCount++;
						DeviceModel tmpDeviceModel = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(), colValue)
								.get(0);
						if (colValue == null || colValue.length() == 0 || tmpDeviceModel == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("prepaymentMeter")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("endDevice")) {
						if (intCheck(colValue.toString()).equals("Failure")
								|| endDeviceDao.get(Integer.parseInt(colValue.toString())) == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastMeteringValue")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpInterval")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterStatus")) {
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("pulseConstant")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("timeDiff")) {
						tmpStatus = longCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("usageThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
				} catch (Exception e) {
					status = "Failure";
				}
				// ////// HeatMeter End ///////
				// ////// VolumeCorrector Start ///////
			} else if (detailType.equals("VolumeCorrector")) {
				try {
					if (colName.equals("atmospherePressure")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("basePressure")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("baseTemperature")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("batteryVoltage")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("co2")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("compressFactor")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("correctedUsageIndex")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("correctUsageCount")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentPressure")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentTemperature")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("fixedFpv")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("fixedPressure")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("fixedTemperature")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gasHour")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gasRelativeDensity")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lowestLimitPressure")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lowestLimptTemperature")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterFactor")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("n2")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("pipeLine")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("powerSupply")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("pulseWeight")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("specificGravity")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("tag")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("uncorrectedusageCount")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("uncorrectedusageIndex")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("upperLimitPressure")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("upperLimitTemperature")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";

						// ////////////////////////////////////////////////공통
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("Location")) {
						tmpCount++;
						Location tmpLocation = locationDao.getLocationByName(colValue).get(0);
						if (colValue == null || colValue.length() == 0 || tmpLocation == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("mdsId")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Meter tmpMdsId = meterDao.get(colValue.toString());

							if (status == "Success" && tmpMdsId != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("meterType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						tmpCount++;
						tmpSupplier = supplierDao.getSupplierByName(colValue);
						if (colValue == null || colValue.length() == 0 || tmpSupplier.getId() == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("model")) {
						tmpCount++;
						DeviceModel tmpDeviceModel = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(), colValue)
								.get(0);
						if (colValue == null || colValue.length() == 0 || tmpDeviceModel == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("prepaymentMeter")) {
						tmpStatus = boolCheck(colValue.toString()); // boolean
																	// 검사
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("endDevice")) {
						if (intCheck(colValue.toString()).equals("Failure")
								|| endDeviceDao.get(Integer.parseInt(colValue.toString())) == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastMeteringValue")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpInterval")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("meterStatus")) {
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode != null) {
							tmpStatus = "Success";
						} else {
							status = "Failure";
						}
					} else if (colName.equals("pulseConstant")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("timeDiff")) {
						tmpStatus = longCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("usageThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
				} catch (Exception e) {
					status = "Failure";
				}
			}

			// ############################################### 모뎀
			// /////////////////////////////////////////////// 모뎀타입별 객체 생성
			else if (detailType.equals("ZRU")) {
				try {
					if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpChoice")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						// tmpCount++;
						// tmpModemType =
						// ModemType.valueOf(colValue.toString());
						// if (StringUtil.nullToBlank(colValue).isEmpty() ||
						// tmpModemType == null) {
						// status = "Failure";
						// } else {
						// tmpStatus = "Success";
						// }
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("ZMU")) {
				try {
					if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("ZEUPLS")) {
				try {
					if (colName.equals("activeTime")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("alarmFlag")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("alarmMask")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("autoTrapFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("batteryCapacity")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("batteryVolt")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpChoice")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("LQI")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("operationDay")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("permitMode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("permitState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetReason")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rssi")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("solarADV")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("solarBDCV")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("solarChgBV")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("trapDate")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("trapHour")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("trapMinute")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("trapSecond")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("ZEUMBus")) {
				try {
					if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}

			} else if (detailType.equals("ZBRepeater")) {
				try {
					if (colName.equals("activeTime")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("networkType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							if (ModemNetworkType.valueOf(colValue) == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							if (ModemPowerType.valueOf(colValue) == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("batteryCapacity")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("batteryStatus")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							if (BatteryStatus.valueOf(colValue) == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("batteryVolt")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpChoice")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("operationDay")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("repeatingSetupSec")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("solarADV")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("solarBDCV")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("PLCIU")) {
				try {
					if (colName.equals("sysFactoryReset")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysNodeType")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysReset")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysRtsCtsEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysSerialParityType")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysSerialRate")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysSerialStopBit")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysSerialWordBit")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysService")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysUseDhcp")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("MMIU")) {
				try {
					if (colName.equals("phoneNumber")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("errorStatus")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";

						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("IHD")) {
				try {
					if (colName.equals("billDate")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gasThreshold")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("peakDemandThreshold")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("waterThreshold")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("IEIU")) {
				try {
					if (colName.equals("phoneNumber")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("errorStatus")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("groupNumber")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("memberNumber")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";

						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("HMU")) {
				try {
					if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpChoice")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("ACD")) {
				try {
					if (colName.equals("channelId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lpChoice")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("manualEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("needJoinSet")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("panId")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityEnable")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("testFlag")) {
						tmpStatus = boolCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			} else if (detailType.equals("Converter")) {
				try {
					if (colName.equals("sysPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue.length() > 10) {
							tmpStatus = "Failure";
						} else if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysName")) {
						if (colValue == null || "".equals(colValue)) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("address")) {

					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
				// #################SubGiga ###############################
			} else if (detailType.equals("SubGiga")) {
				try {
					if (colName.equals("baseStationAddress")) {
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("ipv6Address")) {
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("securityKey")) {
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("hopsToBaseStation")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("frequency")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("bandWidth")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
						// ////////////공통////////////////////
					} else if (colName.equals("deviceSerial")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpCount++;
							Modem tmpModem = modemDao.get(colValue);

							if (status == "Success" && tmpModem != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("lpPeriod")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("modemType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpModemType = ModemType.valueOf(colValue.toString());
							if (tmpModemType != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("nodeType")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0
								|| intCheck(colValue.toString()).equals("Failure")) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							tmpSupplier = supplierDao.getSupplierByName(colValue.toString());

							if (tmpSupplier != null && tmpSupplier.getId() != null) {
								tmpCount++;
							}
						}
					} else if (colName.equals("commState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("currentThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioX")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioY")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("gpioZ")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lastResetCode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mcu")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMcu = mcuDao.get(colValue);
							if (tmpMcu == null) {
								status = "Failure";
							}
						}
					} else if (colName.equals("model")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(),
									colValue);

							if (modelList == null || modelList.size() <= 0) {
								status = "Failure";
							}
						}
					} else if (colName.equals("powerThreshold")) {
						tmpStatus = doubleCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("resetCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("rfPower")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && Protocol.valueOf(colValue) == null) {
							status = "Failure";
						}
					}
				} catch (Exception e) {
					status = "Failure";
				}
			}
			// ##################################### 집중기
			else if (detailType.equals(TargetClass.DCU.name())) {
				try {
					if (colName.equals("supplier")) {
						tmpCount++;
						tmpSupplier = supplierDao.getSupplierByName(colValue.toString());
						if (colValue == null || colValue.length() == 0 || tmpSupplier.getId() == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("installDate")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("location")) {
						tmpCount++;
						Location tmpLocation = locationDao.getLocationByName(colValue.toString()).get(0);
						if (colValue == null || colValue.length() == 0 || tmpLocation == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("dcuType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("protocolType")) {
						tmpCount++;
						Code tmpCode = codeDao.get(codeDao.getCodeIdByCode((colValue)));
						if (colValue == null || colValue.length() == 0 || tmpCode == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("deviceModel")) {
						tmpCount++;
						DeviceModel tmpDeviceModel = deviceModelDao.getDeviceModelByName(tmpSupplier.getId(), colValue)
								.get(0);
						if (colValue == null || colValue.length() == 0 || tmpDeviceModel == null) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("sysHwVersion")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("sysID")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && colValue.length() <= 10) {
							tmpCount++;
							MCU tmpMCU = mcuDao.get(colValue);

							if (status == "Success" && tmpMCU != null) {
								status = "Update";
							}
						}
					} else if (colName.equals("sysLocalPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysSwRevision")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("sysSwVersion")) {
						tmpCount++;
						if (colValue == null || colValue.length() == 0) {
							status = "Failure";
						} else {
							tmpStatus = "Success";
						}
					} else if (colName.equals("batteryCapacity")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("fwState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("lowBatteryFlag")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("mobileUsageFlag")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("networkStatus")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("powerState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("serviceAtm")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysCurTemp")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysEtherType")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysJoinNodeCount")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysMaxTemp")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysMinTemp")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysMobileMode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysMobileType")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysMobileVendor")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysOpMode")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysPowerType")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysResetReason")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysServerPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysState")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysStateMask")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysTimeZone")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("sysType")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					} else if (colName.equals("updateServerPort")) {
						tmpStatus = intCheck(colValue.toString());
						if (colValue == null || colValue.length() == 0) {
							tmpStatus = "Success";
						}
						if (tmpStatus.equals("Failure"))
							status = "Failure";
					}
				} catch (Exception e) {
					status = "Failure";
				}
			}
			// ///////////////////////////////////// 데이터 검증 끝

			returnData.put(colName, colValue);
		} // for end : Cell

		// meter의 필수값 개수 체크
		if (detailType.equals("EnergyMeter")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("WaterMeter")) {
			if (tmpCount != 8) {
				status = "Failure";
			}
		} else if (detailType.equals("GasMeter")) {
			if (tmpCount != 8) {
				status = "Failure";
			}
		} else if (detailType.equals("HeatMeter")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("VolumeCorrector")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
			// modem의 필수값 개수 체크
		} else if (detailType.equals("ZRU")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("ZMU")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("ZEUPLS")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("ZEUMBus")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("ZBRepeater")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("PLCIU")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("MMIU")) {
			if (tmpCount != 7) {
				status = "Failure";
			}
		} else if (detailType.equals("IHD")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("IEIU")) {
			if (tmpCount != 7) {
				status = "Failure";
			}
		} else if (detailType.equals("HMU")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("ACD")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("Converter")) {
			if (tmpCount != 6) {
				status = "Failure";
			}
		} else if (detailType.equals("SubGiga")) {

			if (tmpCount != 6) {
				status = "Failure";
			}
			// DCU의 필수값 개수 체크
		} else if (detailType.equals(TargetClass.DCU.name())) {
			if (tmpCount != 10) {
				status = "Failure";
			}
		}

		returnData.put("Status", status);
		tmpStatus = "";
		return returnData;
	}
	
	
	private String getFileMapRead2(Row titles, Row row) throws IOException {

		Map<String, Object> returnData = new HashMap<String, Object>();
		// 상태 데이터 초기화
		int tmpCount = 0;
		String colName = null;
		String colValue = null;
		String tmpStatus = "Success"; // Status (Success or Failure)
		String status = "Success"; // Status (Success or Failure)
		Supplier tmpSupplier = new Supplier();
		ModemType tmpModemType = null;

		//returnData.put("Status", "");
		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}

			colValue = colValue.trim();
		}
		return colValue;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private MCU getMCU(Row titles, Row row, int supplierId) throws IOException {

		Map<String, Object> format = getFormat(DeviceType.MCU, null);
		Map<String, Object> required = (Map<String, Object>) format.get("required");
		Map<String, Object> unrequired = (Map<String, Object>) format.get("unrequired");

		MCU mcu = new MCU();
		mcu.setSupplier(supplierDao.get(supplierId));

		String colName = null;
		String colValue = null;
		Boolean colFlag = false;
		int chkCount = required.size();

		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}
			colValue = colValue.trim();
			// 필수 항목 체크
			if (required.containsValue(colName)) {

				// 필수 항목일 경우 업로드 된 엑셀에 해당 값이 들어있는지 확인
				if (colValue != null && !"".equals(colValue)) {

					chkCount--;

					// MCU 포맷과 업로드 된 엑셀과의 순서 및 항목명이 동일한 지 확인
					if (colName.equals(required.get(cell.getColumnIndex()))) {

						McuEnum item = McuEnum.valueOf(colName);
						mcu = item.getMCU(mcu, colValue);
					} else {
						throw new IOException("=========== 포맷과 다름 !! ===========");
					}
				}
			}

			// 필수 항목 이외
			else {

				if ("".equals(colValue))
					continue;
				else if (!colFlag)
					colFlag = true;

				// MCU 포맷과 업로드 된 엑셀과의 순서 및 항목명이 동일한 지 확인
				if (colName.equals(unrequired.get(cell.getColumnIndex()))) {

					McuEnum item = McuEnum.valueOf(colName);
					mcu = item.getMCU(mcu, colValue);
				}
			}
		} // for end : Cell

		if (colFlag) {
			if (chkCount > 0)
				throw new IOException(); // 필수항목 누락
		} else {
			if (chkCount == required.size())
				mcu = null; 
		}

		return mcu;
	}

	@Transactional
	private Map<String, Object> msisdnUpdate(Row titles, Row row, int supplierId, String fileType, String detailType) throws IOException {
		
		Map<String, Object> contentsMap = new HashMap();
		Map<String, Object> map = new HashMap<String, Object>();

		String colName = null;
		String colValue = null;
		Boolean isUpdate = false;
		
		// FOR Section (S)
		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}

			colValue = colValue.trim();
			
			if (colName.equals("IMSI")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("imsi", colValue);
				} else {
					contentsMap.put("imsi", null);
				}
			}
			
			if (colName.equals("MSISDN")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("msisdn", colValue);
				} else {
					contentsMap.put("msisdn", null);
				}
			}
		} // FOR Section (E)
		  
		String imsi = (String) contentsMap.get("imsi");
		String msisdn = (String) contentsMap.get("msisdn");
		String imsiError = "IMSI ERROR";
		
		if (imsi == null) {
			map.put("imsi", imsiError);
			map.put("isUpdate", isUpdate);
			return map;
		}
		
		try {
			MCU mcu = mcuDao.findByCondition("simNumber", imsi);
			Modem modem = modemDao.findByCondition("simNumber", imsi);

			if (mcu != null) {
				mcu.setSysPhoneNumber(msisdn);
				mcuDao.groupUpdate(mcu);
				isUpdate = true;
				
				map.put("imsi", imsi);
				map.put("isUpdate", isUpdate);
			} else if (modem != null) {
				modem.setPhoneNumber(msisdn);
				modemDao.groupUpdate(modem);
				isUpdate = true;
				
				map.put("imsi", imsi);
				map.put("isUpdate", isUpdate);
			} else {
				map.put("imsi", imsi);
				map.put("isUpdate", isUpdate);
			}
		} catch (Exception e) {
			map.put("imsi", imsi);
			map.put("isUpdate", isUpdate);
			
			return map;
		}
			
		return map;
	}
	
	@Transactional
	private Map<String, Object> simCardUpdate(Row titles, Row row, int supplierId, String fileType, String detailType) throws IOException {
		
		Map<String, Object> contentsMap = new HashMap();
		Map<String, Object> map = new HashMap<String, Object>();

		String colName = null;
		String colValue = null;
		Boolean isUpdate = false;
		boolean isNew = false;
		
		// FOR Section (S)
		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}

			colValue = colValue.trim();
			
			if (colName.equals("ICC ID")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("iccId", colValue);
				} else {
					contentsMap.put("iccId", null);
				}
			}
			
			if (colName.equals("IMSI")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("imsi", colValue);
				} else {
					contentsMap.put("imsi", null);
				}
			}
			
			
			if (colName.equals("MSISDN")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("msisdn", colValue);
				} else {
					contentsMap.put("msisdn", null);
				}
			}
		}
		// FOR Section (E)
		
		String iccId = (String) contentsMap.get("iccId");
		String imsi = (String) contentsMap.get("imsi");
		String msisdn = (String) contentsMap.get("msisdn");
		String iccIdError = "ICC ID ERROR";
		
		if (iccId == null) {
			map.put("iccId", iccIdError);
			map.put("isUpdate", isUpdate);
			return map;
		}
		
		try {
			SimCard simCard =  null;
			simCard = simCardDao.findByCondition("iccId", iccId);
			
			if (simCard == null) {
				// 일치하는 ICC_ID가 없는 경우, 새로 등록
				isNew = true;
				simCard = new SimCard();
				simCard.setIccId(iccId);
			}
			
			if (imsi != null) {
				simCard.setImsi(imsi);
			}
			
			if (msisdn != null) {
				simCard.setPhoneNumber(msisdn);
			}
			
			if (!isNew) {
				simCardDao.groupUpdate(simCard);
			} else {
				simCardDao.add_requires_new(simCard);
				simCardDao.flushAndClear();
			}
			
			isUpdate = true;
			map.put("iccId", iccId);
			map.put("isUpdate", isUpdate);
			
		} catch (Exception e) {
			map.put("iccId", iccId);
			map.put("isUpdate", isUpdate);
			
			return map;
		}
			
		return map;
	}
	
	@Transactional
	private Map<String, Object> shipmentUpdate(Row titles, Row row, int supplierId, String fileType, String detailType) throws IOException {
		Map<String, Object> contentsMap = new HashMap();
		Map<String, Object> map = new HashMap<String, Object>();
		
		String colName = null;
		String colValue = null;
		Boolean isUpdate = false;
		
		// FOR Section (S)
		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}

			colValue = colValue.trim();

			if (colName.equals("PO")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("po", colValue);
				} else {
					contentsMap.put("po", null);
				}
			}

			if (colName.equals("Type")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("type", colValue);
				} else {
					contentsMap.put("type", null);
				}
			}

			if (colName.equals("EUI ID")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					colValue.toUpperCase();
					contentsMap.put("euiId", colValue);
				} else {
					contentsMap.put("euiId", null);
				}
			}

			if (colName.equals("GS1 Code")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("gs1", colValue);
				} else {
					contentsMap.put("gs1", null);
				}
			}

			if (colName.equals("Model")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("model", colValue.trim());
				} else {
					contentsMap.put("model", null);
				}
			}

			if (colName.equals("HW Version")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("hwVer", colValue);
				} else {
					contentsMap.put("hwVer", null);
				}
			}

			if (colName.equals("SW Version")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("swVer", colValue);
				} else {
					contentsMap.put("swVer", null);
				}
			}

			if (colName.equals("IMEI")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("imei", colValue);
				} else {
					contentsMap.put("imei", null);
				}
			}

			if (colName.equals("IMSI")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("imsi", colValue);
				} else {
					contentsMap.put("imsi", null);
				}
			}

			if (colName.equals("ICC ID")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("iccId", colValue);
				} else {
					contentsMap.put("iccId", null);
				}
			}

			if (colName.equals("Production Date")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("productionDate", colValue);
				} else {
					contentsMap.put("productionDate", null);
				}
			}
			
			if (colName.equals("MSISDN")) {
				if (!StringUtil.nullToBlank(colValue).isEmpty()) {
					contentsMap.put("phoneNumber", colValue);
				} else {
					contentsMap.put("phoneNumber", null);
				}
			}
			
		}
		// FOR Section (E)

		String gs1 = (String) contentsMap.get("gs1");
		String hwVer = (String) contentsMap.get("hwVer");
		String swVer = (String) contentsMap.get("swVer");
		String productionDate = (String) contentsMap.get("productionDate");
		String modelName = (String) contentsMap.get("model");
		String typeInExcel = (String) contentsMap.get("type");
		String euiId = (String) contentsMap.get("euiId");
		String euiIdError = "EUI ID ERROR";
		boolean isNew = false;

		// productionDate Setting (S)
		try {
			if (productionDate != null) {
				productionDate = productionDate.replaceAll("[^0-9]", "").substring(0, 8);
			}
		} catch (Exception e) {
			logger.error(e, e);
			
			map.put("euiId", euiId);
			map.put("isUpdate", isUpdate);
			return map;
		}
		// productionDate Setting (E)
		
		if (euiId == null) {
			map.put("euiId", euiIdError);
			map.put("isUpdate", isUpdate);
			return map;
		}
		
		if (typeInExcel == null) {
			map.put("euiId", euiId);
			map.put("isUpdate", isUpdate);
			return map;
		}

		if (fileType.equals(CommonConstants.DeviceType.MCU.name()) && typeInExcel.toUpperCase().equals(TargetClass.DCU.name().toUpperCase())) {
			try {
				MCU mcu = null;
				
				String po = (String) contentsMap.get("po");
				String imei = (String) contentsMap.get("imei");
				String imsi = (String) contentsMap.get("imsi");
				String iccId = (String) contentsMap.get("iccId");
				String phoneNumber = (String) contentsMap.get("phoneNumber");
				String sysId = null;
				
				try {
					sysId = (String) euiId.subSequence(8, 16);
				} catch (Exception e) {
					map.put("euiId", euiIdError);
					map.put("isUpdate", isUpdate);
					return map;
				}
				
				long sysId_long  = Long.parseLong(sysId, 16);   
				String sysId_decimal = String.valueOf(sysId_long);
				
				mcu = mcuDao.findByCondition("sysID", sysId_decimal);
				
				if (mcu == null) {
					// 일치하는 SYS_ID가 없는 경우, Device 등록
					isNew = true;
					mcu = new MCU();
					mcu.setSysID(sysId_decimal);
					mcu.setSysSerialNumber(euiId);
					mcu.setSupplier(supplierDao.get(supplierId));
				}
				
				Code dcuCode = codeDao.getCodeByName(TargetClass.DCU.name());
				mcu.setMcuType(dcuCode);
				 
				if (po != null) {
					mcu.setPo(po);
				}

				if (gs1 != null) {
					mcu.setGs1(gs1);
				}

				if (modelName != null) {
					try {
						DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
						mcu.setDeviceModel(deviceModel);
					} catch (Exception e) {
						return null;
					}
				}

				if (hwVer != null) {
					mcu.setSysHwVersion(hwVer);
				}

				if (swVer != null) {
					mcu.setSysSwVersion(swVer);
				}

				if (imei != null) {
					mcu.setImei(imei);
				}

				if (imsi != null) {
					mcu.setSimNumber(imsi);
				}

				// SIM_CARD 테이블 정보 auto mapping (S)
				if (iccId != null) {
					mcu.setIccId(iccId);

					// excel file에 IMSI가 기재되어있으면, 기재된 정보로 데이터 SET
					if (imsi != null) {
						mcu.setSimNumber(imsi);
					} else {
					// excel file에 IMSI가 기재되어있지 않으면, 심카드 테이블 조회 후 데이터 SET	
						mcu.setSimNumber(simCardDao.findByCondition("iccId", iccId).getImsi());
					}
					
					// excel file에 MSISDN가 기재되어있으면, 기재된 정보로 데이터 SET
					if (phoneNumber != null) {
						mcu.setSysPhoneNumber(phoneNumber);
					} else {
					// excel file에 MSISDN가 기재되어있지 않으면, 심카드 테이블 조회 후 데이터 SET
						mcu.setSysPhoneNumber(simCardDao.findByCondition("iccId", iccId).getPhoneNumber());
					}
				} else {
					if (imsi != null) {
						mcu.setSimNumber(imsi);
					}
					
					if (phoneNumber != null) {
						mcu.setSysPhoneNumber(phoneNumber);
					}
				}
				// SIM_CARD 테이블 정보 auto mapping (E)
				
				if (productionDate != null) {
					mcu.setManufacturedDate(productionDate);
				}

				if (!isNew) {
					mcuDao.groupUpdate(mcu);
				} else {
					mcuDao.mcuAdd(mcu);
					mcuDao.flushAndClear();
				}
				
				isUpdate = true;
				map.put("euiId", mcu.getSysSerialNumber());
				map.put("isUpdate", isUpdate);
			} catch (Exception e) {
				logger.error(e, e);

				map.put("euiId", euiId);
				map.put("isUpdate", isUpdate);
				return map;
			}
		} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
			try {
				String po = (String) contentsMap.get("po");
				String imei = (String) contentsMap.get("imei");
				String imsi = (String) contentsMap.get("imsi");
				String iccId = (String) contentsMap.get("iccId");
				String phoneNumber = (String) contentsMap.get("phoneNumber");
				
				if ((detailType.equals(CommonConstants.ShipmentTargetType.EthernetModem.getName()) && typeInExcel.toUpperCase().equals(CommonConstants.ShipmentTargetType.EthernetModem.getName().toUpperCase())) ||
						(detailType.equals(CommonConstants.ShipmentTargetType.MBBModem.getName()) && typeInExcel.toUpperCase().equals(CommonConstants.ShipmentTargetType.MBBModem.getName().toUpperCase()))) {
					
					MMIU updMMIU = mMIUDao.findByCondition("deviceSerial", euiId);
					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updMMIU == null) {
						isNew = true;
						updMMIU = new MMIU();
						updMMIU.setDeviceSerial(euiId);
						updMMIU.setSupplier(supplierDao.get(supplierId));
					}
					
					if (po != null) {
						updMMIU.setPo(po);
					}

					if (gs1 != null) {
						updMMIU.setGs1(gs1);
					}

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updMMIU.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							return null;
						}
					}

					if (hwVer != null) {
						updMMIU.setHwVer(hwVer);
					}

					if (swVer != null) {
						updMMIU.setSwVer(swVer);
					}

					if (imei != null) {
						updMMIU.setImei(imei);
					}

					// SIM_CARD 테이블 정보 auto mapping (S)
					if (iccId != null) {
						updMMIU.setIccId(iccId);

						// excel file에 IMSI가 기재되어있으면, 기재된 정보로 데이터 SET
						if (imsi != null) {
							updMMIU.setSimNumber(imsi);
						} else {
						// excel file에 IMSI가 기재되어있지 않으면, 심카드 테이블 조회 후 데이터 SET	
							updMMIU.setSimNumber(simCardDao.findByCondition("iccId", iccId).getImsi());
						}
						
						// excel file에 MSISDN가 기재되어있으면, 기재된 정보로 데이터 SET
						if (phoneNumber != null) {
							updMMIU.setPhoneNumber(phoneNumber);
						} else {
						// excel file에 MSISDN가 기재되어있지 않으면, 심카드 테이블 조회 후 데이터 SET
							updMMIU.setPhoneNumber(simCardDao.findByCondition("iccId", iccId).getPhoneNumber());
						}
					} else {
						if (imsi != null) {
							updMMIU.setSimNumber(imsi);
						}
						
						if (phoneNumber != null) {
							updMMIU.setPhoneNumber(phoneNumber);
						}
					}
					// SIM_CARD 테이블 정보 auto mapping (E)
					
					if (productionDate != null) {
						updMMIU.setManufacturedDate(productionDate);
					}
					
					if (!isNew) {
						// 기존의 Device를 Update하는 경우 - 기존의 Device의 Protocol Type과 update하려고하는 모뎀 모델의 Protocol Type을 비교      
						String selectedDeviceProtocol = updMMIU.getProtocolType().name();
						
						if(detailType.equals(CommonConstants.ShipmentTargetType.EthernetModem.getName())) {
							if (!selectedDeviceProtocol.equals(Protocol.IP.name())) {
								return null;
							}
						} else if (detailType.equals(CommonConstants.ShipmentTargetType.MBBModem.getName())) {
							if (!selectedDeviceProtocol.equals(Protocol.SMS.name())) {
								return null;
							}
						}
						
						mMIUDao.groupUpdate(updMMIU);
					} else {
						// 새로운 Device를 등록하는 경우 - protocol type을 각 model에 맞는 protocol로 setting
						if(detailType.equals(CommonConstants.ShipmentTargetType.EthernetModem.getName())) {
							updMMIU.setProtocolType(Protocol.IP.name());
						} else if (detailType.equals(CommonConstants.ShipmentTargetType.MBBModem.getName())) {
							updMMIU.setProtocolType(Protocol.SMS.name());
						}
						
						mMIUDao.modemAdd(updMMIU);
						mMIUDao.flushAndClear();
					}
					
					isUpdate = true;
					map.put("euiId", updMMIU.getDeviceSerial());
					map.put("isUpdate", isUpdate);
				} else if (detailType.equals(CommonConstants.ShipmentTargetType.EthernetConverter.getName()) && typeInExcel.toUpperCase().equals(CommonConstants.ShipmentTargetType.EthernetConverter.getName().toUpperCase())) {
					Converter updConverter  = converterDao.findByCondition("deviceSerial", euiId);
					
					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updConverter == null) {
						isNew = true;
						updConverter = new Converter();
						updConverter.setDeviceSerial(euiId);
						updConverter.setSupplier(supplierDao.get(supplierId));
					}

					if (po != null) {
						updConverter.setPo(po);
					}

					if (gs1 != null) {
						updConverter.setGs1(gs1);
					}

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updConverter.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							return null;
						}
					}

					if (hwVer != null) {
						updConverter.setHwVer(hwVer);
					}

					if (swVer != null) {
						updConverter.setSwVer(swVer);
					}

					if (imei != null) {
						updConverter.setImei(imei);
					}

					if (imsi != null) {
						updConverter.setSimNumber(imsi);
					}

					if (iccId != null) {
						updConverter.setIccId(iccId);
					}

					if (productionDate != null) {
						updConverter.setManufacturedDate(productionDate);
					}

					if (!isNew) {
						converterDao.groupUpdate(updConverter);
					} else {
						converterDao.modemAdd(updConverter);
						converterDao.flushAndClear();
					}

					isUpdate = true;
					map.put("euiId", updConverter.getDeviceSerial());
					map.put("isUpdate", isUpdate);
				} else if (detailType.equals(CommonConstants.ShipmentTargetType.RFModem.getName()) && typeInExcel.toUpperCase().equals(CommonConstants.ShipmentTargetType.RFModem.getName().toUpperCase())) {
					SubGiga updSubGiga = subGigaDao.findByCondition("deviceSerial", euiId);

					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updSubGiga == null) {
						isNew = true;
						updSubGiga = new SubGiga();
						updSubGiga.setDeviceSerial(euiId);
						updSubGiga.setSupplier(supplierDao.get(supplierId));
					}
					
					if (po != null) {
						updSubGiga.setPo(po);
					}

					if (gs1 != null) {
						updSubGiga.setGs1(gs1);
					}

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updSubGiga.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							return null;
						}
					}

					if (hwVer != null) {
						updSubGiga.setHwVer(hwVer);
					}

					if (swVer != null) {
						updSubGiga.setSwVer(swVer);
					}

					if (imei != null) {
						updSubGiga.setImei(imei);
					}

					if (imsi != null) {
						updSubGiga.setSimNumber(imsi);
					}

					if (iccId != null) {
						updSubGiga.setIccId(iccId);
					}

					if (productionDate != null) {
						updSubGiga.setManufacturedDate(productionDate);
					}

					if (!isNew) {
						// 기존의 Device를 Update하는 경우 - 기존의 Device의 Protocol Type과 update하려고하는 모뎀 모델의 Protocol Type을 비교      
						String selectedDeviceProtocol = updSubGiga.getProtocolType().name();
						
						if (!selectedDeviceProtocol.equals(Protocol.IP.name())) {
							return null;
						}
						
						subGigaDao.groupUpdate(updSubGiga);
					} else {
						// 새로운 Device를 등록하는 경우 - protocol type을 각 model에 맞는 protocol로 setting
						updSubGiga.setProtocolType(Protocol.IP.name());
						
						subGigaDao.modemAdd(updSubGiga);
						subGigaDao.flushAndClear();
					}
					
					isUpdate = true;
					map.put("euiId", updSubGiga.getDeviceSerial());
					map.put("isUpdate", isUpdate);
				} else {
					map.put("euiId", euiId);
					map.put("isUpdate", isUpdate);	// update fail
					return map;
				}
			} catch (Exception e) {
				logger.error(e,e);
				
				map.put("euiId", euiId);
				map.put("isUpdate", isUpdate);
				return map;
			}
		} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
			try {
				if (detailType.equals(CommonConstants.MeterType.EnergyMeter.name()) && typeInExcel.toUpperCase().equals(CommonConstants.MeterType.EnergyMeter.name().toUpperCase())) {
					EnergyMeter updEnergyMeter = energyMeterDao.findByCondition("mdsId", euiId);
			
					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updEnergyMeter == null) {
						isNew = true;
						updEnergyMeter = new EnergyMeter();
						updEnergyMeter.setMdsId(euiId);
						updEnergyMeter.setSupplier(supplierDao.get(supplierId));
					}

					// if (gs1 != null) {
					//	updEnergyMeter.setGs1(gs1);
					// }

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updEnergyMeter.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							
							map.put("euiId", euiId);
							map.put("isUpdate", isUpdate);
							return map;
						}
					}

					if (hwVer != null) {
						updEnergyMeter.setHwVersion(hwVer);
					}

					if (swVer != null) {
						updEnergyMeter.setSwVersion(swVer);
					}

					if (productionDate != null) {
						updEnergyMeter.setManufacturedDate(productionDate);
					}

					if (!isNew) {
						energyMeterDao.groupUpdate(updEnergyMeter);
					} else {
						updEnergyMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));	// INSERT SP-599
						energyMeterDao.meterAdd(updEnergyMeter);
						energyMeterDao.flushAndClear();
					}
					
					isUpdate = true;
					map.put("euiId", updEnergyMeter.getMdsId());
					map.put("isUpdate", isUpdate);
					
				} else if (detailType.equals(CommonConstants.MeterType.WaterMeter.name()) && typeInExcel.toUpperCase().equals(CommonConstants.MeterType.WaterMeter.name().toUpperCase())) {
					WaterMeter updWaterMeter = waterMeterDao.findByCondition("mdsId", euiId);
					
					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updWaterMeter == null) {
						isNew = true;
						updWaterMeter = new WaterMeter();
						updWaterMeter.setMdsId(euiId);
						updWaterMeter.setSupplier(supplierDao.get(supplierId));
					}

					if (gs1 != null) {
						updWaterMeter.setGs1(gs1);
					}

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updWaterMeter.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							
							map.put("euiId", euiId);
							map.put("isUpdate", isUpdate);
							return map;
						}
					}

					if (hwVer != null) {
						updWaterMeter.setHwVersion(hwVer);
					}

					if (swVer != null) {
						updWaterMeter.setSwVersion(swVer);
					}

					if (productionDate != null) {
						updWaterMeter.setManufacturedDate(productionDate);
					}

					if (!isNew) {
						waterMeterDao.groupUpdate(updWaterMeter);
					} else {
						updWaterMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));	// INSERT SP-599						
						waterMeterDao.meterAdd(updWaterMeter);
						waterMeterDao.flushAndClear();
					}
					
					isUpdate = true;
					map.put("euiId", updWaterMeter.getMdsId());
					map.put("isUpdate", isUpdate);
				} else if (detailType.equals(CommonConstants.MeterType.GasMeter.name()) && typeInExcel.toUpperCase().equals(CommonConstants.MeterType.GasMeter.name().toUpperCase())) {
					GasMeter updGasMeter = gasMeterDao.findByCondition("mdsId", euiId);
					
					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updGasMeter == null) {
						isNew = true;
						updGasMeter = new GasMeter();
						updGasMeter.setMdsId(euiId);
						updGasMeter.setSupplier(supplierDao.get(supplierId));
					}

					if (gs1 != null) {
						updGasMeter.setGs1(gs1);
					}

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updGasMeter.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							
							map.put("euiId", euiId);
							map.put("isUpdate", isUpdate);
							return map;
						}
					}

					if (hwVer != null) {
						updGasMeter.setHwVersion(hwVer);
					}

					if (swVer != null) {
						updGasMeter.setSwVersion(swVer);
					}

					if (productionDate != null) {
						updGasMeter.setManufacturedDate(productionDate);
					}

					if (!isNew) {
						gasMeterDao.groupUpdate(updGasMeter);
					} else {
						updGasMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));	// INSERT SP-599						
						gasMeterDao.meterAdd(updGasMeter);
						gasMeterDao.flushAndClear();
					}
					
					isUpdate = true;
					map.put("euiId", updGasMeter.getMdsId());
					map.put("isUpdate", isUpdate);
				} else if (detailType.equals(CommonConstants.MeterType.HeatMeter.name()) && typeInExcel.toUpperCase().equals(CommonConstants.MeterType.HeatMeter.name().toUpperCase())) {
					HeatMeter updHeatMeter = heatMeterDao.findByCondition("mdsId", euiId);

					// 일치하는 EUI ID가 없는 경우, Device 등록
					if (updHeatMeter == null) {
						isNew = true;
						updHeatMeter = new HeatMeter();
						updHeatMeter.setMdsId(euiId);
						updHeatMeter.setSupplier(supplierDao.get(supplierId));
					}
					
					if (gs1 != null) {
						updHeatMeter.setGs1(gs1);
					}

					if (modelName != null) {
						try {
							DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, modelName).get(0);
							updHeatMeter.setModel(deviceModel);
						} catch (Exception e) {
							logger.error(e,e);
							
							map.put("euiId", euiId);
							map.put("isUpdate", isUpdate);
							return map;
						}
					}

					if (hwVer != null) {
						updHeatMeter.setHwVersion(hwVer);
					}

					if (swVer != null) {
						updHeatMeter.setSwVersion(swVer);
					}

					if (productionDate != null) {
						updHeatMeter.setManufacturedDate(productionDate);
					}

					if (!isNew) {
						heatMeterDao.groupUpdate(updHeatMeter);
					} else {
						updHeatMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));	// INSERT SP-599						
						heatMeterDao.meterAdd(updHeatMeter);
						heatMeterDao.flushAndClear();
					}
					
					isUpdate = true;
					map.put("euiId", updHeatMeter.getMdsId());
					map.put("isUpdate", isUpdate);
				} else {
					map.put("euiId", euiId);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} catch (Exception e) {
				logger.error(e,e);
				
				map.put("euiId", euiId);
				map.put("isUpdate", isUpdate);
				return map;
			}
		} else {
			map.put("euiId", euiId);
			map.put("isUpdate", isUpdate);
			return map;
		}

		return map;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> getAllMCU(Row titles, Row row, int supplierId, String detailType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Map<String, Object> format = getFormat(DeviceType.MCU, null);
			Map<String, Object> required = (Map<String, Object>) format.get("required");
			Map<String, Object> unrequired = (Map<String, Object>) format.get("unrequired");

			MCU mcu = new MCU();
			mcu.setSupplier(supplierDao.get(supplierId));

			String colName = null;
			String colValue = null;
			Boolean colFlag = false;
			Boolean isUpdate = false;
			int chkCount = required.size();

			for (Cell cell : row) {
				if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
					break;
				}
				colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					colValue = cell.getRichStringCellValue().getString();
					break;

				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						colValue = cell.getDateCellValue().toString();
					} else {
						Long roundVal = Math.round(cell.getNumericCellValue());
						Double doubleVal = cell.getNumericCellValue();
						if (doubleVal.equals(roundVal.doubleValue())) {
							colValue = String.valueOf(roundVal);
						} else {
							colValue = String.valueOf(doubleVal);
						}
					}
					break;

				case Cell.CELL_TYPE_BOOLEAN:
					colValue = String.valueOf(cell.getBooleanCellValue());
					break;

				case Cell.CELL_TYPE_FORMULA:
					colValue = cell.getCellFormula();
					break;

				default:
					colValue = "";
				}
				colValue = colValue.trim();

				try {
					if (colName.equals("supplier")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							Supplier supplier = supplierDao.getSupplierByName(colValue);
							if (supplier.getId() != null) {
								mcu.setSupplier(supplier);
							} else {
								logger.info("Not Exist");
								return null;
							}
						} else {
							return null;
						}
					} else if (colName.equals("installDate")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setInstallDate(colValue.toString());
						} else {
							return null;
						}
					} else if (colName.equals("location")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							List<Location> locList = locationDao.getLocationByName(colValue.toString());
							if (locList != null && locList.size() > 0) {
								mcu.setLocation(locList.get(0));
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else if (colName.equals("dcuType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setMcuType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
						} else {
							return null;
						}
					} else if (colName.equals("protocolType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setProtocolType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
						} else {
							return null;
						}
					} else if (colName.equals("deviceModel")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty() && mcu.getSupplier() != null) {
							List<DeviceModel> devices = deviceModelDao.getDeviceModelByName(mcu.getSupplier().getId(),
									colValue);

							if (devices != null && devices.size() > 0) {
								mcu.setDeviceModel(devices.get(0));
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else if (colName.equals("sysHwVersion")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysHwVersion(colValue);
						} else {
							return null;
						}
					} else if (colName.equals("sysID")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCU tmpMCU = mcuDao.get(colValue.toString());
							if (tmpMCU != null) {
								isUpdate = true;
							}
							mcu.setSysID(colValue);
						} else {
							return null;
						}
					} else if (colName.equals("sysLocalPort")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysLocalPort(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysSwRevision")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysSwRevision(colValue);
						} else {
							return null;
						}
					} else if (colName.equals("sysSwVersion")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysSwVersion(colValue);
						} else {
							return null;
						}
					} else if (colName.equals("batteryCapacity")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setBatteryCapacity(Integer.parseInt(colValue));
						}
					} else if (colName.equals("fwState")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setFwState(Integer.parseInt(colValue));
						}
					} else if (colName.equals("ipAddr")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setIpAddr(colValue);
						}
					} else if (colName.equals("lastCommDate")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setLastCommDate(colValue);
						}
					} else if (colName.equals("lastModifiedDate")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setLastModifiedDate(colValue);
						}
					} else if (colName.equals("lastswUpdateDate")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setLastswUpdateDate(colValue);
						}
					} else if (colName.equals("lastTimeSyncDate")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setLastTimeSyncDate(colValue);
						}
					} else if (colName.equals("locDetail")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setLocDetail(colValue);
						}
					} else if (colName.equals("lowBatteryFlag")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setLowBatteryFlag(Integer.parseInt(colValue));
						}
					} else if (colName.equals("dcuCodi")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCUCodi codi = new MCUCodi();
							codi.setMcuCodiBinding(new MCUCodiBinding());
							codi.setMcuCodiDevice(new MCUCodiDevice());
							codi.setMcuCodiMemory(new MCUCodiMemory());
							codi.setMcuCodiNeighbor(new MCUCodiNeighbor());

							mcuCodiDao.add(codi);
							mcu.setMcuCodi(codi);
						}
					} else if (colName.equals("dcuVar")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							MCUVar dcuVar = new MCUVar();
							mcuVarDao.add(dcuVar);
							mcu.setMcuVar(dcuVar);
						}
					} else if (colName.equals("mobileUsageFlag")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setMobileUsageFlag(Integer.parseInt(colValue));
						}
					} else if (colName.equals("networkStatus")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setNetworkStatus(Integer.parseInt(colValue));
						}
					} else if (colName.equals("powerState")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setPowerState(Integer.parseInt(colValue));
						}
					} else if (colName.equals("serviceAtm")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setServiceAtm(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysContanct")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysContact(colValue);
						}
					} else if (colName.equals("sysCurTemp")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysCurTemp(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysDescr")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysDescr(colValue);
						}
					} else if (colName.equals("sysEtherType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysEtherType(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysJoinNodeCount")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysJoinNodeCount(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysLocation")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysLocation(colValue);
						}
					} else if (colName.equals("sysMaxTemp")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysMaxTemp(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysMinTemp")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysMinTemp(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysMobileAccessPoinstName")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysMobileAccessPointName(colValue);
						}
					} else if (colName.equals("sysMobileMode")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysMobileMode(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysMobileType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysMobileType(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysMobileVendor")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysMobileVendor(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysModel")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysModel(colValue);
						}
					} else if (colName.equals("sysName")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysName(colValue);
						}
					} else if (colName.equals("sysOpMode")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysOpMode(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysPhoneNumber")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysPhoneNumber(colValue);
						}
					} else if (colName.equals("sysPowerType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysPowerType(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysResetReason")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysResetReason(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysServer")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysServer(colValue);
						}
					} else if (colName.equals("sysServerAlarmPort")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysServer(colValue);
						}
					} else if (colName.equals("sysServerPort")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysServerPort(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysState")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysState(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysStateMask")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysStateMask(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysTime")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysTime(colValue);
						}
					} else if (colName.equals("sysTimeZone")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysTimeZone(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysType")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysType(Integer.parseInt(colValue));
						}
					} else if (colName.equals("sysUpTime")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysUpTime(colValue);
						}
					} else if (colName.equals("sysVendor")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setSysVendor(colValue);
						}
					} else if (colName.equals("updateServerPort")) {
						if (!StringUtil.nullToBlank(colValue).isEmpty()) {
							mcu.setUpdateServerPort(Integer.parseInt(colValue));
						}
					}
				} catch (Exception e) {
					return null;
				}
			} // for end : Cell

			if (mcu.getSupplier() == null || mcu.getDeviceModel() == null
					|| StringUtil.nullToBlank(mcu.getInstallDate()).isEmpty() || mcu.getLocation() == null
					|| mcu.getMcuType() == null || mcu.getProtocolType() == null
					|| StringUtil.nullToBlank(mcu.getSysHwVersion()).isEmpty()
					|| StringUtil.nullToBlank(mcu.getSysID()).isEmpty()
					|| StringUtil.nullToBlank(mcu.getSysSwRevision()).isEmpty()
					|| StringUtil.nullToBlank(mcu.getSysSwVersion()).isEmpty()) {
				return null;
			} else {
				map.put("MCU", mcu);
				map.put("isUpdate", isUpdate);
				return map;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Meter getMeter(Row titles, Row row, int supplierId, String detailType) throws IOException {

		Map<String, Object> format = getFormat(DeviceType.Meter, MeterType.valueOf(detailType).name());
		Map<String, Object> required = (Map<String, Object>) format.get("required");
		Map<String, Object> unrequired = (Map<String, Object>) format.get("unrequired");

		Meter meter = new Meter();
		meter.setSupplier(supplierDao.get(supplierId));

		String colName = null;
		String colValue = null;
		Boolean colFlag = false;
		int chkCount = required.size();

		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}
			colValue = colValue.trim();

			// 필수 항목 체크
			if (required.containsValue(colName)) {

				if (colValue != null && !"".equals(colValue)) {

					chkCount--;

					// Meter 포맷과 업로드 된 엑셀과의 순서 및 항목명이 동일한 지 확인
					if (colName.equals(required.get(cell.getColumnIndex()))) {

						MeterEnum item = MeterEnum.valueOf(colName);
						meter = item.getMeter(meter, colValue);
					} else {
						throw new IOException("=========== 포맷과 다름 !! ===========");
					}
				}
			}

			// 필수 항목 이외
			else {

				if ("".equals(colValue))
					continue;
				else if (!colFlag)
					colFlag = true;

				// Meter 포맷과 업로드 된 엑셀과의 순서 및 항목명이 동일한 지 확인
				if (colName.equals(unrequired.get(cell.getColumnIndex()))) {

					MeterEnum item = MeterEnum.valueOf(colName);
					meter = item.getMeter(meter, colValue);
				}
			}

		} // for end : Cell

		if (colFlag) {
			if (chkCount > 0)
				throw new IOException(); // 필수항목 누락
		} else {
			if (chkCount == required.size())
				meter = null;
		}

		return meter;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> getAllMeter(Row titles, Row row, int supplierId, String detailType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			Map<String, Object> format = getFormat(DeviceType.Meter, MeterType.valueOf(detailType).name());
			Map<String, Object> required = (Map<String, Object>) format.get("required");
			Map<String, Object> unrequired = (Map<String, Object>) format.get("unrequired");

			Meter tmpMeter = new Meter();

			Meter meter = new Meter();
			EnergyMeter energyMeter = new EnergyMeter();
			WaterMeter waterMeter = new WaterMeter();
			GasMeter gasMeter = new GasMeter();
			HeatMeter heatMeter = new HeatMeter();
			VolumeCorrector vcMeter = new VolumeCorrector();

			String colName = null;
			String colValue = null;
			Boolean colFlag = false;
			Boolean isUpdate = false;
			int chkCount = required.size();

			for (Cell cell : row) {
				if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
					break;
				}
				colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					colValue = cell.getRichStringCellValue().getString();
					break;

				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						colValue = cell.getDateCellValue().toString();
					} else {
						Long roundVal = Math.round(cell.getNumericCellValue());
						Double doubleVal = cell.getNumericCellValue();
						if (doubleVal.equals(roundVal.doubleValue())) {
							colValue = String.valueOf(roundVal);
						} else {
							colValue = String.valueOf(doubleVal);
						}
					}
					break;

				case Cell.CELL_TYPE_BOOLEAN:
					colValue = String.valueOf(cell.getBooleanCellValue());
					break;

				case Cell.CELL_TYPE_FORMULA:
					colValue = cell.getCellFormula();
					break;

				default:
					colValue = "";
				}
				colValue = colValue.trim();
				// ///////////////////////////////////////////////// 미터타입별 객체 생성
				if (detailType.equals("EnergyMeter")) {
					try {
						if (colName.equals("ct")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setCt(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("pt") || colName.equals("vt")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setVt(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("dstApplyOn")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1")) {
									energyMeter.setDstApplyOn(true);
								} else if (colValue.equals("0")) {
									energyMeter.setDstApplyOn(false);
								} else {
									return null;
								}
							}
						} else if (colName.equals("dstSeasonOn")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1")) {
									energyMeter.setDstSeasonOn(true);
								} else if (colValue.equals("0")) {
									energyMeter.setDstSeasonOn(false);
								} else {
									return null;
								}
							}
						} else if (colName.equals("meterElement")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setMeterElement(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							}
						} else if (colName.equals("switchActivateStatus")) {
							if (colValue == null || colValue.length() == 0) {
							}
						} else if (colName.equals("switchStatus")) {
							if (colValue == null || colValue.length() == 0) {
							}
						} else if (colName.equals("transformerRatio")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								energyMeter.setTransformerRatio(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("vt")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								energyMeter.setVt(Double.parseDouble(colValue.toString()));
							}
						}
						// ////////////////////////////////////////////////공통
						else if (colName.equals("installDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setInstallDate(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("Location")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<Location> locationList = locationDao.getLocationByName(colValue);
								if (locationList.size() == 0) {
									return null;
								} else {
									energyMeter.setLocation(locationList.get(0));
								}
							} else {
								return null;
							}
						} else if (colName.equals("mdsId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Meter tmpMdsId = meterDao.get(colValue.toString());
								if (tmpMdsId != null) {
									isUpdate = true;
								}
								energyMeter.setMdsId(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("meterType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Code code = codeDao.findByCondition("code", colValue);
								if (code != null) {
									energyMeter.setMeterType(code);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
								if (supplier != null && supplier.getId() != null) {
									energyMeter.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> devices = deviceModelDao
										.getDeviceModelByName(energyMeter.getSupplier().getId(), colValue);

								if (devices.size() > 0) {
									energyMeter.setModel(devices.get(0));
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("modemPort")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setModemPort(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("prepaymentMeter")) {
							if (colValue.equals("1")) {
								energyMeter.setPrepaymentMeter(true);
							} else if (colValue.equals("0")) {
								energyMeter.setPrepaymentMeter(false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("Address")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setAddress(colValue.toString());
							}
						} else if (colName.equals("endDevice")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setEndDevice(endDeviceDao.get(Integer.parseInt(colValue.toString())));// EndDevice
							}
						} else if (colName.equals("expirationDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setExpirationDate(colValue.toString());
							}
						} else if (colName.equals("gpioX") || colName.equals("gpsX")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY") || colName.equals("gpsY")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ") || colName.equals("gpsZ")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setHwVersion(colValue.toString());
							}
						} else if (colName.equals("ihdId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setIhdId(colValue.toString());
							}
						} else if (colName.equals("installedSiteImg")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setInstalledSiteImg(colValue.toString());
							}
						} else if (colName.equals("installProperty")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setInstallProperty(colValue.toString());
							}
						} else if (colName.equals("lastMeteringValue")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setLastMeteringValue(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lastReadDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setLastReadDate(colValue.toString());
							}
						} else if (colName.equals("lastTimesyncDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setLastTimesyncDate(colValue.toString());
							}
						} else if (colName.equals("lpInterval")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setLpInterval(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("meterCaution")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setMeterCaution(colValue.toString());
							}
						} else if (colName.equals("meterError")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setMeterError(colValue.toString());
							}
						} else if (colName.equals("meterStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setMeterStatus(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							}
						} else if (colName.equals("modem")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setModem(modemDao.get(colValue));
							}
						} else if (colName.equals("pulseConstant")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setPulseConstant(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("qualifiedDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setQualifiedDate(colValue.toString());
							}
						} else if (colName.equals("swName")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setSwName(colValue.toString());
							}
						} else if (colName.equals("swUpdateDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setSwUpdateDate(colValue.toString());
							}
						} else if (colName.equals("swVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setSwVersion(colValue.toString());
							}
						} else if (colName.equals("timeDiff")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setTimeDiff(Long.parseLong(colValue.toString()));
							}
						} else if (colName.equals("usageThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setUsageThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("writeDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								energyMeter.setWriteDate(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
					// ////// EnergyMeter End ///////
					// ////// WaterMeter Start ///////
				} else if (detailType.equals("WaterMeter")) {
					try {
						if (colName.equals("correctPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setCorrectPulse(Double.parseDouble(colValue.toString()));
							} else {
								return null;
							}
						} else if (colName.equals("currentPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setCurrentPulse(Integer.parseInt(colValue.toString()));
							} else {
								return null;
							}
						} else if (colName.equals("initPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setInitPulse(Double.parseDouble(colValue.toString()));
							}

						} else if (colName.equals("meterSize")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setMeterSize(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("Qmax")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setQMax(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("underGround")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1")) {
									waterMeter.setUnderGround(true);
								} else if (colValue.equals("0")) {
									waterMeter.setUnderGround(false);
								} else {
									return null;
								}
							}
							// ////////////////////////////////////////////////공통
						} else if (colName.equals("installDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setInstallDate(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("Location")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setLocation(locationDao.getLocationByName(colValue).get(0));
							} else {
								return null;
							}
						} else if (colName.equals("mdsId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Meter tmpMdsId = meterDao.get(colValue.toString());

								if (tmpMdsId != null) {
									isUpdate = true;
								}
								waterMeter.setMdsId(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("meterType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setMeterType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							} else {
								return null;
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
								if (supplier != null && supplier.getId() != null) {
									waterMeter.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty() && waterMeter.getSupplier() != null) {
								List<DeviceModel> devices = deviceModelDao
										.getDeviceModelByName(waterMeter.getSupplier().getId(), colValue);

								if (devices != null && devices.size() > 0) {
									waterMeter.setModel(devices.get(0));
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("modemPort")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setModemPort(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("prepaymentMeter")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1")) {
									waterMeter.setPrepaymentMeter(true);
								} else if (colValue.equals("0")) {
									waterMeter.setPrepaymentMeter(false);
								} else {
									return null;
								}
							}
						} else if (colName.equals("Address")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setAddress(colValue.toString());
							}
						} else if (colName.equals("endDevice")) {
							// TODO - 데이터가 이상함. ID 가 아닌 Serial Number 로 하는게
							// 나아보임.
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								EndDevice device = endDeviceDao.get(Integer.parseInt(colValue.toString()));

								if (device != null) {
									waterMeter.setEndDevice(device);
								}
							}
						} else if (colName.equals("expirationDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setExpirationDate(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setHwVersion(colValue.toString());
							}
						} else if (colName.equals("ihdId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setIhdId(colValue.toString());
							}
						} else if (colName.equals("installedSiteImg")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setInstalledSiteImg(colValue.toString());
							}
						} else if (colName.equals("installProperty")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setInstallProperty(colValue.toString());
							}
						} else if (colName.equals("lastMeteringValue")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setLastMeteringValue(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lastReadDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setLastReadDate(colValue.toString());
							}
						} else if (colName.equals("lastTimesyncDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setLastTimesyncDate(colValue.toString());
							}
						} else if (colName.equals("lpInterval")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setLpInterval(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("meterCaution")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setMeterCaution(colValue.toString());
							}
						} else if (colName.equals("meterError")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setMeterError(colValue.toString());
							}
						} else if (colName.equals("meterStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setMeterStatus(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							}
						} else if (colName.equals("modem")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setModem(modemDao.get(colValue));
							}
						} else if (colName.equals("pulseConstant")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setPulseConstant(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("qualifiedDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setQualifiedDate(colValue.toString());
							}
						} else if (colName.equals("swName")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setSwName(colValue.toString());
							}
						} else if (colName.equals("swUpdateDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setSwUpdateDate(colValue.toString());
							}
						} else if (colName.equals("swVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setSwVersion(colValue.toString());
							}
						} else if (colName.equals("timeDiff")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setTimeDiff(Long.parseLong(colValue.toString()));
							}
						} else if (colName.equals("usageThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setUsageThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("writeDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								waterMeter.setWriteDate(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
					// ////// WaterMeter End ///////
					// ////// GasMeter Start ///////
				} else if (detailType.equals("GasMeter")) {
					try {
						if (colName.equals("correctPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setCorrectPulse(Double.parseDouble(colValue.toString()));
							} else {
								return null;
							}
						} else if (colName.equals("currentPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setCurrentPulse(Double.parseDouble(colValue.toString()));
							} else {
								return null;
							}
						} else if (colName.equals("initPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setInitPulse(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("valveStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setAlarmStatus(Integer.parseInt(colValue.toString()));
							}
							// ////////////////////////////////////////////////공통
						} else if (colName.equals("installDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setInstallDate(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("Location")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setLocation(locationDao.getLocationByName(colValue).get(0));
							} else {
								return null;
							}
						} else if (colName.equals("mdsId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Meter tmpMdsId = meterDao.get(colValue.toString());

								if (tmpMdsId != null) {
									isUpdate = true;
								}
								gasMeter.setMdsId(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("meterType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setMeterType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							} else {
								return null;
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
								if (supplier != null && supplier.getId() != null) {
									gasMeter.setSupplier(supplier);
								} else {
									logger.info("Not Exist");
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty() && gasMeter.getSupplier() != null) {
								List<DeviceModel> devices = deviceModelDao
										.getDeviceModelByName(gasMeter.getSupplier().getId(), colValue);
								gasMeter.setModel(devices.get(0));
							} else {
								return null;
							}
						} else if (colName.equals("modemPort")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setModemPort(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("prepaymentMeter")) {
							if (colValue.equals("1")) {
								gasMeter.setPrepaymentMeter(true);
							} else if (colValue.equals("0")) {
								gasMeter.setPrepaymentMeter(false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("Address")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setAddress(colValue.toString());
							}
						} else if (colName.equals("endDevice")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setEndDevice(endDeviceDao.get(Integer.parseInt(colValue.toString())));// EndDevice
																												// Id.
							}
						} else if (colName.equals("expirationDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setExpirationDate(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setHwVersion(colValue.toString());
							}
						} else if (colName.equals("ihdId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setIhdId(colValue.toString());
							}
						} else if (colName.equals("installedSiteImg")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setInstalledSiteImg(colValue.toString());
							}
						} else if (colName.equals("installProperty")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setInstallProperty(colValue.toString());
							}
						} else if (colName.equals("lastMeteringValue")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setLastMeteringValue(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lastReadDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setLastReadDate(colValue.toString());
							}
						} else if (colName.equals("lastTimesyncDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setLastTimesyncDate(colValue.toString());
							}
						} else if (colName.equals("lpInterval")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setLpInterval(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("meterCaution")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setMeterCaution(colValue.toString());
							}
						} else if (colName.equals("meterError")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setMeterError(colValue.toString());
							}
						} else if (colName.equals("meterStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setMeterStatus(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							}
						} else if (colName.equals("modem")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setModem(modemDao.get(colValue));
							}
						} else if (colName.equals("pulseConstant")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setPulseConstant(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("qualifiedDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setQualifiedDate(colValue.toString());
							}
						} else if (colName.equals("swName")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setSwName(colValue.toString());
							}
						} else if (colName.equals("swUpdateDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setSwUpdateDate(colValue.toString());
							}
						} else if (colName.equals("swVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setSwVersion(colValue.toString());
							}
						} else if (colName.equals("timeDiff")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setTimeDiff(Long.parseLong(colValue.toString()));
							}
						} else if (colName.equals("usageThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setUsageThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("writeDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								gasMeter.setWriteDate(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
					// ////// GasMeter End ///////
					// ////// HeatMeter Start ///////
				} else if (detailType.equals("HeatMeter")) {
					try {
						if (colName.equals("apparatusRoomNumber")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setApparatusRoomNumber(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("flowPerUnitPulse")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setFlowPerUnitPulse(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("heatingArea")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setHeatingArea(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("heatType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setHeatType(codeDao.get(codeDao.getCodeIdByCode(colValue.toString())));
							}
						} else if (colName.equals("installedPressSensor")) {
							if (colValue.equals("1")) {
								heatMeter.setInstalledPressSensor(true);
							} else if (colValue.equals("0")) {
								heatMeter.setInstalledPressSensor(false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("meteringUnit")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setMeteringUnit(colValue.toString());
							}
						} else if (colName.equals("numOfRoom")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setNumOfRoom(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("standard")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setStandard(colValue.toString());
							}
							// ////////////////////////////////////////////////공통
						} else if (colName.equals("installDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setInstallDate(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("Location")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setLocation(locationDao.getLocationByName(colValue).get(0));
							} else {
								return null;
							}
						} else if (colName.equals("mdsId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Meter tmpMdsId = meterDao.get(colValue.toString());

								if (tmpMdsId != null) {
									isUpdate = true;
								}
								heatMeter.setMdsId(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("meterType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setMeterType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							} else {
								return null;
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
								if (supplier.getId() != null) {
									heatMeter.setSupplier(supplier);
								} else {
									throw new Exception("Not Exist");
								}
							} else {
								return null;
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty() && heatMeter.getSupplier() != null) {
								List<DeviceModel> devices = deviceModelDao
										.getDeviceModelByName(heatMeter.getSupplier().getId(), colValue);
								heatMeter.setModel(devices.get(0));
							} else {
								return null;
							}
						} else if (colName.equals("modemPort")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setModemPort(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("prepaymentMeter")) {
							if (colValue.equals("1")) {
								heatMeter.setPrepaymentMeter(true);
							} else if (colValue.equals("0")) {
								heatMeter.setPrepaymentMeter(false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("Address")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setAddress(colValue.toString());
							}
						} else if (colName.equals("endDevice")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setEndDevice(endDeviceDao.get(Integer.parseInt(colValue.toString())));// EndDevice
																												// Id.
							}
						} else if (colName.equals("expirationDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setExpirationDate(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setHwVersion(colValue.toString());
							}
						} else if (colName.equals("ihdId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setIhdId(colValue.toString());
							}
						} else if (colName.equals("installedSiteImg")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setInstalledSiteImg(colValue.toString());
							}
						} else if (colName.equals("installProperty")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setInstallProperty(colValue.toString());
							}
						} else if (colName.equals("lastMeteringValue")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setLastMeteringValue(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lastReadDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setLastReadDate(colValue.toString());
							}
						} else if (colName.equals("lastTimesyncDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setLastTimesyncDate(colValue.toString());
							}
						} else if (colName.equals("lpInterval")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setLpInterval(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("meterCaution")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setMeterCaution(colValue.toString());
							}
						} else if (colName.equals("meterError")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setMeterError(colValue.toString());
							}
						} else if (colName.equals("meterStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setMeterStatus(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							}
						} else if (colName.equals("modem")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setModem(modemDao.get(colValue));
							}
						} else if (colName.equals("pulseConstant")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setPulseConstant(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("qualifiedDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setQualifiedDate(colValue.toString());
							}
						} else if (colName.equals("swName")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setSwName(colValue.toString());
							}
						} else if (colName.equals("swUpdateDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setSwUpdateDate(colValue.toString());
							}
						} else if (colName.equals("swVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setSwVersion(colValue.toString());
							}
						} else if (colName.equals("timeDiff")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setTimeDiff(Long.parseLong(colValue.toString()));
							}
						} else if (colName.equals("usageThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setUsageThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("writeDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								heatMeter.setWriteDate(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
					// ////// HeatMeter End ///////
					// ////// VolumeCorrector Start ///////
				} else if (detailType.equals("VolumeCorrector")) {
					try {
						if (colName.equals("atmospherePressure")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setAtmospherePressure(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("basePressure")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setBasePressure(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("baseTemperature")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setBaseTemperature(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("batteryVoltage")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setBatteryVoltage(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("co2")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setCo2(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("compressFactor")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setCompressFactor(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("convertType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setConverterType(colValue.toString());
							}
						} else if (colName.equals("correctedUsageIndex")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setCorretedUsageIndex(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("correctUsageCount")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setCorretedUsageCount(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("currentPressure")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setCurrentPressure(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("currentTemperature")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setCurrentTemperature(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fixedFpv")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setFixedFpv(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fixedPressure")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setFixedPressure(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fixedTemperature")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setFixedTemperature(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gasHour")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setGasHour(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("gasRelativeDensity")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setGasRelativeDensity(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lowestLimitPressure")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLowestLimitPressure(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lowestLimptTemperature")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLowestLimitTemperature(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("meterFactor")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setMeterFactor(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("n2")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setN2(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("pipeLine")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setPipeLine(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("powerSupply")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setPowerSupply(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("pressureUnit")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setPressureUnit(colValue.toString());
							}
						} else if (colName.equals("pulseWeight")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setPulseWeight(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("siteName")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setSiteName(colValue.toString());
							}
						} else if (colName.equals("specificGravity")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setSpecificGravity(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("tag")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setTag(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("temperatureUnit")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setTemperatureUnit(colValue.toString());
							}
						} else if (colName.equals("uncorrectedusageCount")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setUncorrectedUsageCount(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("uncorrectedusageIndex")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setUncorretedUsageIndex(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("upperLimitPressure")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setUpperLimitPressure(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("upperLimitTemperature")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setUpperLimitTemperature(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("volumeUnit")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setVolumeUnit(colValue.toString());
							}
							// ////////////////////////////////////////////////공통
						} else if (colName.equals("installDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setInstallDate(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("Location")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLocation(locationDao.getLocationByName(colValue).get(0));
							} else {
								return null;
							}
						} else if (colName.equals("mdsId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Meter tmpMdsId = meterDao.get(colValue.toString());

								if (tmpMdsId != null) {
									isUpdate = true;
								}
								vcMeter.setMdsId(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("meterType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setMeterType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							} else {
								return null;
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
								if (supplier.getId() != null) {
									vcMeter.setSupplier(supplier);
								} else {
									logger.info("Not Exist");
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty() && vcMeter.getSupplier() != null) {
								List<DeviceModel> devices = deviceModelDao
										.getDeviceModelByName(vcMeter.getSupplier().getId(), colValue);
								vcMeter.setModel(devices.get(0));
							} else {
								return null;
							}
						} else if (colName.equals("modemPort")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setModemPort(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("prepaymentMeter")) {
							if (colValue.equals("1")) {
								vcMeter.setPrepaymentMeter(true);
							} else if (colValue.equals("0")) {
								vcMeter.setPrepaymentMeter(false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("Address")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setAddress(colValue.toString());
							}
						} else if (colName.equals("endDevice")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setEndDevice(endDeviceDao.get(Integer.parseInt(colValue.toString())));// EndDevice
																												// Id.
							}
						} else if (colName.equals("expirationDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setExpirationDate(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setHwVersion(colValue.toString());
							}
						} else if (colName.equals("ihdId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setIhdId(colValue.toString());
							}
						} else if (colName.equals("installedSiteImg")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setInstalledSiteImg(colValue.toString());
							}
						} else if (colName.equals("installProperty")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setInstallProperty(colValue.toString());
							}
						} else if (colName.equals("lastMeteringValue")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLastMeteringValue(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("lastReadDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLastReadDate(colValue.toString());
							}
						} else if (colName.equals("lastTimesyncDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLastTimesyncDate(colValue.toString());
							}
						} else if (colName.equals("lpInterval")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setLpInterval(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("meterCaution")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setMeterCaution(colValue.toString());
							}
						} else if (colName.equals("meterError")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setMeterError(colValue.toString());
							}
						} else if (colName.equals("meterStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setMeterStatus(codeDao.get(codeDao.getCodeIdByCode((colValue))));
							}
						} else if (colName.equals("modem")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setModem(modemDao.get(colValue));
							}
						} else if (colName.equals("pulseConstant")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setPulseConstant(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("qualifiedDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setQualifiedDate(colValue.toString());
							}
						} else if (colName.equals("swName")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setSwName(colValue.toString());
							}
						} else if (colName.equals("swUpdateDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setSwUpdateDate(colValue.toString());
							}
						} else if (colName.equals("swVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setSwVersion(colValue.toString());
							}
						} else if (colName.equals("timeDiff")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setTimeDiff(Long.parseLong(colValue.toString()));
							}
						} else if (colName.equals("usageThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setUsageThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("writeDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								vcMeter.setWriteDate(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
					// ////// VolumeCorrector End ///////
				} else {
				}
				// ///////////////////////////////////////////////// 미터타입별 객체 생성
				// END
			} // for end : Cell

			if (detailType.equals("EnergyMeter")) {
				if (StringUtil.nullToBlank(energyMeter.getInstallDate()).isEmpty()
						|| StringUtil.nullToBlank(energyMeter.getMdsId()).isEmpty()
						|| energyMeter.getMeterType() == null || energyMeter.getModel() == null
						|| energyMeter.getSupplier() == null) {
					return null;
				} else {
					map.put("Meter", energyMeter);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("WaterMeter")) {
				if (waterMeter.getCorrectPulse() == null || waterMeter.getCurrentPulse() == null
						|| StringUtil.nullToBlank(waterMeter.getInstallDate()).isEmpty()
						|| waterMeter.getLocation() == null || StringUtil.nullToBlank(waterMeter.getMdsId()).isEmpty()
						|| waterMeter.getMeterType() == null || waterMeter.getModel() == null
						|| waterMeter.getSupplier() == null) {
					return null;
				} else {
					map.put("Meter", waterMeter);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("GasMeter")) {
				if (gasMeter.getCorrectPulse() == null || gasMeter.getCurrentPulse() == null
						|| StringUtil.nullToBlank(gasMeter.getInstallDate()).isEmpty() || gasMeter.getLocation() == null
						|| StringUtil.nullToBlank(gasMeter.getMdsId()).isEmpty() || gasMeter.getMeterType() == null
						|| gasMeter.getModel() == null || gasMeter.getSupplier() == null) {
					return null;
				} else {
					map.put("Meter", gasMeter);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("HeatMeter")) {
				if (StringUtil.nullToBlank(heatMeter.getInstallDate()).isEmpty() || heatMeter.getLocation() == null
						|| StringUtil.nullToBlank(heatMeter.getMdsId()).isEmpty() || heatMeter.getMeterType() == null
						|| heatMeter.getModel() == null || heatMeter.getSupplier() == null) {
					return null;
				} else {
					map.put("Meter", heatMeter);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("VolumeCorrector")) {
				if (StringUtil.nullToBlank(vcMeter.getInstallDate()).isEmpty() || vcMeter.getLocation() == null
						|| StringUtil.nullToBlank(vcMeter.getMdsId()).isEmpty() || vcMeter.getMeterType() == null
						|| vcMeter.getModel() == null || vcMeter.getSupplier() == null) {
					return null;
				} else {
					map.put("Meter", vcMeter);
					map.put("isUpdate", isUpdate);
					return map;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Modem getModem(Row titles, Row row, int supplierId, String detailType) throws IOException {

		Map<String, Object> format = getFormat(DeviceType.Modem, ModemType.valueOf(detailType).name());
		Map<String, Object> required = (Map<String, Object>) format.get("required");
		Map<String, Object> unrequired = (Map<String, Object>) format.get("unrequired");

		Modem modem = new Modem();
		modem.setSupplier(supplierDao.get(supplierId));

		String colName = null;
		String colValue = null;
		Boolean colFlag = false;
		int chkCount = required.size();

		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal);
					} else {
						colValue = String.valueOf(doubleVal);
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue());
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula();
				break;

			default:
				colValue = "";
			}
			colValue = colValue.trim();

			// 필수 항목 체크
			if (required.containsValue(colName)) {
				if (colValue != null && !"".equals(colValue)) {
					chkCount--;

					// Modem 포맷과 업로드 된 엑셀과의 순서 및 항목명이 동일한 지 확인
					if (colName.equals(required.get(cell.getColumnIndex()))) {
						ModemEnum item = ModemEnum.valueOf(colName);
						modem = item.getModem(modem, colValue);
					} else {
						throw new IOException("=========== 포맷과 다름 !! ===========");
					}
				}
			} else {
				if ("".equals(colValue))
					continue;
				else if (!colFlag)
					colFlag = true;

				// Modem 포맷과 업로드 된 엑셀과의 순서 및 항목명이 동일한 지 확인
				if (colName.equals(unrequired.get(cell.getColumnIndex()))) {
					ModemEnum item = ModemEnum.valueOf(colName);
					modem = item.getModem(modem, colValue);
				} else {
					throw new IOException("=========== 포맷과 다름 !! ===========");
				}
			}

			if (colFlag) {
				if (chkCount > 0)
					throw new IOException(); // 필수항목 누락
			} else {
				if (chkCount == required.size())
					modem = null;
			}

		} // for end : Cell

		return modem;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> getAllModem(Row titles, Row row, int supplierId, String detailType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Map<String, Object> format = getFormat(DeviceType.Modem, ModemType.valueOf(detailType).name());
			Map<String, Object> required = (Map<String, Object>) format.get("required");
			Map<String, Object> unrequired = (Map<String, Object>) format.get("unrequired");

			Modem modem = new Modem();

			ZRU zru = new ZRU();
			ZMU zmu = new ZMU();
			ZEUPLS zeupls = new ZEUPLS();
			ZEUMBus zeumBus = new ZEUMBus();
			ZBRepeater zbrRepeater = new ZBRepeater();
			PLCIU plciu = new PLCIU();
			MMIU mmiu = new MMIU();
			IHD ihd = new IHD();
			IEIU ieiu = new IEIU();
			HMU hmu = new HMU();
			ACD acd = new ACD();
			Converter converter = new Converter();
			SubGiga subGiga = new SubGiga();

			String colName = null;
			String colValue = null;
			Boolean colFlag = false;
			Boolean isUpdate = false;
			int chkCount = required.size();

			for (Cell cell : row) {
				if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
					break;
				}
				colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					colValue = cell.getRichStringCellValue().getString();
					break;

				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						colValue = cell.getDateCellValue().toString();
					} else {
						Long roundVal = Math.round(cell.getNumericCellValue());
						Double doubleVal = cell.getNumericCellValue();
						if (doubleVal.equals(roundVal.doubleValue())) {
							colValue = String.valueOf(roundVal);
						} else {
							colValue = String.valueOf(doubleVal);
						}
					}
					break;

				case Cell.CELL_TYPE_BOOLEAN:
					colValue = String.valueOf(cell.getBooleanCellValue());
					break;

				case Cell.CELL_TYPE_FORMULA:
					colValue = cell.getCellFormula();
					break;

				default:
					colValue = "";
				}
				colValue = colValue.trim();
				// /////////////////////////////////////////////// 모뎀타입별 객체 생성
				if (detailType.equals("ZRU")) {
					try {
						if (colName.equals("channelId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("lpChoice")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setLpChoice(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("manualEnable")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1") || colValue.equals("0")) {
									zru.setManualEnable(colValue.equals("1") ? true : false);
								} else {
									return null;
								}
							}
						} else if (colName.equals("meteringDay")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setMeteringDay(colValue.toString());
							}
						} else if (colName.equals("meteringHour")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setMeteringHour(colValue.toString());
							}
						} else if (colName.equals("needJoinSet")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1") || colValue.equals("0")) {
									zru.setNeedJoinSet(colValue.equals("1") ? true : false);
								} else {
									return null;
								}
							}
						} else if (colName.equals("networkKey")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("panId")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1") || colValue.equals("0")) {
									zru.setSecurityEnable(colValue.equals("1") ? true : false);
								} else {
									return null;
								}
							}
						} else if (colName.equals("testFlag")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (colValue.equals("1") || colValue.equals("0")) {
									zru.setTestFlag(colValue.equals("1") ? true : false);
								} else {
									return null;
								}
							}
							// ////////////공통////////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								zru.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setInstallDate(colValue.toString());
							} else {
								return null;
							}
						} else if (colName.equals("lpPeriod")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setLpPeriod(Integer.parseInt(colValue.toString()));
							} else {
								return null;
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									zru.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setNodeType(Integer.parseInt(colValue.toString()));
							} else {
								return null;
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									zru.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setLastLinkTime(colValue);
							}
						} else if (colName.equals("lastResetCode")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									zru.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									zru.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									zru.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setRfPower(Long.parseLong(colValue.toString()));
								// zru.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								zru.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("ZMU")) {
					try {
						if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zmu.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("needJoinSet")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zmu.setNeedJoinSet(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zmu.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
							// /////////////공통//////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								zmu.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zmu.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zmu.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									zmu.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zmu.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									zmu.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									zmu.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									zmu.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									zmu.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setRfPower(Long.parseLong(colValue.toString()));
								// zmu.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zmu.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("ZEUPLS")) {
					try {
						if (colName.equals("activeTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setActiveTime(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("alarmFlag")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setAlarmFlag(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("alarmMask")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setAlarmMask(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("autoTrapFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeupls.setAutoTrapFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("batteryCapacity")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setBatteryCapacity(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("batteryStatus")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setBatteryStatus(colValue.toString());
							}
						} else if (colName.equals("batteryVolt")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setBatteryVolt(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("lpChoice")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setLpChoice(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("LQI")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setLQI(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeupls.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("meteringDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setMeteringDay(colValue.toString());
							}
						} else if (colName.equals("meteringHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setMeteringHour(colValue.toString());
							}
						} else if (colName.equals("needJoinSet")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeupls.setNeedJoinSet(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("networkType")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setNetworkType(colValue.toString());
							}
						} else if (colName.equals("operationDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setOperatingDay(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("permitMode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setPermitMode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("permitState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setPermitState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("powerType")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setPowerType(colValue.toString());
							}
						} else if (colName.equals("resetReason")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setResetReason(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rssi")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setRssi(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeupls.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("solarADV")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setSolarADV(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("solarBDCV")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setSolarBDCV(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("solarChgBV")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setSolarChgBV(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("testFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeupls.setTestFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("trapDate")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setTrapDate(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("trapHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setTrapHour(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("trapMinute")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setTrapMinute(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("trapSecond")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setTrapSecond(Integer.parseInt(colValue.toString()));
							}
							// //////////////공통///////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								zeupls.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zeupls.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zeupls.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									zeupls.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zeupls.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									zeupls.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									zeupls.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									zeupls.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									zeupls.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setRfPower(Long.parseLong(colValue.toString()));
								// zeupls.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeupls.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("ZEUMBus")) {
					try {
						if (colName.equals("armFwBuild")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setArmFwBuild(colValue.toString());
							}
						} else if (colName.equals("armFwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setArmFwVer(colValue.toString());
							}
						} else if (colName.equals("armHwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setArmHwVer(colValue.toString());
							}
						} else if (colName.equals("armModel")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setArmModel(colValue.toString());
							}
						} else if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeumBus.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("meteringDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setMeteringDay(colValue.toString());
							}
						} else if (colName.equals("meteringHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setMeteringHour(colValue.toString());
							}
						} else if (colName.equals("needJoinSet")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeumBus.setNeedJoinSet(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeumBus.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("testFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zeumBus.setTestFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
							// /////////////////공통/////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								zeumBus.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zeumBus.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zeumBus.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									zeumBus.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zeumBus.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									zeumBus.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									zeumBus.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									zeumBus.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									zeumBus.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setRfPower(Long.parseLong(colValue.toString()));
								// zeumBus.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zeumBus.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}

				} else if (detailType.equals("ZBRepeater")) {
					try {
						if (colName.equals("activeTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setActiveTime(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("batteryCapacity")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setBatteryCapacity(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("batteryStatus")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (BatteryStatus.valueOf(colValue) != null) {
									zbrRepeater.setBatteryStatus(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("batteryVolt")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setBatteryVolt(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("lpChoice")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setLpChoice(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zbrRepeater.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("meteringDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setMeteringDay(colValue.toString());
							}
						} else if (colName.equals("meteringHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setMeteringHour(colValue.toString());
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("networkType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (ModemNetworkType.valueOf(colValue) != null) {
									zbrRepeater.setNetworkType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("operationDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setOperatingDay(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("powerType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (ModemPowerType.valueOf(colValue) != null) {
									zbrRepeater.setPowerType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("repeatingDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setRepeatingDay(colValue.toString());
							}
						} else if (colName.equals("repeatingHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setRepeatingHour(colValue.toString());
							}
						} else if (colName.equals("repeatingSetupSec")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setRepeatingSetupSec(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zbrRepeater.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("solarADV")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setSolarADV(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("solarBDCV")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setSolarBDCV(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("testFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								zbrRepeater.setTestFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
							// //////////////////공통//////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								zbrRepeater.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zbrRepeater.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zbrRepeater.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									zbrRepeater.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								zbrRepeater.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									zbrRepeater.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setMacAddr(colValue);
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									zbrRepeater.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									zbrRepeater.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									zbrRepeater.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setRfPower(Long.parseLong(colValue.toString()));
								// zbrRepeater.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								zbrRepeater.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("PLCIU")) {
					try {
						if (colName.equals("sysContact")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysContact(colValue.toString());
							}
						} else if (colName.equals("sysDescr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysDescr(colValue.toString());
							}
						} else if (colName.equals("sysFactoryReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysFactoryReset(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysFwVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysFwVersion(colValue.toString());
							}
						} else if (colName.equals("sysIpAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysIpAddr(colValue.toString());
							}
						} else if (colName.equals("sysLocation")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysLocation(colValue.toString());
							}
						} else if (colName.equals("sysName")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysName(colValue.toString());
							}
						} else if (colName.equals("sysNodeType")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysObjectId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysObjectId(colValue.toString());
							}
						} else if (colName.equals("sysPort")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysPort(colValue.toString());
							}
						} else if (colName.equals("sysReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysReset(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysRtsCtsEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								plciu.setSysRtsCtsEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("sysSerialParityType")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysSerialParityType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysSerialRate")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysSerialRate(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysSerialStopBit")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysSerialStopBit(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysSerialWordBit")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysSerialWordBit(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysService")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysService(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysStatus")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysStatus(colValue.toString());
							}
						} else if (colName.equals("sysUseDhcp")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSysUseDhcp(Integer.parseInt(colValue.toString()));
							}
							// ////////////////////공통/////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								plciu.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								plciu.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								plciu.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									plciu.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								plciu.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									plciu.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									plciu.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									plciu.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									plciu.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setRfPower(Long.parseLong(colValue.toString()));
								// plciu.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								plciu.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("MMIU")) {
					try {
						if (colName.equals("phoneNumber")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								mmiu.setPhoneNumber(colValue.toString());
							}
						} else if (colName.equals("errorStatus")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setErrorStatus(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("simNumber")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setSimNumber(colValue.toString());
							}
							// ///////////////공통/////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								mmiu.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								mmiu.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								mmiu.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									mmiu.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								mmiu.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									mmiu.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									mmiu.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									mmiu.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									mmiu.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setRfPower(Long.parseLong(colValue.toString()));
								// mmiu.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								mmiu.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("IHD")) {
					try {
						if (colName.equals("billDate")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setBillDate(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("gasThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setGasThreshold(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								ihd.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("needJoinSet")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								ihd.setNeedJoinSet(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("peakDemandThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setPeakDemandThreshold(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								ihd.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("testFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								ihd.setTestFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("waterThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setWaterThreshold(Integer.parseInt(colValue.toString()));
							}
							// ////////////////공통/////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								ihd.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ihd.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ihd.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									ihd.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ihd.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									ihd.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									ihd.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									ihd.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									ihd.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setRfPower(Long.parseLong(colValue.toString()));
								// ihd.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ihd.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("IEIU")) {
					try {
						if (colName.equals("phoneNumber")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ieiu.setPhoneNumber(colValue.toString());
							}
						} else if (colName.equals("errorStatus")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setErrorStatus(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("groupNumber")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setGroupNumber(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("memberNumber")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setMemberNumber(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("simNumber")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setSimNumber(colValue.toString());
							}
							// /////////////공통////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								ieiu.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ieiu.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ieiu.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									ieiu.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								ieiu.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									ieiu.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									ieiu.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									ieiu.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									ieiu.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setRfPower(Long.parseLong(colValue.toString()));
								// ieiu.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								ieiu.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("HMU")) {
					try {
						if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("lpChoice")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setLpChoice(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								hmu.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("meteringDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setMeteringDay(colValue.toString());
							}
						} else if (colName.equals("meteringHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setMeteringHour(colValue.toString());
							}
						} else if (colName.equals("needJoinSet")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								hmu.setNeedJoinSet(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								hmu.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("testFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								hmu.setTestFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
							// //////////////////공통///////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								hmu.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								hmu.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								hmu.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									hmu.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								hmu.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									hmu.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									hmu.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									hmu.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									hmu.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setRfPower(Long.parseLong(colValue.toString()));
								// hmu.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								hmu.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("ACD")) {
					try {
						if (colName.equals("channelId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setChannelId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("extPanId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setExtPanId(colValue.toString());
							}
						} else if (colName.equals("fixedReset")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setFixedReset(colValue.toString());
							}
						} else if (colName.equals("linkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setLinkKey(colValue.toString());
							}
						} else if (colName.equals("lpChoice")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setLpChoice(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("manualEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								acd.setManualEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("meteringDay")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setMeteringDay(colValue.toString());
							}
						} else if (colName.equals("meteringHour")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setMeteringHour(colValue.toString());
							}
						} else if (colName.equals("needJoinSet")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								acd.setNeedJoinSet(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("networkKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setNetworkKey(colValue.toString());
							}
						} else if (colName.equals("panId")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setPanId(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("securityEnable")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								acd.setSecurityEnable(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
						} else if (colName.equals("testFlag")) {
							if (colValue.equals("1") || colValue.equals("0")) {
								acd.setTestFlag(colValue.equals("1") ? true : false);
							} else if (colValue == null || colValue.length() == 0) {
							} else {
								return null;
							}
							// /////////////공통///////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								acd.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								acd.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								acd.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									acd.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								acd.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									acd.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									acd.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									acd.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									acd.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setRfPower(Long.parseLong(colValue.toString()));
								// acd.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								acd.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				} else if (detailType.equals("Converter")) {
					try {
						if (colName.equals("sysPort")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setSysPort(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("sysName")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setSysName(colValue.toString());
							}
							// /////////////공통///////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								converter.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								converter.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								converter.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									converter.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								converter.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									converter.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setLastLinkTime(colValue.toString());
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									converter.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									converter.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									converter.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setRfPower(Long.parseLong(colValue.toString()));
								// converter.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								converter.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						logger.error(e,e);
						return null;
					}
				} else if (detailType.equals("SubGiga")) {
					try {

						if (colName.equals("baseStationAddress")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setBaseStationAddress(colValue.toString());
							}
						} else if (colName.equals("ipv6Address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setIpv6Address(colValue.toString());
							}
						} else if (colName.equals("securityKey")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setSecurityKey(colValue.toString());
							}
						} else if (colName.equals("hopsToBaseStation")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setHopsToBaseStation(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("frequency")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setFrequency(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("bandWidth")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setBandWidth(Integer.parseInt(colValue.toString()));
							}
							// ////////////공통////////////////////
						} else if (colName.equals("deviceSerial")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Modem tmpModem = modemDao.get(colValue);
								if (tmpModem != null) {
									isUpdate = true;
								}
								subGiga.setDeviceSerial(colValue);
							} else {
								return null;
							}
						} else if (colName.equals("installDate")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								subGiga.setInstallDate(colValue.toString());
							}
						} else if (colName.equals("lpPeriod")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								subGiga.setLpPeriod(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("modemType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								ModemType tmpModemType = ModemType.valueOf(colValue);
								if (tmpModemType != null) {
									subGiga.setModemType(colValue);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("nodeType")) {
							if (colValue == null || colValue.length() == 0) {
								throw new Exception("null Error");
							} else {
								subGiga.setNodeType(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("supplier")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								Supplier supplier = supplierDao.getSupplierByName(colValue);
								if (supplier != null && supplier.getId() != null) {
									subGiga.setSupplier(supplier);
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else if (colName.equals("address")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setAddress(colValue.toString());
							}
						} else if (colName.equals("commState")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setCommState(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("currentThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setCurrentThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("fwRevision")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setFwRevision(colValue.toString());
							}
						} else if (colName.equals("fwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setFwVer(colValue.toString());
							}
						} else if (colName.equals("gpioX")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setGpioX(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioY")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setGpioY(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("gpioZ")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setGpioZ(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("hwVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setHwVer(colValue.toString());
							}
						} else if (colName.equals("ipAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setIpAddr(colValue.toString());
							}
						} else if (colName.equals("lastLinkTime")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setLastLinkTime(colValue);
							}
						} else if (colName.equals("lastResetCode")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setLastResetCode(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("macAddr")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setMacAddr(colValue.toString());
							}
						} else if (colName.equals("mcu")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								MCU tmpMcu = mcuDao.get(colValue);
								if (tmpMcu != null) {
									subGiga.setMcu(tmpMcu);
								} else {
									return null;
								}
							}
						} else if (colName.equals("model")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								List<DeviceModel> modelList = deviceModelDao.getDeviceModelByName(supplierId, colValue);
								if (modelList != null && modelList.size() > 0) {
									subGiga.setModel(modelList.get(0));
								} else {
									return null;
								}
							}
						} else if (colName.equals("nodeKind")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setNodeKind(colValue.toString());
							}
						} else if (colName.equals("powerThreshold")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setPowerThreshold(Double.parseDouble(colValue.toString()));
							}
						} else if (colName.equals("protocolType")) {
							if (!StringUtil.nullToBlank(colValue).isEmpty()) {
								if (Protocol.valueOf(colValue) != null) {
									subGiga.setProtocolType(colValue);
								} else {
									return null;
								}
							}
						} else if (colName.equals("protocolVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setProtocolVersion(colValue.toString());
							}
						} else if (colName.equals("resetCount")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setResetCount(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("rfPower")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setRfPower(Long.parseLong(colValue.toString()));
								// subGiga.setRfPower(Integer.parseInt(colValue.toString()));
							}
						} else if (colName.equals("swVer")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setSwVer(colValue.toString());
							}
						} else if (colName.equals("zdzdIfVersion")) {
							if (colValue == null || colValue.length() == 0) {
							} else {
								subGiga.setZdzdIfVersion(colValue.toString());
							}
						}
					} catch (Exception e) {
						return null;
					}
				}

			} // for end : Cell

			if (detailType.equals("ZRU")) {
				if (StringUtil.nullToBlank(zru.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(zru.getInstallDate()).isEmpty() || zru.getLpPeriod() == null
						|| zru.getModemType() == null || zru.getNodeType() == null || zru.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", zru);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("ZMU")) {
				if (StringUtil.nullToBlank(zmu.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(zmu.getInstallDate()).isEmpty() || zmu.getLpPeriod() == null
						|| zmu.getModemType() == null || zmu.getNodeType() == null || zmu.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", zmu);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("ZEUPLS")) {
				if (StringUtil.nullToBlank(zeupls.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(zeupls.getInstallDate()).isEmpty() || zeupls.getLpPeriod() == null
						|| zeupls.getModemType() == null || zeupls.getNodeType() == null
						|| zeupls.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", zeupls);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("ZEUMBus")) {
				if (StringUtil.nullToBlank(zeumBus.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(zeumBus.getInstallDate()).isEmpty() || zeumBus.getLpPeriod() == null
						|| zeumBus.getModemType() == null || zeumBus.getNodeType() == null
						|| zeumBus.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", zeumBus);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("ZBRepeater")) {
				if (StringUtil.nullToBlank(zbrRepeater.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(zbrRepeater.getInstallDate()).isEmpty()
						|| zbrRepeater.getLpPeriod() == null || zbrRepeater.getModemType() == null
						|| zbrRepeater.getNodeType() == null || zbrRepeater.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", zbrRepeater);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("PLCIU")) {
				if (StringUtil.nullToBlank(plciu.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(plciu.getInstallDate()).isEmpty() || plciu.getLpPeriod() == null
						|| plciu.getModemType() == null || plciu.getNodeType() == null || plciu.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", plciu);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("MMIU")) {
				if (StringUtil.nullToBlank(mmiu.getPhoneNumber()).isEmpty()
						|| StringUtil.nullToBlank(mmiu.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(mmiu.getInstallDate()).isEmpty() || mmiu.getLpPeriod() == null
						|| mmiu.getModemType() == null || mmiu.getNodeType() == null || mmiu.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", mmiu);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("IHD")) {
				if (StringUtil.nullToBlank(ihd.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(ihd.getInstallDate()).isEmpty() || ihd.getLpPeriod() == null
						|| ihd.getModemType() == null || ihd.getNodeType() == null || ihd.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", ihd);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("IEIU")) {
				if (StringUtil.nullToBlank(ieiu.getPhoneNumber()).isEmpty()
						|| StringUtil.nullToBlank(ieiu.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(ieiu.getInstallDate()).isEmpty() || ieiu.getLpPeriod() == null
						|| ieiu.getModemType() == null || ieiu.getNodeType() == null || ieiu.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", ieiu);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("HMU")) {
				if (StringUtil.nullToBlank(hmu.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(hmu.getInstallDate()).isEmpty() || hmu.getLpPeriod() == null
						|| hmu.getModemType() == null || hmu.getNodeType() == null || hmu.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", hmu);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("ACD")) {
				if (StringUtil.nullToBlank(acd.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(acd.getInstallDate()).isEmpty() || acd.getLpPeriod() == null
						|| acd.getModemType() == null || acd.getNodeType() == null || acd.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", acd);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("Converter")) {
				if (StringUtil.nullToBlank(converter.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(converter.getInstallDate()).isEmpty()
						|| converter.getLpPeriod() == null || converter.getModemType() == null
						|| converter.getNodeType() == null || converter.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", converter);
					map.put("isUpdate", isUpdate);
					return map;
				}
			} else if (detailType.equals("SubGiga")) {
				if (StringUtil.nullToBlank(subGiga.getDeviceSerial()).isEmpty()
						|| StringUtil.nullToBlank(subGiga.getInstallDate()).isEmpty() || subGiga.getLpPeriod() == null
						|| subGiga.getModemType() == null || subGiga.getNodeType() == null
						|| subGiga.getSupplier() == null) {
					return null;
				} else {
					map.put("Modem", subGiga);
					map.put("isUpdate", isUpdate);
					return map;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public Object insertDevice(Object obj, String fileType, String detailType) {

		Object result = null;
		ResultStatus insertResult = ResultStatus.SUCCESS;

		Map<String, Object> logData = new HashMap<String, Object>();
		try {
			if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
				try {
					result = mcuDao.mcuAdd((MCU) obj);
					mcuDao.flushAndClear();
				} catch (Exception e) {
					insertResult = ResultStatus.FAIL;
				} finally {
					MCU mcu = (MCU) obj;
					// 로그 저장
					logData.put("deviceType", TargetClass.DCU);
					logData.put("deviceName", mcu.getSysID());
					logData.put("deviceModel", mcu.getDeviceModel());
					logData.put("resultStatus", insertResult);
					logData.put("regType", RegType.Bulk);
					logData.put("supplier", mcu.getSupplier());

					insertDeviceRegLog(logData);
				}
			} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
				if (detailType.equals("EnergyMeter")) {
					try {
						result = energyMeterDao.meterAdd((EnergyMeter) obj);
						energyMeterDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						EnergyMeter energyMeter = (EnergyMeter) obj;

						// 로그 저장
						logData.put("deviceType", TargetClass.EnergyMeter);
						logData.put("deviceName", energyMeter.getMdsId());
						logData.put("deviceModel", energyMeter.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", energyMeter.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("WaterMeter")) {
					try {
						result = waterMeterDao.meterAdd((WaterMeter) obj);
						waterMeterDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						WaterMeter waterMeter = (WaterMeter) obj;

						// 로그 저장
						logData.put("deviceType", TargetClass.WaterMeter);
						logData.put("deviceName", waterMeter.getMdsId());
						logData.put("deviceModel", waterMeter.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", waterMeter.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("GasMeter")) {
					try {
						result = gasMeterDao.meterAdd((GasMeter) obj);
						gasMeterDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						GasMeter gasMeter = (GasMeter) obj;

						// 로그 저장
						logData.put("deviceType", TargetClass.GasMeter);
						logData.put("deviceName", gasMeter.getMdsId());
						logData.put("deviceModel", gasMeter.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", gasMeter.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("HeatMeter")) {
					try {
						result = heatMeterDao.meterAdd((HeatMeter) obj);
						heatMeterDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						HeatMeter heatMeter = (HeatMeter) obj;

						// 로그 저장
						logData.put("deviceType", TargetClass.HeatMeter);
						logData.put("deviceName", heatMeter.getMdsId());
						logData.put("deviceModel", heatMeter.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", heatMeter.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("VolumeCorrector")) {
					try {
						result = volumeCorrectorDao.meterAdd((VolumeCorrector) obj);
						volumeCorrectorDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						VolumeCorrector volumeCorrector = (VolumeCorrector) obj;

						// 로그 저장
						logData.put("deviceType", TargetClass.VolumeCorrector);
						logData.put("deviceName", volumeCorrector.getMdsId());
						logData.put("deviceModel", volumeCorrector.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", volumeCorrector.getSupplier());

						insertDeviceRegLog(logData);
					}
				}
				// result = meterDao.meterAdd((Meter)obj);
			} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
				// Unknown(0), ZRU(1), ZMU(2), MMIU(11), IEIU(13), PLCIU(18),
				// ZEUPLS(5), ZEUMBus(14), ZBRepeater(98), IHD(15),
				// ACD(16), HMU(17), Converter(19)
				if (detailType.equals("ZRU")) {
					try {
						result = zRUDao.modemAdd((ZRU) obj);
						zRUDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						ZRU zru = (ZRU) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.ZRU);
						logData.put("deviceName", zru.getDeviceSerial());
						logData.put("deviceModel", zru.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", zru.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("ZMU")) {
					try {
						result = zMUDao.modemAdd((ZMU) obj);
						zMUDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						ZMU zmu = (ZMU) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.ZMU);
						logData.put("deviceName", zmu.getDeviceSerial());
						logData.put("deviceModel", zmu.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", zmu.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("ZEUPLS")) {
					try {
						result = zEUPLSDao.modemAdd((ZEUPLS) obj);
						zEUPLSDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						ZEUPLS zeupls = (ZEUPLS) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.ZEUPLS);
						logData.put("deviceName", zeupls.getDeviceSerial());
						logData.put("deviceModel", zeupls.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", zeupls.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("MMIU")) {
					try {
						result = mMIUDao.modemAdd((MMIU) obj);
						mMIUDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						MMIU mmiu = (MMIU) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.MMIU);
						logData.put("deviceName", mmiu.getDeviceSerial());
						logData.put("deviceModel", mmiu.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", mmiu.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("IEIU")) {
					try {
						result = iEIUDao.modemAdd((IEIU) obj);
						iEIUDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						IEIU ieiu = (IEIU) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.IEIU);
						logData.put("deviceName", ieiu.getDeviceSerial());
						logData.put("deviceModel", ieiu.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", ieiu.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("ZEUMBus")) {
					try {
						result = zEUMBusDao.modemAdd((ZEUMBus) obj);
						zEUMBusDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						ZEUMBus zeumBus = (ZEUMBus) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.ZEUMBus);
						logData.put("deviceName", zeumBus.getDeviceSerial());
						logData.put("deviceModel", zeumBus.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", zeumBus.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("IHD")) {
					try {
						result = iHDDao.modemAdd((IHD) obj);
						iHDDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						IHD ihd = (IHD) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.IHD);
						logData.put("deviceName", ihd.getDeviceSerial());
						logData.put("deviceModel", ihd.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", ihd.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("ACD")) {
					try {
						result = aCDDao.modemAdd((ACD) obj);
						aCDDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						ACD acd = (ACD) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.ACD);
						logData.put("deviceName", acd.getDeviceSerial());
						logData.put("deviceModel", acd.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", acd.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("HMU")) {
					try {
						result = hMUDao.modemAdd((HMU) obj);
						hMUDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						HMU hmu = (HMU) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.HMU);
						logData.put("deviceName", hmu.getDeviceSerial());
						logData.put("deviceModel", hmu.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", hmu.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("PLCIU")) {
					try {
						result = pLCIUDao.modemAdd((PLCIU) obj);
						pLCIUDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						PLCIU plciu = (PLCIU) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.PLC);
						logData.put("deviceName", plciu.getDeviceSerial());
						logData.put("deviceModel", plciu.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", plciu.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("ZBRepeater")) {
					try {
						result = zBRepeaterDao.modemAdd((ZBRepeater) obj);
						zBRepeaterDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						ZBRepeater zbRepeater = (ZBRepeater) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.ZBRepeater);
						logData.put("deviceName", zbRepeater.getDeviceSerial());
						logData.put("deviceModel", zbRepeater.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", zbRepeater.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("Converter")) {
					try {
						result = converterDao.modemAdd((Converter) obj);
						converterDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						Converter converter = (Converter) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.Converter);
						logData.put("deviceName", converter.getDeviceSerial());
						logData.put("deviceModel", converter.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", converter.getSupplier());

						insertDeviceRegLog(logData);
					}
				} else if (detailType.equals("SubGiga")) {
					try {
						result = subGigaDao.modemAdd((SubGiga) obj);
						subGigaDao.flushAndClear();
					} catch (Exception e) {
						insertResult = ResultStatus.FAIL;
					} finally {
						SubGiga subGiga = (SubGiga) obj;

						// 로그저장
						logData.put("deviceType", TargetClass.SubGiga);
						logData.put("deviceName", subGiga.getDeviceSerial());
						logData.put("deviceModel", subGiga.getModel());
						logData.put("resultStatus", insertResult);
						logData.put("regType", RegType.Bulk);
						logData.put("supplier", subGiga.getSupplier());

						insertDeviceRegLog(logData);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}

		return result;
	}

	/**
	 * method name : updateDevice<b/> method Desc : Device Bulk 등록 시 기존 데이터가 있으면
	 * Update 한다.
	 * 
	 * @param objList
	 * @param fileType
	 * @param detailType
	 * @return
	 */
	@Transactional
	public void updateDevice(List<Object> objList, String fileType, String detailType) {

		// objList : [{GS1 Code=gs1 value, SW Ver.=swVer value, Type=1.1.1.7,
		// ICC ID=iccId value, Model=model value, IMSI=imsi value, IMEI=imei
		// value, Production Date=20161111, HW Ver.=hwVer value, PO=po value,
		// EUI ID=TEST01_mcu}]
		// obj : {GS1 Code=gs1 value, SW Ver.=swVer value, Type=1.1.1.7, ICC
		// ID=iccId value, Model=model value, IMSI=imsi value, IMEI=imei value,
		// Production Date=20161111, HW Ver.=hwVer value, PO=po value, EUI
		// ID=TEST01_mcu}

		// MCU, Modem, Meter

		for (Object obj : objList) {
			try {
				if (fileType.equals(CommonConstants.DeviceType.MCU.name())) {
					try {
						MCU updMCU = mcuDao.get(((MCU) obj).getSysID());
						mergeExcelObject((MCU) obj, updMCU);
						mcuDao.groupUpdate(updMCU);
					} catch (Exception e) {
						logger.error(e.toString(), e);
					}
				} else if (fileType.equals(CommonConstants.DeviceType.Meter.name())) {
					if (detailType.equals("EnergyMeter")) {
						try {
							EnergyMeter updEnergyMeter = energyMeterDao.findByCondition("mdsId",
									((EnergyMeter) obj).getMdsId());
							mergeExcelObject((EnergyMeter) obj, updEnergyMeter);
							energyMeterDao.groupUpdate(updEnergyMeter);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("WaterMeter")) {
						try {
							WaterMeter updWaterMeter = waterMeterDao.findByCondition("mdsId",
									((WaterMeter) obj).getMdsId());
							mergeExcelObject((WaterMeter) obj, updWaterMeter);
							waterMeterDao.groupUpdate(updWaterMeter);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("GasMeter")) {
						try {
							GasMeter updGasMeter = gasMeterDao.findByCondition("mdsId", ((GasMeter) obj).getMdsId());
							mergeExcelObject((GasMeter) obj, updGasMeter);
							gasMeterDao.groupUpdate(updGasMeter);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("HeatMeter")) {
						try {
							HeatMeter updHeatMeter = heatMeterDao.findByCondition("mdsId",
									((HeatMeter) obj).getMdsId());
							mergeExcelObject((HeatMeter) obj, updHeatMeter);
							heatMeterDao.groupUpdate(updHeatMeter);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("VolumeCorrector")) {
						try {
							VolumeCorrector updVcMeter = volumeCorrectorDao.findByCondition("mdsId",
									((VolumeCorrector) obj).getMdsId());
							mergeExcelObject((VolumeCorrector) obj, updVcMeter);
							volumeCorrectorDao.groupUpdate(updVcMeter);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					}
				} else if (fileType.equals(CommonConstants.DeviceType.Modem.name())) {
					// Unknown(0), ZRU(1), ZMU(2), MMIU(11), IEIU(13),
					// PLCIU(18), ZEUPLS(5), ZEUMBus(14), ZBRepeater(98),
					// IHD(15), ACD(16), HMU(17), Converter(19)
					if (detailType.equals("ZRU")) {
						try {
							ZRU updZRU = zRUDao.get(((ZRU) obj).getDeviceSerial());
							mergeExcelObject((ZRU) obj, updZRU);
							zRUDao.groupUpdate(updZRU);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("ZMU")) {
						try {
							ZMU updZMU = zMUDao.get(((ZMU) obj).getDeviceSerial());
							mergeExcelObject((ZMU) obj, updZMU);
							zMUDao.groupUpdate(updZMU);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("ZEUPLS")) {
						try {
							ZEUPLS updZEUPLS = zEUPLSDao.get(((ZEUPLS) obj).getDeviceSerial());
							mergeExcelObject((ZEUPLS) obj, updZEUPLS);
							zEUPLSDao.groupUpdate(updZEUPLS);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("MMIU")) {
						try {
							MMIU updMMIU = mMIUDao.findByCondition("deviceSerial", ((MMIU) obj).getDeviceSerial());
							mergeExcelObject((MMIU) obj, updMMIU);
							mMIUDao.groupUpdate(updMMIU);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("IEIU")) {
						try {
							IEIU updIEIU = iEIUDao.findByCondition("deviceSerial", ((IEIU) obj).getDeviceSerial());
							mergeExcelObject((IEIU) obj, updIEIU);
							iEIUDao.groupUpdate(updIEIU);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("ZEUMBus")) {
						try {
							ZEUMBus updZEUMBus = zEUMBusDao.get(((ZEUMBus) obj).getDeviceSerial());
							mergeExcelObject((ZEUMBus) obj, updZEUMBus);
							zEUMBusDao.groupUpdate(updZEUMBus);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("IHD")) {
						try {
							IHD updIHD = iHDDao.findByCondition("deviceSerial", ((IHD) obj).getDeviceSerial());
							mergeExcelObject((IHD) obj, updIHD);
							iHDDao.groupUpdate(updIHD);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("ACD")) {
						try {
							ACD updACD = aCDDao.findByCondition("deviceSerial", ((ACD) obj).getDeviceSerial());
							mergeExcelObject((ACD) obj, updACD);
							aCDDao.groupUpdate(updACD);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("HMU")) {
						try {
							HMU updHMU = hMUDao.findByCondition("deviceSerial", ((HMU) obj).getDeviceSerial());
							mergeExcelObject((HMU) obj, updHMU);
							hMUDao.groupUpdate(updHMU);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("PLCIU")) {
						try {
							PLCIU updPLCIU = pLCIUDao.findByCondition("deviceSerial", ((PLCIU) obj).getDeviceSerial());
							mergeExcelObject((PLCIU) obj, updPLCIU);
							pLCIUDao.groupUpdate(updPLCIU);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("ZBRepeater")) {
						try {
							ZBRepeater updZBRepeater = zBRepeaterDao.get(((ZBRepeater) obj).getDeviceSerial());
							mergeExcelObject((ZBRepeater) obj, updZBRepeater);
							zBRepeaterDao.groupUpdate(updZBRepeater);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("Converter")) {
						try {
							Converter updConverter = converterDao.get(((Converter) obj).getDeviceSerial());
							mergeExcelObject((Converter) obj, updConverter);
							converterDao.groupUpdate(updConverter);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					} else if (detailType.equals("SubGiga")) {
						try {
							SubGiga updSubGiga = subGigaDao.get(((SubGiga) obj).getDeviceSerial());
							mergeExcelObject((SubGiga) obj, updSubGiga);
							subGigaDao.groupUpdate(updSubGiga);
						} catch (Exception e) {
							logger.error(e.toString(), e);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e,e);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertDeviceRegLog(Map<String, Object> insertData) {
		// DeviceLog 객체 생성
		DeviceRegLog deviceRegLog = new DeviceRegLog();

		try {
			deviceRegLog.setCreateDate(TimeUtil.getCurrentTime());
		} catch (ParseException e) {
			logger.error(e,e);
		}
		deviceRegLog.setDeviceType((TargetClass) insertData.get("deviceType"));
		deviceRegLog.setDeviceName((String) insertData.get("deviceName"));
		deviceRegLog.setDeviceModel((DeviceModel) insertData.get("deviceModel"));
		deviceRegLog.setResult((ResultStatus) insertData.get("resultStatus"));
		deviceRegLog.setRegType((RegType) insertData.get("regType"));
		if ((Supplier) insertData.get("supplier") != null) {
			deviceRegLog.setSupplier((Supplier) insertData.get("supplier"));
		}

		// DeviceLog 등록
		deviceRegistrationDao.add(deviceRegLog);
		deviceRegistrationDao.flushAndClear();
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW)
	public void insertShipmentImportLog(Map<String, Object> insertData) {
		try {
			DeviceRegLog deviceRegLog = new DeviceRegLog();

			deviceRegLog.setRegType((RegType) insertData.get("regType"));
			deviceRegLog.setDeviceType((TargetClass) insertData.get("deviceType"));
			deviceRegLog.setShipmentFileName((String) insertData.get("fileName"));
			deviceRegLog.setTotalCount((String) insertData.get("totalCount"));
			deviceRegLog.setSuccessCount((String) insertData.get("successCount"));
			deviceRegLog.setFailCount((String) insertData.get("failCount"));
			deviceRegLog.setCreateDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			if ((Supplier) insertData.get("supplier") != null) {
				deviceRegLog.setSupplier((Supplier) insertData.get("supplier"));
			}

			deviceRegistrationDao.add(deviceRegLog);
			deviceRegistrationDao.flushAndClear();

			logger.info("Save Complete - Shipment Import Log");
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	private String boolCheck(String val) {
		String result = "Success";
		if (val.equals("1") || val.equals("0")) {
		} else {
			result = "Failure";
		}

		return result;
	}

	private String doubleCheck(String val) {
		String result = "Success";
		try {
			Double.parseDouble(val);
		} catch (Exception e) {
			result = "Failure";
		}

		return result;
	}

	private String intCheck(String val) {
		String result = "Success";
		try {
			Integer.parseInt(val);
		} catch (Exception e) {
			result = "Failure";
		}

		return result;
	}

	private String longCheck(String val) {
		String result = "Success";
		try {
			Long.parseLong(val);
		} catch (Exception e) {
			result = "Failure";
		}

		return result;
	}

	/**
	 * method name : mergeExcelObject<b/> method Desc : Device(Meter, Modem,
	 * MCU) Bulk 등록에서 Update 시 입력값이 있는 경우 기존값을 Update 한다.
	 * 
	 * @param source
	 *            엑셀 데이터
	 * @param target
	 *            기존 데이터
	 * @return
	 */
	private Object mergeExcelObject(Object source, Object target) {
		Class<?> scl = source.getClass();
		mergeExcelObjectByClass(scl, source, target);

		if (!scl.getSimpleName().equals("Meter") && !scl.getSimpleName().equals("Modem")
				&& !scl.getSimpleName().equals("MCU")) {
			Class<?> pcl = scl.getSuperclass();

			for (int i = 0; i < 10; i++) {
				mergeExcelObjectByClass(pcl, source, target);

				if (pcl.getSimpleName().equals("Meter") || pcl.getSimpleName().equals("Modem")
						|| pcl.getSimpleName().equals("MCU")) {
					break;
				} else {
					pcl = pcl.getSuperclass();
				}
			}
		}
		return target;
	}

	/**
	 * method name : mergeExcelObjectByClass<b/> method Desc :
	 * 
	 * @param clas
	 * @param source
	 * @param target
	 */
	private void mergeExcelObjectByClass(Class<?> clas, Object source, Object target) {
		Field[] fieldlist = clas.getDeclaredFields();
		StringBuilder getMethodName = new StringBuilder();
		StringBuilder setMethodName = new StringBuilder();

		for (Field fld : fieldlist) {
			if (fld.getModifiers() == Modifier.PRIVATE) {
				// member 변수 타입과 setter 파라메터 타입이 다른 경우 예외 처리.
				if (clas.getSimpleName().equals("EnergyMeter")) {
					if (fld.getName().equals("switchStatus")) {
						if (((EnergyMeter) source).getSwitchStatus() != null) {
							((EnergyMeter) target).setSwitchStatus(((EnergyMeter) source).getSwitchStatus().getCode());
						}
						continue;
					}
				} else if (clas.getSimpleName().equals("Modem")) {
					if (fld.getName().equals("modemType")) {
						if (((Modem) source).getModemType() != null) {
							((Modem) target).setModemType(((Modem) source).getModemType().name());
						}
						continue;
					} else if (fld.getName().equals("protocolType")) {
						if (((Modem) source).getProtocolType() != null) {
							((Modem) target).setProtocolType(((Modem) source).getProtocolType().name());
						}
						continue;
					}
				} else if (clas.getSimpleName().equals("ZBRepeater")) {
					if (fld.getName().equals("networkType")) {
						if (((ZBRepeater) source).getNetworkType() != null) {
							((ZBRepeater) target).setNetworkType(((ZBRepeater) source).getNetworkType().name());
						}
						continue;
					} else if (fld.getName().equals("powerType")) {
						if (((ZBRepeater) source).getPowerType() != null) {
							((ZBRepeater) target).setPowerType(((ZBRepeater) source).getPowerType().name());
						}
						continue;
					} else if (fld.getName().equals("batteryStatus")) {
						if (((ZBRepeater) source).getBatteryStatus() != null) {
							((ZBRepeater) target).setBatteryStatus(((ZBRepeater) source).getBatteryStatus().name());
						}
						continue;
					}
				} else if (clas.getSimpleName().equals("ZEUPLS")) {
					if (fld.getName().equals("networkType")) {
						if (((ZEUPLS) source).getNetworkType() != null) {
							((ZEUPLS) target).setNetworkType(((ZEUPLS) source).getNetworkType().name());
						}
						continue;
					} else if (fld.getName().equals("powerType")) {
						if (((ZEUPLS) source).getPowerType() != null) {
							((ZEUPLS) target).setPowerType(((ZEUPLS) source).getPowerType().name());
						}
						continue;
					} else if (fld.getName().equals("batteryStatus")) {
						if (((ZEUPLS) source).getBatteryStatus() != null) {
							((ZEUPLS) target).setBatteryStatus(((ZEUPLS) source).getBatteryStatus().name());
						}
						continue;
					} else if (fld.getName().equals("voltOffset")) {
						if (((ZEUPLS) source).getVoltOffset() != null) {
							((ZEUPLS) target).setVoltOffset(((ZEUPLS) source).getVoltOffset());
						}
						continue;
					}
				}

				getMethodName.delete(0, getMethodName.length());
				setMethodName.delete(0, setMethodName.length());

				getMethodName.append("get");
				getMethodName.append(fld.getName().substring(0, 1).toUpperCase());
				getMethodName.append(fld.getName().substring(1));
				setMethodName.append("set");
				setMethodName.append(fld.getName().substring(0, 1).toUpperCase());
				setMethodName.append(fld.getName().substring(1));

				try {
					Class<?>[] emptyCls = new Class<?>[0];
					Method getMth = clas.getMethod(getMethodName.toString(), emptyCls);
					Method setMth = clas.getMethod(setMethodName.toString(), fld.getType());
					Object[] emptyObj = new Object[0];
					Object obj = getMth.invoke(source, emptyObj);

					if (obj instanceof String) {
						if (!StringUtil.nullToBlank(obj).isEmpty()) {
							setMth.invoke(target, obj);
						}
					} else {
						if (obj != null) {
							setMth.invoke(target, obj);
						}
					}
				} catch (Exception e) {
					logger.error(e.toString(), e);
				}
			}
		}
	}

	@Override
	public List<Object> getShipmentImportHistory(Map<String, Object> condition) {
		return deviceRegistrationDao.getShipmentImportHistory(condition, false);
	}

	@Override
	public Integer getShipmentImportHistoryTotalCount(Map<String, Object> condition) {
		List<Object> result = deviceRegistrationDao.getShipmentImportHistory(condition, true);
		return Integer.parseInt(result.get(0).toString());
	}
}