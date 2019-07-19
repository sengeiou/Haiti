package com.aimir.mars.integration.multispeak.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.Address;
import org.multispeak.version_4.ArrayOfAssessment;
import org.multispeak.version_4.ArrayOfAssessmentLocation;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.Assessment;
import org.multispeak.version_4.AssessmentLocation;
import org.multispeak.version_4.DetailedAddressFields;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.GpsPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.Category;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

@Service
@Transactional
public class AssessmentLocationChangedNotificationService {

    private static Log log = LogFactory
            .getLog(AssessmentLocationChangedNotificationService.class);

    @Autowired
    private MCUDao mcuDao;
    @Autowired
    private DeviceModelDao deviceModelDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private LocationDao locationDao;
    @Autowired
    private CodeDao codeDao;

    public ArrayOfErrorObject execute(ArrayOfAssessmentLocation locations)
            throws Exception {

        if (log.isDebugEnabled())
            log.debug(
                    "AssessmentLocationChangedNotificationService execute start..");
        if (log.isTraceEnabled())
            log.trace("Parameter : " + locations);

        
        ArrayOfErrorObject resp = new ArrayOfErrorObject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());

        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Code deleteStatus = codeDao.getCodeIdByCodeObject(CommonConstants.McuStatus.Delete.getCode());
        //Code deleteStatus = codeDao.getCodeIdByCodeObject("1.1.4.2");
        Code normalStatus = codeDao.getCodeIdByCodeObject(CommonConstants.McuStatus.Normal.getCode());
        //Code normalStatus = codeDao.getCodeIdByCodeObject("1.1.4.1");
        Code deactivateStatus = codeDao.getCodeIdByCodeObject(CommonConstants.McuStatus.Deativate.getCode());
        //Code deactivateStatus = codeDao.getCodeIdByCodeObject("1.1.4.6");
        DeviceModel deviceModel = deviceModelDao.getDeviceModelByName(0, MarsProperty.getProperty("default.dcu.name","NDC-I336")).get(0);
        Supplier supplier = supplierDao.getSupplierByName(MarsProperty.getProperty("default.supplier.name","SORIA"));

        List<AssessmentLocation> target = locations.getAssessmentLocation();

        for (AssessmentLocation location : target) {

            ErrorObject obj = new ErrorObject();
            ArrayOfAssessment assessmentList = location.getAssessmentList();
            GpsPoint gpsPoint = location.getGpsPoint();
            Address address = location.getAddress();
            String gridLocation = location.getGridLocation();
            String locationOffset = location.getLocationOffset();
            MCU mcu = null;
            Location mcuLocation = null;
            Location defaultLocation = locationDao.findByCondition("geocode",
                    MarsProperty.getProperty("default.location.geocode"));
            try {
                if (assessmentList != null
                        && assessmentList.getAssessment().size() > 0) {

                    Assessment assessment = assessmentList.getAssessment().get(0);
                    String category = assessment.getCategory();
                    String elementID = assessment.getElementID();
                    // String elementName = assessment.getElementName();
                    String elementType = assessment.getElementType();
                    String comments = assessment.getComments();
                    XMLGregorianCalendar createdOn = assessment.getCreatedOn();
                    XMLGregorianCalendar closedOn = assessment.getClosedOn();
                    String geocode = null;
                    if(assessment.getUtility() != null) {
                        geocode = assessment.getUtility();
                        if(geocode != null) {
                            mcuLocation = locationDao.findByCondition("geocode", geocode);
                        }
                    }

                    boolean isInvalidParameter = false;
                    obj.setNounType("");
                    obj.setObjectID(assessment.getObjectID());

                    if (category == null || "".equals(category)) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName()
                                + " [category] is empty.");
                        isInvalidParameter = true;
                    } else if (!category.equals(Category.Add.getName())
                            && !category.equals(Category.Remove.getName())
                            && !category.equals(Category.Deactivation.getName())) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName()
                                + " [category] is wrong.");
                        isInvalidParameter = true;
                    } else if (elementID == null || "".equals(elementID)) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName()
                                + " [elementID] is empty.");
                        isInvalidParameter = true;
                    } else if (elementType == null || "".equals(elementType)
                            || !McuType.DCU.name().equals(elementType)) {
                        obj.setErrorString(
                                ValidationError.INVALID_PARAMETER.getName()
                                + " [elementType] is empty or wrong.");
                        isInvalidParameter = true;
                    }

                    if (isInvalidParameter == false && category != null
                            && category.equals(Category.Add.getName())) {
                        if (locationOffset == null || "".equals(locationOffset)) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [locationOffset] is empty.");
                            isInvalidParameter = true;
                        } else if (gpsPoint == null) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [gpsPoint] is empty.");
                            isInvalidParameter = true;
                        } else if (geocode == null || "".equals(geocode)) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [utility] is empty.");
                            isInvalidParameter = true;
                        } else if (geocode != null && mcuLocation == null) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [utility] is invalid.");
                            isInvalidParameter = true;
                        }else if (createdOn == null) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [createdOn] is empty.");
                            isInvalidParameter = true;
                        } else if (comments == null || "".equals(elementID)) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [comments] is empty.");
                            isInvalidParameter = true;
                        }
                    } else if (isInvalidParameter == false && category != null
                            && (category.equals(Category.Remove.getName())
                                    || category.equals(
                                            Category.Deactivation.getName()))) {
                        if (closedOn == null) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [closedOn] is empty.");
                            isInvalidParameter = true;
                        } else if (geocode != null && mcuLocation == null) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER.getName()
                                    + " [utility] is invalid.");
                            isInvalidParameter = true;
                        }
                    }

                    if (isInvalidParameter == false && category != null) {
                        mcu = mcuDao.get(elementID);

                        if(category.equals(Category.Add.getName())) {
                            if (mcu != null) {
                                if ((mcu.getMcuStatus() != null
                                        && mcu.getMcuStatus().getId()
                                                .equals(deleteStatus.getId()))
                                        || mcu.getLocation() == null
                                            || mcu.getLocation().getId()
                                                    .intValue() == mcuLocation.getId()
                                                            .intValue()
                                            || mcu.getLocation().getId()
                                                    .intValue() == defaultLocation.getId()
                                                            .intValue()) {
                                    mcu.setSysID(elementID);
                                    McuType mcuTypeEnum = McuType.DCU;

                                    if (comments != null && !"".equals(comments)) {
                                        // “Default RF Antenna”
                                        // “Extension RF Antenna”
                                        // “Default MBB Antenna”
                                        // “Extension MBB Antenna”
                                        mcu.setSysDescr(comments);
                                    }
                                    if (mcu.getDeviceModel() == null) {
                                        mcu.setDeviceModel(deviceModel);
                                    }
                                    if (mcu.getSupplier() == null) {
                                        mcu.setSupplier(supplier);
                                    }
                                    mcu.setMcuType(CommonConstants
                                            .getMcuTypeByName(McuType.DCU.name()));
                                    mcu.setInstallDate(sdf.format(
                                            createdOn.toGregorianCalendar().getTime()));
                                    mcu.setLastModifiedDate(sdf.format(
                                            createdOn.toGregorianCalendar().getTime()));
                                    mcu.setMcuStatus(normalStatus);
                                    if (gpsPoint != null) {
                                        mcu.setGpioX(gpsPoint.getLongitude());
                                        mcu.setGpioY(gpsPoint.getLatitude());
                                        mcu.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                                    }

                                    if(mcuLocation != null) {
                                        mcu.setLocation(mcuLocation);
                                    }
                                    if (address != null) {
                                        String locDetail = "";
                                        locDetail += address.getAddress1();
                                        locDetail += " " + address.getAddress2();
                                        locDetail += "," + address.getTownCode();
                                        locDetail += "," + address.getCity();
                                        locDetail += "," + address.getPostalCode();
                                        locDetail += "," + address.getState();
                                        locDetail += "," + address.getCountry();
                                        DetailedAddressFields addressField = address
                                                .getDetailedAddressFields();

                                        if (addressField != null) {
                                            addressField.getAddressGeneral();
                                            addressField.getBuildingNumber();
                                            addressField.getPostOfficeBox();
                                            addressField.getStreetNumber();
                                            addressField.getStreetPrefix();
                                            addressField.getStreetSuffix();
                                            addressField.getStreetType();
                                            addressField.getRegion();
                                            addressField.getSuiteNumber();

                                            mcu.setLocDetail(locDetail);
                                        }
                                    }
                                    if (gridLocation != null
                                            && !"".equals(gridLocation)) {
                                        mcu.setSysLocation(gridLocation);
                                    }
                                    mcuDao.update(mcu);
                                } else {
                                    obj.setErrorString(ValidationError.DCU_ALREADY_EXIST
                                        .getName());
                                }
                            } else {
                                mcu = new MCU();
                                mcu.setSysID(elementID);
                                McuType mcuTypeEnum = McuType.DCU;

                                if (comments != null && !"".equals(comments)) {
                                    // “Default RF Antenna”
                                    // “Extension RF Antenna”
                                    // “Default MBB Antenna”
                                    // “Extension MBB Antenna”
                                    mcu.setSysDescr(comments);
                                }
                                mcu.setSupplier(supplier);
                                mcu.setDeviceModel(deviceModel);
                                mcu.setMcuType(CommonConstants
                                        .getMcuTypeByName(McuType.DCU.name()));
                                mcu.setInstallDate(sdf.format(
                                        createdOn.toGregorianCalendar().getTime()));
                                mcu.setLastModifiedDate(sdf.format(
                                        createdOn.toGregorianCalendar().getTime()));
                                mcu.setMcuStatus(normalStatus);
                                if (gpsPoint != null) {
                                    mcu.setGpioX(gpsPoint.getLongitude());
                                    mcu.setGpioY(gpsPoint.getLatitude());
                                    mcu.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                                }

                                if(mcuLocation != null) {
                                    mcu.setLocation(mcuLocation);
                                }
                                if (address != null) {
                                    String locDetail = "";
                                    locDetail += address.getAddress1();
                                    locDetail += " " + address.getAddress2();
                                    locDetail += "," + address.getTownCode();
                                    locDetail += "," + address.getCity();
                                    locDetail += "," + address.getPostalCode();
                                    locDetail += "," + address.getState();
                                    locDetail += "," + address.getCountry();
                                    DetailedAddressFields addressField = address
                                            .getDetailedAddressFields();

                                    if (addressField != null) {
                                        addressField.getAddressGeneral();
                                        addressField.getBuildingNumber();
                                        addressField.getPostOfficeBox();
                                        addressField.getStreetNumber();
                                        addressField.getStreetPrefix();
                                        addressField.getStreetSuffix();
                                        addressField.getStreetType();
                                        addressField.getRegion();
                                        addressField.getSuiteNumber();

                                        mcu.setLocDetail(locDetail);
                                    }
                                }
                                if (gridLocation != null
                                        && !"".equals(gridLocation)) {
                                    mcu.setSysLocation(gridLocation);
                                }
                                mcuDao.add(mcu);
                            }
                        } else if (category.equals(Category.Remove.getName())) {
                            if (mcu != null && mcuLocation != null
                                    && (mcu.getLocation()!=null && mcu.getLocationId()
                                            .intValue() == mcuLocation.getId()
                                                    .intValue())
                                    && (mcu.getMcuStatus()!=null && !mcu.getMcuStatus().getId()
                                            .equals(deleteStatus.getId()))) {
                                mcu.setLastModifiedDate(sdf.format(
                                        closedOn.toGregorianCalendar().getTime()));
                                mcu.setMcuStatus(deleteStatus);
                                mcuDao.update(mcu);
                            } else if (mcu != null && mcuLocation == null) {
                                mcu.setLastModifiedDate(sdf.format(
                                        closedOn.toGregorianCalendar().getTime()));
                                mcu.setMcuStatus(deleteStatus);
                                mcuDao.update(mcu);
                            } else if (mcu != null && mcuLocation != null
                                    && mcu.getLocationId()
                                            .intValue() != mcuLocation.getId()
                                                    .intValue()) {
                                obj.setErrorString(
                                        ValidationError.INVALID_PARAMETER
                                                .getName() + ": The requested dcu does not exist in utility["+mcuLocation.getGeocode()+"].");
                            } else {
                                obj.setErrorString(
                                        ValidationError.ALREADY_REMOVED
                                                .getName());
                            }
                        } else if (category
                                .equals(Category.Deactivation.getName())) {
                            if (mcu != null && mcuLocation != null
                                    && (mcu.getLocationId() != null
                                            && mcu.getLocationId()
                                                    .intValue() == mcuLocation
                                                            .getId().intValue())
                                    && (mcu.getMcuStatus() != null
                                            && !mcu.getMcuStatus().getId()
                                                    .equals(deactivateStatus
                                                            .getId()))) {
                                if (mcu.getMcuStatus() != null
                                        && !mcu.getMcuStatus().getId()
                                        .equals(deleteStatus.getId())) {
                                    mcu.setLastModifiedDate(sdf.format(
                                            closedOn.toGregorianCalendar().getTime()));
                                    mcu.setMcuStatus(deactivateStatus);
                                    mcuDao.update(mcu);
                                } else {
                                    obj.setErrorString(
                                            ValidationError.ALREADY_REMOVED
                                                    .getName());
                                }
                            } else if (mcu != null && mcuLocation == null) {
                                mcu.setLastModifiedDate(sdf.format(
                                        closedOn.toGregorianCalendar().getTime()));
                                mcu.setMcuStatus(deactivateStatus);
                                mcuDao.update(mcu);
                            } else if (mcu != null && mcuLocation != null
                                    && mcu.getLocationId()
                                            .intValue() != mcuLocation.getId()
                                                    .intValue()) {
                                obj.setErrorString(
                                        ValidationError.INVALID_PARAMETER
                                                .getName() + ": The requested dcu does not exist in utility["+mcuLocation.getGeocode()+"].");
                            } else {
                                obj.setErrorString(
                                        ValidationError.ALREADY_DEACTIVATED
                                                .getName());
                            }
                        }
                        mcuDao.flush();
                    }
                } else {
                    obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                            + " [assessmentList] is empty.");
                    obj.setNounType("");
                    obj.setObjectID(location.getObjectID());
                }
            }catch(Exception e) {
                log.error(e,e);
                obj.setErrorString(ValidationError.SYSTEM_ERROR.getName());
                obj.setNounType("");
                obj.setEventTime(eventTime);
            }

            if (obj.getErrorString() != null
                    && !"".equals(obj.getErrorString())) {
                obj.setEventTime(eventTime);
                resp.getErrorObject().add(obj);
            }
        }

        if (log.isDebugEnabled())
            log.debug(
                    "AssessmentLocationChangedNotificationService execute end..");

        return resp;
    }

}