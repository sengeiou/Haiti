package com.aimir.mars.integration.multispeak.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ElectricMeter;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.GasMeter;
import org.multispeak.version_4.GpsPoint;
import org.multispeak.version_4.Meters;
import org.multispeak.version_4.PropaneMeter;
import org.multispeak.version_4.UtilityInfo;
import org.multispeak.version_4.WaterMeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;

@Service
@Transactional
public class MeterAddNotificationService {

    private static Log log = LogFactory
            .getLog(MeterAddNotificationService.class);

    @Autowired
    private MeterDao meterDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private LocationDao locationDao;
    @Autowired
    private CodeDao codeDao;

    public ArrayOfErrorObject execute(Meters addMeters) throws Exception {

        if (log.isDebugEnabled())
            log.debug("MeterAddNotificationService execute start..");
        if (log.isTraceEnabled())
            log.trace("Parameter : " + addMeters);

        ArrayOfErrorObject resp = new ArrayOfErrorObject();

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Code deleteStatus = codeDao.getCodeIdByCodeObject("1.3.3.9");
        String mdsId = null;
        String geocode = null;
        XMLGregorianCalendar installedDate = null;
        String strInstalledDate = null;
        boolean isInvalidParameter = false;
        String meterType = null;
        String billingCycle = null;
        UtilityInfo utilityInfo = null;
        String serviceLocationID = null;
        GpsPoint gpsPoint = null;
        Code newRegisteredMeterStatus = codeDao.getCodeIdByCodeObject("1.3.3.8");
        Location defaultLocation = locationDao.findByCondition("geocode",
                MarsProperty.getProperty("default.location.geocode"));

        if (addMeters.getElectricMeters() != null && !addMeters
                .getElectricMeters().getElectricMeter().isEmpty()) {
            List<ElectricMeter> target = new ArrayList<ElectricMeter>(
                    addMeters.getElectricMeters().getElectricMeter());
            for (ElectricMeter meter : target) {
                isInvalidParameter = false;
                mdsId = meter.getMeterNo();
                geocode = meter.getUtility();
                installedDate = meter.getInstalledDate();
                meterType = meter.getMeterType();
                billingCycle = meter.getBillingCycle();
                utilityInfo = meter.getUtilityInfo();
                if (utilityInfo != null) {
                    serviceLocationID = utilityInfo.getServiceLocationID();
                    gpsPoint = utilityInfo.getGpsPoint();
                } else {
                    serviceLocationID = null;
                    gpsPoint = null;
                }
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }

                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(meter.getObjectID());

                obj = checkParam(obj, mdsId, geocode,
                        installedDate, meterType, billingCycle,
                        utilityInfo, serviceLocationID, gpsPoint);
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    isInvalidParameter = true;
                }

                if (isInvalidParameter) {
                } else if (geocode != null && meterLocation == null) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [utility] is invalid.");
                    isInvalidParameter = true;
                } else if (meterType != null && !"ElectricityMeter".equals(meterType)) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [meterType] is wrong.");
                    isInvalidParameter = true;
                }

                if (isInvalidParameter == false) {
                    if (installedDate != null) {
                        strInstalledDate = sdf.format(meter.getInstalledDate()
                                .toGregorianCalendar().getTime());
                    }

                    Meter addMeter = meterDao.get(mdsId);
                    if (addMeter != null) {
                        if ((addMeter.getMeterStatus() != null
                                && addMeter.getMeterStatus().getId()
                                        .equals(deleteStatus.getId()))
                                || addMeter.getLocation() == null
                                || addMeter.getLocation().getId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                || addMeter.getLocation().getId()
                                        .intValue() == defaultLocation.getId()
                                                .intValue()) {
                            addMeter.setInstallId(meter.getObjectID());
                            addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                            addMeter.setLocation(meterLocation);
                            addMeter.setGpioX(gpsPoint.getLongitude());
                            addMeter.setGpioY(gpsPoint.getLatitude());
                            addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                            addMeter.setMeterStatus(newRegisteredMeterStatus);
                            obj = updateMeter(addMeter, obj, strInstalledDate);
                        } else {
                            obj.setErrorString(
                                    ValidationError.METER_ALREADY_EXIST
                                            .getName());
                        }
                    } else {
                        addMeter = new EnergyMeter();
                        addMeter.setMdsId(mdsId);
                        addMeter.setInstallId(meter.getObjectID());
                        addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                        addMeter.setLocation(meterLocation);
                        addMeter.setGpioX(gpsPoint.getLongitude());
                        addMeter.setGpioY(gpsPoint.getLatitude());
                        addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                        addMeter.setMeterStatus(newRegisteredMeterStatus);
                        addMeter.setMeterType(CommonConstants
                                .getMeterTypeByName(MeterType.EnergyMeter.name()));
                        obj = addMeter( addMeter, obj, strInstalledDate);
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        } else if (addMeters.getWaterMeters() != null && !addMeters
                .getWaterMeters().getWaterMeter().isEmpty()) {
            List<WaterMeter> target = new ArrayList<WaterMeter>(
                    addMeters.getWaterMeters().getWaterMeter());
            for (WaterMeter meter : target) {
                isInvalidParameter = false;
                mdsId = meter.getMeterNo();
                geocode = meter.getUtility();
                installedDate = meter.getInstalledDate();
                meterType = meter.getMeterType();
                billingCycle = meter.getBillingCycle();
                utilityInfo = meter.getUtilityInfo();
                if (utilityInfo != null) {
                    serviceLocationID = utilityInfo.getServiceLocationID();
                    gpsPoint = utilityInfo.getGpsPoint();
                } else {
                    serviceLocationID = null;
                    gpsPoint = null;
                }
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }
                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(meter.getObjectID());
                obj = checkParam(obj, mdsId, geocode,
                        installedDate, meterType, billingCycle,
                        utilityInfo, serviceLocationID, gpsPoint);
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    isInvalidParameter = true;
                }

                if (isInvalidParameter) {
                } else if (geocode != null && meterLocation == null) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [utility] is invalid.");
                    isInvalidParameter = true;
                } else if (meterType != null && !"WaterMeter".equals(meterType)) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [meterType] is wrong.");
                    isInvalidParameter = true;
                }

                if (isInvalidParameter == false) {
                    if (installedDate != null) {
                        strInstalledDate = sdf.format(meter.getInstalledDate()
                                .toGregorianCalendar().getTime());
                    }

                    Meter addMeter = meterDao.get(mdsId);
                    if (addMeter != null) {
                        if ((addMeter.getMeterStatus() != null
                                && addMeter.getMeterStatus().getId()
                                .equals(deleteStatus.getId()))
                                || addMeter.getLocation() == null
                                || addMeter.getLocation().getId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                || addMeter.getLocation().getId()
                                        .intValue() == defaultLocation.getId()
                                                .intValue()) {
                            addMeter.setInstallId(meter.getObjectID());
                            addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                            addMeter.setLocation(meterLocation);
                            addMeter.setGpioX(gpsPoint.getLongitude());
                            addMeter.setGpioY(gpsPoint.getLatitude());
                            addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                            addMeter.setMeterStatus(newRegisteredMeterStatus);
                            obj = updateMeter(addMeter, obj, strInstalledDate); 
                        } else {
                            obj.setErrorString(
                                    ValidationError.METER_ALREADY_EXIST.getName());
                        };
                    } else {
                        addMeter = new com.aimir.model.device.WaterMeter();
                        addMeter.setMdsId(mdsId);
                        addMeter.setInstallId(meter.getObjectID());
                        addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                        addMeter.setLocation(meterLocation);
                        addMeter.setGpioX(gpsPoint.getLongitude());
                        addMeter.setGpioY(gpsPoint.getLatitude());
                        addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                        addMeter.setMeterStatus(newRegisteredMeterStatus);
                        addMeter.setMeterType(CommonConstants
                                .getMeterTypeByName(MeterType.WaterMeter.name()));
                        obj = addMeter( addMeter, obj, strInstalledDate);
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        } else if (addMeters.getGasMeters() != null && !addMeters
                .getGasMeters().getGasMeter().isEmpty()) {
            List<GasMeter> target = new ArrayList<GasMeter>(
                    addMeters.getGasMeters().getGasMeter());
            for (GasMeter meter : target) {
                isInvalidParameter = false;
                mdsId = meter.getMeterNo();
                geocode = meter.getUtility();
                installedDate = meter.getInstalledDate();
                meterType = meter.getMeterType();
                billingCycle = meter.getBillingCycle();
                utilityInfo = meter.getUtilityInfo();
                if (utilityInfo != null) {
                    serviceLocationID = utilityInfo.getServiceLocationID();
                    gpsPoint = utilityInfo.getGpsPoint();
                } else {
                    serviceLocationID = null;
                    gpsPoint = null;
                }
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }
                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(meter.getObjectID());

                obj = checkParam(obj, mdsId, geocode,
                        installedDate, meterType, billingCycle,
                        utilityInfo, serviceLocationID, gpsPoint);
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    isInvalidParameter = true;
                }

                if (isInvalidParameter) {
                } else if (geocode != null && meterLocation == null) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [utility] is invalid.");
                    isInvalidParameter = true;
                } else if (meterType != null && !"GasMeter".equals(meterType)) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [meterType] is wrong.");
                    isInvalidParameter = true;
                }

                if (isInvalidParameter == false) {
                    if (installedDate != null) {
                        strInstalledDate = sdf.format(meter.getInstalledDate()
                                .toGregorianCalendar().getTime());
                    }

                    Meter addMeter = meterDao.get(mdsId);
                    if (addMeter != null) {
                        if ((addMeter.getMeterStatus() != null
                                && addMeter.getMeterStatus().getId()
                                .equals(deleteStatus.getId()))
                                || addMeter.getLocation() == null
                                || addMeter.getLocation().getId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                || addMeter.getLocation().getId()
                                        .intValue() == defaultLocation.getId()
                                                .intValue()) {
                            addMeter.setInstallId(meter.getObjectID());
                            addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                            addMeter.setLocation(meterLocation);
                            addMeter.setGpioX(gpsPoint.getLongitude());
                            addMeter.setGpioY(gpsPoint.getLatitude());
                            addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                            addMeter.setMeterStatus(newRegisteredMeterStatus);
                            obj = updateMeter(addMeter, obj, strInstalledDate);
                        } else {
                            obj.setErrorString(
                                    ValidationError.METER_ALREADY_EXIST.getName());
                        };
                    } else {
                        addMeter = new com.aimir.model.device.GasMeter();
                        addMeter.setMdsId(mdsId);
                        addMeter.setInstallId(meter.getObjectID());
                        addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                        addMeter.setLocation(meterLocation);
                        addMeter.setGpioX(gpsPoint.getLongitude());
                        addMeter.setGpioY(gpsPoint.getLatitude());
                        addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                        addMeter.setMeterStatus(newRegisteredMeterStatus);
                        addMeter.setMeterType(CommonConstants
                                .getMeterTypeByName(MeterType.GasMeter.name()));
                        obj = addMeter( addMeter, obj, strInstalledDate);
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        } else if (addMeters.getPropaneMeters() != null && !addMeters
                .getPropaneMeters().getPropaneMeter().isEmpty()) {
            List<PropaneMeter> target = new ArrayList<PropaneMeter>(
                    addMeters.getPropaneMeters().getPropaneMeter());
            for (PropaneMeter meter : target) {
                isInvalidParameter = false;
                mdsId = meter.getMeterNo();
                geocode = meter.getUtility();
                installedDate = meter.getInstalledDate();
                meterType = meter.getMeterType();
                billingCycle = meter.getBillingCycle();
                utilityInfo = meter.getUtilityInfo();
                if (utilityInfo != null) {
                    serviceLocationID = utilityInfo.getServiceLocationID();
                    gpsPoint = utilityInfo.getGpsPoint();
                } else {
                    serviceLocationID = null;
                    gpsPoint = null;
                }
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }
                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(meter.getObjectID());

                obj = checkParam(obj, mdsId, geocode,
                        installedDate, meterType, billingCycle,
                        utilityInfo, serviceLocationID, gpsPoint);
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    isInvalidParameter = true;
                }

                if (isInvalidParameter) {
                } else if (geocode != null && meterLocation == null) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [utility] is invalid.");
                    isInvalidParameter = true;
                } else if (meterType != null && !"HeatMeter".equals(meterType)) {
                    obj.setErrorString(
                            ValidationError.INVALID_PARAMETER.getName()
                            + " [meterType] is wrong.");
                    isInvalidParameter = true;
                }

                if (isInvalidParameter == false) {
                    if (installedDate != null) {
                        strInstalledDate = sdf.format(meter.getInstalledDate()
                                .toGregorianCalendar().getTime());
                    }

                    Meter addMeter = meterDao.get(mdsId);
                    if (addMeter != null) {
                        if ((addMeter.getMeterStatus() != null
                                && addMeter.getMeterStatus().getId()
                                .equals(deleteStatus.getId()))
                                || addMeter.getLocation() == null
                                || addMeter.getLocation().getId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                || addMeter.getLocation().getId()
                                        .intValue() == defaultLocation.getId()
                                                .intValue()) {
                            addMeter.setInstallId(meter.getObjectID());
                            addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                            addMeter.setLocation(meterLocation);
                            addMeter.setGpioX(gpsPoint.getLongitude());
                            addMeter.setGpioY(gpsPoint.getLatitude());
                            addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                            addMeter.setMeterStatus(newRegisteredMeterStatus);
                            obj = updateMeter(addMeter, obj, strInstalledDate);
                        } else {
                            obj.setErrorString(
                                    ValidationError.METER_ALREADY_EXIST.getName());
                        };
                    } else {
                        addMeter = new HeatMeter();
                        addMeter.setMdsId(mdsId);
                        addMeter.setInstallId(meter.getObjectID());
                        addMeter.setGs1(meter.getUtilityInfo().getServiceLocationID());
                        addMeter.setLocation(meterLocation);
                        addMeter.setGpioX(gpsPoint.getLongitude());
                        addMeter.setGpioY(gpsPoint.getLatitude());
                        addMeter.setGpioZ(gpsPoint.getAltitude()==null?0:gpsPoint.getAltitude());
                        addMeter.setMeterStatus(newRegisteredMeterStatus);
                        addMeter.setMeterType(CommonConstants
                                .getMeterTypeByName(MeterType.HeatMeter.name()));
                        obj = addMeter( addMeter, obj, strInstalledDate);
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        }
        meterDao.flush();

        if (log.isDebugEnabled())
            log.debug("MeterAddNotificationService execute end..");

        return resp;
    }

    private ErrorObject addMeter(Meter addMeter,
            ErrorObject obj, String currTime) {

        addMeter.setWriteDate(
                DateTimeUtil.getDateString(new Date()));
        addMeter.setInstallDate(currTime);

        Supplier supplier = supplierDao.getSupplierByName(MarsProperty.getProperty("default.supplier.name","SORIA"));
        /* 추후에 Utility 정보에 따라 supplier 각각 따로 지정되어야 할 수도 있.
        if ( false && meter.getUtility() != null
                && !"".equals(meter.getUtility())) {
            supplier = supplierDao
                    .getSupplierByName(meter.getUtility());
        }
        */

        if (supplier != null) {
            addMeter.setSupplier(supplier);
            addMeter.setSupplierId(supplier.getId());
        }

        try {
            meterDao.add(addMeter);
            obj.setErrorString("");
        } catch (Exception e) {
            obj.setErrorString(
                    ValidationError.SYSTEM_ERROR.getName());
        }

        return obj;
    }

    private ErrorObject checkParam(ErrorObject obj, String mdsId,
            String geocode, XMLGregorianCalendar installedDate,
            String meterType, String billingCycle, UtilityInfo utilityInfo,
            String serviceLocationID, GpsPoint gpsPoint) {
        if (mdsId == null || "".equals(mdsId)) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterNo] is empty.");
        } else if (geocode == null || "".equals(geocode)) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [utility] is empty.");
        } else if (installedDate == null) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [installedDate] is empty.");
        } else if (meterType == null || "".equals(meterType)) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterType] is empty.");
        } else if (billingCycle == null || "".equals(billingCycle)) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [billingCycle] is empty.");
        } else if (utilityInfo == null) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [utilityInfo] is empty.");
        }

        if (utilityInfo != null) {
            serviceLocationID = utilityInfo.getServiceLocationID();
            gpsPoint = utilityInfo.getGpsPoint();
            if (serviceLocationID == null || "".equals(serviceLocationID)) {
                obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [serviceLocationID] is empty.");
            } else if (gpsPoint == null) {
                obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                        + " [gpsPoint] is empty.");
            }
        }
        return obj;
    }

    private ErrorObject updateMeter(Meter addMeter, ErrorObject obj, String currTime) {
        addMeter.setWriteDate(
                DateTimeUtil.getDateString(new Date()));
        addMeter.setInstallDate(currTime);

        Supplier supplier = supplierDao.getSupplierByName(MarsProperty.getProperty("default.supplier.name","SORIA"));
        /* 추후에 Utility 정보에 따라 supplier 각각 따로 지정되어야 할 수도 있다.
        if ( false && meter.getUtility() != null
                && !"".equals(meter.getUtility())) {
            supplier = supplierDao
                .getSupplierByName(meter.getUtility());
        }
         */

        if (supplier != null) {
            addMeter.setSupplier(supplier);
            addMeter.setSupplierId(supplier.getId());
        }

        try {
            meterDao.update(addMeter);
        } catch (Exception e) {
            obj.setErrorString(
                    ValidationError.SYSTEM_ERROR.getName());
        }

        return obj;
    }
}
