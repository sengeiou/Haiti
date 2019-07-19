package com.aimir.schedule.task;

import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DayHUM;
import com.aimir.model.mvm.DayTM;
import com.aimir.model.mvm.MonthHUM;
import com.aimir.model.mvm.MonthTM;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.schedule.util.BemsProperty;


@Transactional
public class GetTemperatureDataTask {

	
	protected static Log log = LogFactory.getLog(GetTemperatureDataTask.class);
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

	private String to2Digit(int str) {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(str);
	}

	public void excute() {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL(BemsProperty.getProperty("korea.weather.url"));
			
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			log.error(e,e);
			return;
		}

		Element root = doc.getRootElement();
		Element header = root.getChild("header");
		Element body = root.getChild("body");
		String yyyymmddHHmm = header.getChild("tm").getValue();

		List<Element> list = body.getChildren();
		log.info("#######################temperature,humidity save start#######################");
		for (Element obj : list) {
			Calendar calTM = Calendar.getInstance();
			Calendar calHUM = Calendar.getInstance();
			try {
				calTM.setTime(DateTimeUtil
						.getDateFromYYYYMMDDHHMMSS(yyyymmddHHmm + "00"));

				calHUM.setTime(DateTimeUtil
						.getDateFromYYYYMMDDHHMMSS(yyyymmddHHmm + "00"));

			} catch (ParseException e) {
				e.printStackTrace();
				log.error(e);
			}
			int seq = Integer.parseInt(obj.getAttributeValue("seq"));

			
			if (seq > 7) {
				break;
			}

			int day = Integer.parseInt(obj.getChild("day").getValue());
			int hour = Integer.parseInt(obj.getChild("hour").getValue());

			
			calTM.add(Calendar.DATE, day);
			calHUM.add(Calendar.DATE, day);

			String date = DateTimeUtil.getDateString(calTM.getTime());
			date = date.substring(0, 8);

		
			Double temp = Double.parseDouble(obj.getChild("temp").getValue());
			Double hum = Double.parseDouble(obj.getChild("reh").getValue());
			putDayTM(calTM, date, hour, temp);
			putDayHUM(calHUM, date, hour, hum);
		}
		
		log.info("#######################temperature,humidity save end#######################");

	}

	private void putDayTM(Calendar cal, String date, int hour, Double temp) {
		HashSet<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("id.channel", new Object[] { 1 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.dst", new Object[] { 0 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevType",
				new Object[] { DeviceType.Meter }, null, Restriction.EQ));
		condition.add(new Condition("id.yyyymmdd", new Object[] { date }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { "10000" },
				null, Restriction.EQ));

		List<DayTM> dayTMList = daytmDao.findByConditions(condition);

		try {
			if (dayTMList!= null &&dayTMList.size() > 0) {
				DayTM dayTM = dayTMList.get(0);
				if (hour == 24) {
					BeanUtils.copyProperty(dayTM,
							"value_" + to2Digit(hour - 2), temp);
					BeanUtils.copyProperty(dayTM,
							"value_" + to2Digit(hour - 1), temp);
					setDayTMAvgMaxMin(dayTM);
					putMonthTM( date.substring(0,6), Integer.parseInt(date.substring(6,8)), dayTM.getAvgValue(),dayTM.getMaximumValue(),dayTM.getMinimumValue());
					daytmDao.update(dayTM);
					cal.add(Calendar.DATE, 1);

					String addDate = DateTimeUtil.getDateString(cal.getTime());
					addDate = addDate.substring(0, 8);
					putDayTM(cal, addDate, 0, temp);

				} else {

					BeanUtils.copyProperty(dayTM,
							"value_" + to2Digit(hour - 2), temp);
					BeanUtils.copyProperty(dayTM,
							"value_" + to2Digit(hour - 1), temp);
					BeanUtils.copyProperty(dayTM, "value_" + to2Digit(hour),
							temp);
					setDayTMAvgMaxMin(dayTM);
					daytmDao.update(dayTM);
				}

			} else {
				DayTM dayTM = new DayTM();
				dayTM.id.setChannel(1);
				dayTM.id.setYyyymmdd(date);
				dayTM.id.setDst(0);
				dayTM.id.setMDevType(DeviceType.Meter);
				dayTM.id.setMDevId("10000");
				if (hour == 0) {
					BeanUtils.copyProperty(dayTM, "value_" + to2Digit(hour),
							temp);
				} else {
					BeanUtils.copyProperty(dayTM,
							"value_" + to2Digit(hour - 2), temp);
					BeanUtils.copyProperty(dayTM,
							"value_" + to2Digit(hour - 1), temp);
					BeanUtils.copyProperty(dayTM, "value_" + to2Digit(hour),
							temp);
				}
				setDayTMAvgMaxMin(dayTM);
				daytmDao.add(dayTM);
			
				//
			
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

	}
    
	
	private void setDayTMAvgMaxMin(DayTM dayTM) {
		Double avg = 0d;
		Double max = -30d;
		Double min = 30d;
		Double sum = 0d;
		int j = 0;
		for (int i = 0; i < 24; i++) {
			try {
				if (BeanUtils.getProperty(dayTM, "value_" + to2Digit(i)) != null) {
					Double value = Double.parseDouble(BeanUtils.getProperty(
							dayTM, "value_" + to2Digit(i)));
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
				log.error(e);
			}
		}
		avg = sum / j;
		dayTM.setAvgValue(avg);
		dayTM.setMaximumValue(max);
		dayTM.setMinimumValue(min);

	}

	private void putDayHUM(Calendar cal, String date, int hour, Double hum) {
		HashSet<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("id.channel", new Object[] { 1 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.dst", new Object[] { 0 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevType",
				new Object[] { DeviceType.Meter }, null, Restriction.EQ));
		condition.add(new Condition("id.yyyymmdd", new Object[] { date }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { "10001" },
				null, Restriction.EQ));
     
		List<DayHUM> dayHUMList = dayhumDao.findByConditions(condition);
	
		try {
			if (dayHUMList!= null &&dayHUMList.size() > 0) {
				DayHUM dayHUM = dayHUMList.get(0);
				if (hour == 24) {
					BeanUtils.copyProperty(dayHUM, "value_"
							+ to2Digit(hour - 2), hum);
					BeanUtils.copyProperty(dayHUM, "value_"
							+ to2Digit(hour - 1), hum);
					setDayHUMAvgMaxMin(dayHUM);
					
					putMonthHUM( date.substring(0,6), Integer.parseInt(date.substring(6,8)), dayHUM.getAvgValue());
					
					dayhumDao.update(dayHUM);
					cal.add(Calendar.DATE, 1);

					String addDate = DateTimeUtil.getDateString(cal.getTime());
					addDate = addDate.substring(0, 8);
					putDayHUM(cal, addDate, 0, hum);

				} else {

					BeanUtils.copyProperty(dayHUM, "value_"
							+ to2Digit(hour - 2), hum);
					BeanUtils.copyProperty(dayHUM, "value_"
							+ to2Digit(hour - 1), hum);
					BeanUtils.copyProperty(dayHUM, "value_" + to2Digit(hour),
							hum);
					setDayHUMAvgMaxMin(dayHUM);
					dayhumDao.update(dayHUM);
				}

			} else {
				DayHUM dayHUM = new DayHUM();
				dayHUM.id.setChannel(1);
				dayHUM.id.setYyyymmdd(date);
				dayHUM.id.setDst(0);
				dayHUM.id.setMDevType(DeviceType.Meter);
				dayHUM.id.setMDevId("10001");
				if (hour == 0) {
					BeanUtils.copyProperty(dayHUM, "value_" + to2Digit(hour),
							hum);
				} else {
					BeanUtils.copyProperty(dayHUM, "value_"
							+ to2Digit(hour - 2), hum);
					BeanUtils.copyProperty(dayHUM, "value_"
							+ to2Digit(hour - 1), hum);
					BeanUtils.copyProperty(dayHUM, "value_" + to2Digit(hour),
							hum);
				}
				setDayHUMAvgMaxMin(dayHUM);
				dayhumDao.add(dayHUM);
				
			//	meterLastReadDateUpdate("10001");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

	}

	private void setDayHUMAvgMaxMin(DayHUM dayHUM) {
		Double avg = 0d;
		Double max = 0d;
		Double min = 100d;
		Double sum = 0d;
		int j = 0;
		for (int i = 0; i < 24; i++) {
			try {
				if (BeanUtils.getProperty(dayHUM, "value_" + to2Digit(i)) != null) {
					Double value = Double.parseDouble(BeanUtils.getProperty(
							dayHUM, "value_" + to2Digit(i)));
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
				log.error(e);
			}
		}
		avg = sum / j;
		dayHUM.setAvgValue(avg);
		dayHUM.setMaximumValue(max);
		dayHUM.setMinimumValue(min);

	}

	private void putMonthTM( String date, int day, Double temp,Double tempMax,Double tempMin) {
		HashSet<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("id.channel", new Object[] { 1 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.dst", new Object[] { 0 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevType",
				new Object[] { DeviceType.Meter }, null, Restriction.EQ));
		condition.add(new Condition("id.yyyymm", new Object[] { date }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { "10000" },
				null, Restriction.EQ));

		List<MonthTM> monthTMList = monthtmDao.findByConditions(condition);

		try {
			if (monthTMList.size() > 0) {
				MonthTM monthTM = monthTMList.get(0);

				BeanUtils.copyProperty(monthTM, "value_" + to2Digit(day), temp);
				Double setTempMax = tempMax;
				Double setTempMin = tempMin;
				if(monthTM.getMaximumValue()> tempMax){
					setTempMax = monthTM.getMaximumValue();
				}
				if(monthTM.getMinimumValue() < tempMin){
					setTempMin= monthTM.getMinimumValue();
				}
				
				setMonthTMAvgMaxMin(monthTM,setTempMax,setTempMin);
				monthtmDao.update(monthTM);

			} else {
				MonthTM monthTM = new MonthTM();
				monthTM.id.setChannel(1);
				monthTM.id.setYyyymm(date);
				monthTM.id.setDst(0);
				monthTM.id.setMDevType(DeviceType.Meter);
				monthTM.id.setMDevId("10000");

				BeanUtils.copyProperty(monthTM, "value_" + to2Digit(day), temp);

				setMonthTMAvgMaxMin(monthTM,tempMax,tempMin);
				monthtmDao.add(monthTM);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

	}

	private void setMonthTMAvgMaxMin(MonthTM monthTM,Double tmMax,Double tmMin) {
		Double avg = 0d;
		Double max = -30d;
		Double min = 30d;
		Double sum = 0d;
		int j = 0;
		for (int i = 1; i < 32; i++) {
			try {
				if (BeanUtils.getProperty(monthTM, "value_" + to2Digit(i)) != null) {
					Double value = Double.parseDouble(BeanUtils.getProperty(
							monthTM, "value_" + to2Digit(i)));
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
				log.error(e);
			}
		}
		avg = sum / j;
		monthTM.setAvgValue(avg);
		monthTM.setMaximumValue(max);
		monthTM.setMinimumValue(min);

	}
	private void putMonthHUM( String date, int day, Double hum) {
		HashSet<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("id.channel", new Object[] { 1 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.dst", new Object[] { 0 }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevType",
				new Object[] { DeviceType.Meter }, null, Restriction.EQ));
		condition.add(new Condition("id.yyyymm", new Object[] { date }, null,
				Restriction.EQ));
		condition.add(new Condition("id.mdevId", new Object[] { "10001" },
				null, Restriction.EQ));

		List<MonthHUM> monthHUMList = monthhumDao.findByConditions(condition);

		try {
			if (monthHUMList.size() > 0) {
				MonthHUM monthHUM = monthHUMList.get(0);

				BeanUtils.copyProperty(monthHUM, "value_" + to2Digit(day), hum);
				setMonthHUMAvgMaxMin(monthHUM);
				monthhumDao.update(monthHUM);

			} else {
				MonthHUM monthHUM = new MonthHUM();
				monthHUM.id.setChannel(1);
				monthHUM.id.setYyyymm(date);
				monthHUM.id.setDst(0);
				monthHUM.id.setMDevType(DeviceType.Meter);
				monthHUM.id.setMDevId("10001");

				BeanUtils.copyProperty(monthHUM, "value_" + to2Digit(day), hum);

				setMonthHUMAvgMaxMin(monthHUM);
				monthhumDao.add(monthHUM);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

	}

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
				log.error(e);
			}
		}
		avg = sum / j;
		monthHUM.setAvgValue(avg);
		monthHUM.setMaximumValue(max);
		monthHUM.setMinimumValue(min);

	}

}
