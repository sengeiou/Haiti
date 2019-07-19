package com.aimir.schedule.task;

import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthTMDao;
//import com.aimir.fep.demandResponse.DemandResponseCommon;


@Transactional
public class GetDREventStatesTask {

	
	protected static Log log = LogFactory.getLog(GetDREventStatesTask.class);
	@Autowired
	DayTMDao daytmDao;

	@Autowired
	DayHUMDao dayhumDao;

	@Autowired
	MonthTMDao monthtmDao;
	
	@Autowired
	MonthHUMDao monthhumDao;
	
	@Autowired
	MeterDao meterDao;
	
	//@Autowired
	//DemandResponseCommon drCommon;

	public void excute() {
		log.info("#######################Demand Response Start#######################");
		try{
			//drCommon.getDrEventStates();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/*
	private void setMonthHUMAvgMaxMin(MonthHUM monthHUM) {
		Double avg = 0d;
		Double max = 0d;
		Double min = 100d;
		Double sum = 0d;
		int j = 0;
		for (int i = 1; i < 32; i++) {
			try {
				if (BeanUtils.getProperty(monthHUM, "value_" + to2Digit(i)) != null) {
					Double value = Double.parseDouble(BeanUtils.getProperty(
							monthHUM, "value_" + to2Digit(i)));
					if (j == 0) {
						min = value;
					}
					if (value >= max) {
						max = value;
					}
					if (value < min) {
						min = value;
					}
					sum = sum + value;

					j++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		avg = sum / j;
		monthHUM.setAvgValue(avg);
		monthHUM.setMaximumValue(max);
		monthHUM.setMinimumValue(min);

	}
	*/

}