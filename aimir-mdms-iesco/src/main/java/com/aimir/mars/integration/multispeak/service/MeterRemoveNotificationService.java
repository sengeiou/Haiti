package com.aimir.mars.integration.multispeak.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.multispeak.version_4.Meters;
import org.multispeak.version_4.PropaneMeter;
import org.multispeak.version_4.WaterMeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.util.DateTimeUtil;

@Service
@Transactional
public class MeterRemoveNotificationService {

    private static Log log = LogFactory
            .getLog(MeterRemoveNotificationService.class);

    @Autowired
    private MeterDao meterDao;
    @Autowired
    private LocationDao locationDao;
    @Autowired
    private CodeDao codeDao;

    public ArrayOfErrorObject execute(Meters removedMeters) throws Exception {

        log.debug("MeterRemoveNotificationService execute start..");

        ArrayOfErrorObject resp = new ArrayOfErrorObject();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());

        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Code deleteStatus = codeDao.getCodeIdByCodeObject("1.3.3.9");

        String mdsId = null;
        XMLGregorianCalendar removedDate = null;
        String strRemovedDate = null;
        String meterType = null;
        String geocode = null;
        boolean isInvalidParameter = false;
        if (removedMeters.getElectricMeters() != null && !removedMeters
                .getElectricMeters().getElectricMeter().isEmpty()) {
            List<ElectricMeter> target = new ArrayList<ElectricMeter>(
                    removedMeters.getElectricMeters().getElectricMeter());
            for (ElectricMeter cd : target) {
                isInvalidParameter = false;
                mdsId = cd.getMeterNo();
                removedDate = cd.getRemovedDate();
                meterType = cd.getMeterType();
                geocode = cd.getUtility();
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }
                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(cd.getObjectID());

                obj = checkParam(obj, mdsId, geocode, removedDate, meterType);
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
                    if (removedDate != null) {
                        strRemovedDate = sdf.format(removedDate
                                .toGregorianCalendar().getTime());
                    }

                    Meter removeMeter = meterDao.get(cd.getMeterNo());
                    if (removeMeter == null) {
                        obj = allocateAlreadyRemoved(eventTime);
                    } else {
                        if (meterLocation != null
                                && removeMeter.getLocationId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                && (removeMeter.getMeterStatus() != null
                                        && !removeMeter.getMeterStatus().getId()
                                                .equals(deleteStatus
                                                        .getId()))) {
                            removeMeter.setDeleteDate(strRemovedDate);
                            try {
                                removeMeter(removeMeter, deleteStatus);
                            }catch(Exception e) {
                                obj.setErrorString(
                                        ValidationError.SYSTEM_ERROR
                                                .getName());
                            }
                        } else if (meterLocation == null) {
                            removeMeter.setDeleteDate(strRemovedDate);
                            try {
                                removeMeter(removeMeter,deleteStatus);
                            }catch(Exception e) {
                                obj.setErrorString(
                                        ValidationError.SYSTEM_ERROR
                                                .getName());
                            }
                        } else if (meterLocation != null
                                && removeMeter.getLocationId()
                                        .intValue() != meterLocation.getId()
                                                .intValue()) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER
                                            .getName() + ": The requested meter does not exist in utility["+ meterLocation.getGeocode()+"].");
                        } else {
                            obj.setErrorString(
                                    ValidationError.ALREADY_REMOVED
                                            .getName());
                        }
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        } else if (removedMeters.getWaterMeters() != null && !removedMeters
                .getWaterMeters().getWaterMeter().isEmpty()) {
            List<WaterMeter> target = new ArrayList<WaterMeter>(
                    removedMeters.getWaterMeters().getWaterMeter());
            for (WaterMeter cd : target) {
                if (cd.getMeterNo() != null) {
                    isInvalidParameter = false;
                    mdsId = cd.getMeterNo();
                    removedDate = cd.getRemovedDate();
                    meterType = cd.getMeterType();
                    geocode = cd.getUtility();
                    Location meterLocation = null;
                    if(geocode != null) {
                        meterLocation = locationDao.findByCondition("geocode", geocode);
                    }
                    ErrorObject obj = new ErrorObject();
                    obj.setEventTime(eventTime);
                    obj.setNounType("");
                    obj.setObjectID(cd.getObjectID());

                    obj = checkParam(obj, mdsId, geocode, removedDate, meterType);
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
                        if (removedDate != null) {
                            strRemovedDate = sdf.format(removedDate
                                    .toGregorianCalendar().getTime());
                        }
                        Meter removeMeter = meterDao.get(cd.getMeterNo());
                        if (removeMeter == null) {
                            obj = allocateAlreadyRemoved(eventTime);
                        } else {
                            if (meterLocation != null
                                    && removeMeter.getLocationId()
                                            .intValue() == meterLocation.getId()
                                                    .intValue()
                                    && (removeMeter.getMeterStatus() != null
                                            && !removeMeter.getMeterStatus()
                                                    .getId().equals(deleteStatus
                                                            .getId()))) {
                                removeMeter.setDeleteDate(strRemovedDate);
                                try {
                                    removeMeter(removeMeter, deleteStatus);
                                } catch (Exception e) {
                                    obj.setErrorString(
                                            ValidationError.SYSTEM_ERROR
                                                    .getName());
                                }
                            } else if (meterLocation == null) {
                                removeMeter.setDeleteDate(strRemovedDate);
                                try {
                                    removeMeter(removeMeter,deleteStatus);
                                }catch(Exception e) {
                                    obj.setErrorString(
                                            ValidationError.SYSTEM_ERROR
                                                    .getName());
                                }
                            } else if (meterLocation != null
                                    && removeMeter.getLocationId()
                                            .intValue() != meterLocation.getId()
                                                    .intValue()) {
                                obj.setErrorString(
                                        ValidationError.INVALID_PARAMETER
                                                .getName() + ": The requested meter does not exist in utility["+ meterLocation.getGeocode()+"].");
                            } else {
                                obj.setErrorString(
                                        ValidationError.ALREADY_REMOVED
                                                .getName());
                            }
                        }
                    }
                    if (obj.getErrorString() != null
                            && !"".equals(obj.getErrorString())) {
                        resp.getErrorObject().add(obj);
                    }
                }
            }
        } else if (removedMeters.getGasMeters() != null && !removedMeters
                .getGasMeters().getGasMeter().isEmpty()) {
            List<GasMeter> target = new ArrayList<GasMeter>(
                    removedMeters.getGasMeters().getGasMeter());
            for (GasMeter cd : target) {
                isInvalidParameter = false;
                mdsId = cd.getMeterNo();
                removedDate = cd.getRemovedDate();
                meterType = cd.getMeterType();
                geocode = cd.getUtility();
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }
                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(cd.getObjectID());

                obj = checkParam(obj, mdsId, geocode, removedDate, meterType);
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
                    if (removedDate != null) {
                        strRemovedDate = sdf.format(removedDate
                                .toGregorianCalendar().getTime());
                    }

                    Meter removeMeter = meterDao.get(cd.getMeterNo());
                    if (removeMeter == null) {
                        obj = allocateAlreadyRemoved(eventTime);
                    } else {
                        if (meterLocation != null
                                && removeMeter.getLocationId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                && (removeMeter.getMeterStatus() != null
                                        && !removeMeter.getMeterStatus().getId()
                                                .equals(deleteStatus
                                                        .getId()))) {
                            removeMeter.setDeleteDate(strRemovedDate);
                            try {
                                removeMeter(removeMeter, deleteStatus);
                            } catch (Exception e) {
                                obj.setErrorString(
                                        ValidationError.SYSTEM_ERROR
                                                .getName());
                            }
                        } else if (meterLocation == null) {
                            removeMeter.setDeleteDate(strRemovedDate);
                            try {
                                removeMeter(removeMeter,deleteStatus);
                            }catch(Exception e) {
                                obj.setErrorString(
                                        ValidationError.SYSTEM_ERROR
                                                .getName());
                            }
                        } else if (meterLocation != null
                                && removeMeter.getLocationId()
                                        .intValue() != meterLocation.getId()
                                                .intValue()) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER
                                            .getName() + ": The requested meter does not exist in utility["+ meterLocation.getGeocode()+"].");
                        } else {
                            obj.setErrorString(
                                    ValidationError.ALREADY_REMOVED
                                            .getName());
                        }
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        } else if (removedMeters.getPropaneMeters() != null && !removedMeters
                .getPropaneMeters().getPropaneMeter().isEmpty()) {
            List<PropaneMeter> target = new ArrayList<PropaneMeter>(
                    removedMeters.getPropaneMeters().getPropaneMeter());
            for (PropaneMeter cd : target) {
                isInvalidParameter = false;
                mdsId = cd.getMeterNo();
                removedDate = cd.getRemovedDate();
                meterType = cd.getMeterType();
                geocode = cd.getUtility();
                Location meterLocation = null;
                if(geocode != null) {
                    meterLocation = locationDao.findByCondition("geocode", geocode);
                }
                ErrorObject obj = new ErrorObject();
                obj.setEventTime(eventTime);
                obj.setNounType("");
                obj.setObjectID(cd.getObjectID());

                obj = checkParam(obj, mdsId, geocode, removedDate, meterType);
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
                    if (removedDate != null) {
                        strRemovedDate = sdf.format(removedDate
                                .toGregorianCalendar().getTime());
                    }

                    Meter removeMeter = meterDao.get(cd.getMeterNo());
                    if (removeMeter == null) {
                        obj = allocateAlreadyRemoved(eventTime);
                    } else {
                        if (meterLocation != null
                                && removeMeter.getLocationId()
                                        .intValue() == meterLocation.getId()
                                                .intValue()
                                && (removeMeter.getMeterStatus() != null
                                        && !removeMeter.getMeterStatus().getId()
                                                .equals(deleteStatus
                                                        .getId()))) {
                            removeMeter.setDeleteDate(strRemovedDate);
                            try {
                                meterDao.update(removeMeter);
                            } catch (Exception e) {
                                obj.setErrorString(
                                        ValidationError.SYSTEM_ERROR
                                                .getName());
                            }
                        } else if (meterLocation == null) {
                            removeMeter.setDeleteDate(strRemovedDate);
                            try {
                                removeMeter(removeMeter,deleteStatus);
                            }catch(Exception e) {
                                obj.setErrorString(
                                        ValidationError.SYSTEM_ERROR
                                                .getName());
                            }
                        } else if (meterLocation != null
                                && removeMeter.getLocationId()
                                        .intValue() != meterLocation.getId()
                                                .intValue()) {
                            obj.setErrorString(
                                    ValidationError.INVALID_PARAMETER
                                            .getName() + ": The requested meter does not exist in utility["+ meterLocation.getGeocode()+"].");
                        } else {
                            obj.setErrorString(
                                    ValidationError.ALREADY_REMOVED
                                            .getName());
                        }
                    }
                }
                if (obj.getErrorString() != null
                        && !"".equals(obj.getErrorString())) {
                    resp.getErrorObject().add(obj);
                }
            }
        }
        meterDao.flush();

        log.debug("MeterRemoveNotificationService execute End..");

        return resp;
    }

    private ErrorObject checkParam(ErrorObject obj, String mdsId,
            String geocode, XMLGregorianCalendar removedDate,
            String meterType) {
        if (mdsId == null || "".equals(mdsId)) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterNo] is empty.");
        } else if (removedDate == null) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [removedDate] is empty.");
        } else if (meterType == null || "".equals(meterType)) {
            obj.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [meterType] is empty.");
        }

        return obj;
    }

    private void removeMeter(Meter removeMeter, Code deleteStatus) {
        removeMeter.setDeleteDate(
                DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        removeMeter.setMeterStatus(deleteStatus);
        meterDao.update(removeMeter);
    }

    private ErrorObject allocateAlreadyRemoved(XMLGregorianCalendar eventTime) {
        ErrorObject obj = new ErrorObject();
        obj.setErrorString(ValidationError.ALREADY_REMOVED.getName());// already removed meter
        obj.setEventTime(eventTime);
        obj.setNounType("");
        return obj;
    }
}
