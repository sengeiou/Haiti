package com.aimir.fep.meter.parser.MX2Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.aimir.fep.meter.file.CSV;
import com.aimir.fep.meter.file.CSV.Finder;
import com.aimir.fep.meter.parser.MX2Table.TOUSeasonChange.Season;
/**
 * TOUCalendar Value Object Builder Class
 * @author kskim
 */
public class TOUCalendarBuilder implements VOBuilder<TOUCalendar> {
	//CSV 에서 필요한 값들에 대한 Key
	static final String KEY_FUTURE_CALENDAR = "Future_Calendar_Setting";
	static final String KEY_PRESENT_CALENDAR = "Present_Calendar_Setting";
	static final String KEY_ACTIVATION_DATE = "Activation_Date";
	static final String KEY_SEASON_CHANGE = "Season_Change";
	static final String KEY_DAY_PATTERN = "Day_Pattern";
	static final String KEY_FR_HOLIDAY = "Fix_Recurring";
	static final String KEY_NR_HOLIDAY = "NonRecurring";
	static final String KEY_CALENDAR_NO = "Calendar_No";
	static final String KEY_SELF_READING = "Selfreading_Activation";
	
	static final String KEY_SEASON1 = "Season_1";
	static final String KEY_SEASON2 = "Season_2";
	static final String KEY_SEASON3 = "Season_3";
	static final String KEY_SEASON4 = "Season_4";
	static final String KEY_DAY_TYPE1 = "Day_Type_1_Weekday";
	static final String KEY_DAY_TYPE2 = "Day_Type_2_Saturday";
	static final String KEY_DAY_TYPE3 = "Day_Type_3_Sunday_and_Holidays";
	
	// Columns Name
	static final String[] COL_SEASON_CHANGE = new String[]{"No","Season","Switch_Date"};
	static final String[] COL_DAY_PATTERN = new String[]{"No","Rate","SwitchTime"};
	static final String[] COL_FR_HOLIDAY = new String[]{"No","Date","Day_type"};
	static final String[] COL_NR_HOLIDAY = new String[]{"No","Date","Year","Day_type"};
	
	static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	static final String DATE_FORMAT_HHMM = "HH:mm";
	static final String DATE_FORMAT_MMMDD = "MMM dd";
	static final String DATE_FORMAT_YYYY = "yyyy";
	static final int[] DAY_TYPE = new int[]{1,2,3};
	static final String[] SELF_READING_ACTIVATION = new String[] {
			"No self-reading", "Self-reading when New Calendar Change",
			"Self-reading when Season Change",
			"Self-reading when Season or New Calendar Change" };
	
	// 한 테이블에 쓸수있는 최대 데이터 세트 개수
	public static final int CNT_HOLIDAY = 20;

	//각 테이블 개수 (한개짜리는 제외)
	public static final int CNT_DAY_PATTERN = 4;
	public static final int CNT_FR_HOLIDAY = 2;
	public static final int CNT_NR_HOLIDAY = 20;
	
	//테이블당 데이터 사이즈
	public static final int LEN_DAY_PATTERN = 90;
	public static final int LEN_ACTIV_DATE = 4;
	public static final int LEN_SEASON_CHANGE = 30;
	public static final int LEN_FR_HOLIDAY = 60;
	public static final int LEN_NR_HOLIDAY = 61;
	public static final int LEN_END_MESSAGE = 3;

	@Override
	/**
	 * @param csv
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public TOUCalendar build(CSV csv) throws Exception{
		//csv 파일 정보를 읽어 TOUCalendar 객체를 생성한다.
		
		TOUCalendar cal = new TOUCalendar();
	
		Finder f = csv.getFinder();
		
		//Activation Date
		cal.setActivationDate(parseActiviationData(f));
		
		//Season Change
		cal.setSeasonChange(parseSeasonChange(f));
		
		//Day Pattern
		cal.setDayPattern(parseDayPattern(f));
		
		//Fixed Recurring Holiday
		cal.setFrHoliday(parseFrHoliday(f));
		
		//Non-recurring Holiday
		cal.setNrHoliday(parseNrHoliday(f));
		
		//End Message
		cal.setEndMessage(parseEndMessage(f));
		
		return cal;
	}

	/**
	 * @param f
	 * @return
	 * @throws Exception 
	 */
	private TOUEndMessage parseEndMessage(Finder f) throws Exception {
		TOUEndMessage endMessage = new TOUEndMessage();
					
		String calendarNo =  f.getValue(KEY_CALENDAR_NO);
		String selfReadingStr = f.getValue(KEY_SELF_READING);
		int selfReading = 0;
		
		for(int i = 0;i< SELF_READING_ACTIVATION.length; i++){
			if(selfReadingStr.equals(SELF_READING_ACTIVATION[i])){
				selfReading = i;
				break;
			}
		}
		
		endMessage.setCalendarNo(calendarNo);
		endMessage.setSelfReading(selfReading);
		
		return endMessage;
	}

	/**
	 * 년도별로 holiday 정보가 등록되며 한 테이블당 하나의 연도만 설정 가능하다, 최대 20개의 테이블 설정 가능.
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<TOUNRHoliday> parseNrHoliday(Finder f) throws ParseException {
		List<TOUNRHoliday> nrHolidays = new ArrayList<TOUNRHoliday>();
		Map<String,TOUNRHoliday> years = new HashMap<String,TOUNRHoliday>();
		
		TOUNRHoliday nrHoliday = null;//new TOUNRHoliday();
		
		List<Map<String, String>> nrData = f.findOf(KEY_NR_HOLIDAY).getData();
		
		if(nrData!=null)
		for(Map<String,String> map : nrData){
			
			String MMdd = dateFormatChange(DATE_FORMAT_MMMDD,"MMdd",map.get(COL_NR_HOLIDAY[1]));
			String yy = dateFormatChange(DATE_FORMAT_YYYY,"yy",map.get(COL_NR_HOLIDAY[2]));
			int dayType = Integer.parseInt(Character.toString((map.get(COL_NR_HOLIDAY[3]).charAt(0))));
			
			
			if(years.containsKey(yy)){
				nrHoliday = years.get(yy);
				if(nrHoliday.getSize()==CNT_HOLIDAY){
					nrHolidays.add(nrHoliday);
					nrHoliday = new TOUNRHoliday();
					years.put(yy, nrHoliday);
				}
			}else{
				nrHoliday = new TOUNRHoliday();
				years.put(yy, nrHoliday);
			}
			
			nrHoliday.addData(dayType, MMdd);
			nrHoliday.setYear(yy);
			
		}
		
		for(String key : years.keySet()){
			nrHolidays.add(years.get(key));
		}
		return nrHolidays;
	}

	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<TOUFRHoliday> parseFrHoliday(Finder f) throws ParseException {
		List<TOUFRHoliday> frHolidays = new ArrayList<TOUFRHoliday>();
		
		TOUFRHoliday frHoliday = new TOUFRHoliday();
		
		List<Map<String, String>> frData = f.findOf(KEY_FR_HOLIDAY).getData();
		
		if(frData!=null)
		for(Map<String,String> map : frData){
			
			if(frHoliday.getSize()==CNT_HOLIDAY){
				frHolidays.add(frHoliday);
				frHoliday = new TOUFRHoliday();
			}
			
			int dayType = Integer.parseInt(Character.toString((map.get(COL_FR_HOLIDAY[2]).charAt(0))));
			String MMdd = dateFormatChange(DATE_FORMAT_MMMDD,"MMdd",map.get(COL_FR_HOLIDAY[1]));
			frHoliday.addData(dayType, MMdd);
			
		}
		frHolidays.add(frHoliday);
		return frHolidays;
	}

	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<TOUDayPattern> parseDayPattern(Finder f) throws ParseException {
		List<TOUDayPattern> dayPatterns = new ArrayList<TOUDayPattern>();
		
		String[] seasonKeys = new String[]{KEY_SEASON1,KEY_SEASON2,KEY_SEASON3,KEY_SEASON4};
		String[] dayTypeKeys = new String[]{KEY_DAY_TYPE1,KEY_DAY_TYPE2,KEY_DAY_TYPE3};
		
		for(String seasonKey : seasonKeys){
			
			TOUDayPattern seasonData = new TOUDayPattern();
			for(int i = 0; i<dayTypeKeys.length; i++){

				List<Map<String, String>> dayData = f.findOf(KEY_DAY_PATTERN).nextOf(seasonKey).nextOf(dayTypeKeys[i]).getData();
				if(dayData!=null)
				for(Map<String, String> sm : dayData){

					int rate = Integer.parseInt(sm.get(COL_DAY_PATTERN[1]));
					String hhmm = dateFormatChange(DATE_FORMAT_HHMM,"HHmm",sm.get(COL_DAY_PATTERN[2]));
					seasonData.addDayType(hhmm, rate, DAY_TYPE[i]);
					
				}
				
			}
			dayPatterns.add(seasonData);
			
		}
		return dayPatterns;
	}

	/**
	 * @param f
	 * @return
	 * @throws ParseException 
	 */
	private List<TOUSeasonChange> parseSeasonChange(Finder f) throws ParseException {
		List<TOUSeasonChange> seasonChanges = new ArrayList<TOUSeasonChange>();
		
		List<Map<String, String>> list = f.findOf(KEY_SEASON_CHANGE).getData();
		
		if(list!=null)
		for(Map<String, String> m : list){
			TOUSeasonChange sc = new TOUSeasonChange();
			String season = m.get(COL_SEASON_CHANGE[1]);
			if(season.equals("Season 1"))
				sc.setSeason(Season.Season1);
			else if(season.equals("Season 2"))
				sc.setSeason(Season.Season2);
			else if(season.equals("Season 3"))
				sc.setSeason(Season.Season3);
			else if(season.equals("Season 4"))
				sc.setSeason(Season.Season4);
			
			String dateStr = m.get(COL_SEASON_CHANGE[2]);
			String mMdd = dateFormatChange(DATE_FORMAT,"MMdd",dateStr);
			sc.setMMdd(mMdd);
			
			seasonChanges.add(sc);
		}
		return seasonChanges;
	}

	/**
	 * @param f
	 * @param type
	 * @throws Exception 
	 */
	private TOUActivationDate parseActiviationData(Finder f) throws Exception {

		int calendarType = 1;
		TOUActivationDate d = new TOUActivationDate();
		String dateStr = null;
		String yyMMdd = null;
		
		if(f.containsLiner(KEY_FUTURE_CALENDAR)){
			calendarType = 1;
		}else if(f.containsLiner(KEY_PRESENT_CALENDAR)){
			calendarType = 0;
		}
		
		try {
			dateStr = f.getValue(KEY_ACTIVATION_DATE);
			yyMMdd = dateFormatChange(DATE_FORMAT,"yyMMdd",dateStr);
		} catch (Exception e) {
			//없을경우 오늘 날짜로 설정
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			SimpleDateFormat sf = new SimpleDateFormat("yyMMdd");
			yyMMdd = sf.format(today);
		}
		
		
		d.setYyMMdd(yyMMdd);
		d.setCalendarType(calendarType);
		return d;
	}


	/**
	 * @param dateFormat
	 * @param toFormat
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private String dateFormatChange(String dateFormat,
			String toFormat, String dateStr) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat(dateFormat,Locale.ENGLISH);
		SimpleDateFormat toSf = new SimpleDateFormat(toFormat);
		
		Date date = sf.parse(dateStr);
		return toSf.format(date);
	}

}
