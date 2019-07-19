var DateType = new Object();
DateType.HOURLY     	= "0";	//시간별
DateType.DAILY      	= "1";	//일별
DateType.PERIOD     	= "2";	//기간별
DateType.WEEKLY     	= "3";	//주별
DateType.MONTHLY    	= "4";	//월별
DateType.MONTHLYPERIOD  = "5";	//월별
DateType.WEEKDAILY  	= "6";	//요일별
DateType.SEASONAL   	= "7";	//계절별
DateType.YEARLY   		= "8";	//년도별
DateType.QUARTERLY		= "9";  //분기별

var MeterType = new Object();
MeterType.EM    = "EnergyMeter";
MeterType.GM    = "GasMeter";
MeterType.WM	= "WaterMeter";
MeterType.HM	= "HeatMeter";
MeterType.VC	= "VolumeCorrector";

var RankingType = new Object();
RankingType.ZERO	= "0";
RankingType.BEST    = "1";
RankingType.WORST   = "2";

var Season = new Object();
Season.SPRING = "Spring";
Season.SUMMER = "Summer";
Season.AUTUMN = "Autumn";
Season.WINTER = "Winter";

var ContractStatus = new Object();
ContractStatus.NORMAL = "2.1.0";	// 정상
ContractStatus.PAUSE  = "2.1.1";	// 휴지
ContractStatus.STOP   = "2.1.2";	// 정지
ContractStatus.CANCEL = "2.1.3";	// 해지

var ServiceType = new Object();
ServiceType.Electricity = "3.1";
ServiceType.Gas         = "3.2";
ServiceType.Water       = "3.3";
ServiceType.Heat        = "3.4";
ServiceType.VolumeCorrector = "3.5";

var DeviceType = new Object();
DeviceType.MCU 		   = "0";
DeviceType.MODEM       = "1";
DeviceType.METER       = "2";
DeviceType.ENDDEVICE   = "3";

var SupplierType = new Object();
SupplierType.Electricity     = "Electricity";
SupplierType.Gas             = "Gas";
SupplierType.Water	         = "Water";
SupplierType.Heat	         = "Heat";
SupplierType.VolumeCorrector = "VolumeCorrector";

var VEEType = new Object();
VEEType.ValidateCheck	= "0";
VEEType.History			= "1";
VEEType.Parameters		= "2";

var signType = new Object();
signType.bigger	 = ">";
signType.equal	 = "=";
signType.smaller = "<";

var TypeView = new Object();
TypeView.Voltage 		 = "1";
TypeView.Current 		 = "2";
TypeView.VoltageAngle    = "3";
TypeView.CurrentAngle    = "4";
TypeView.VoltageTHD		 = "5";
TypeView.CurrentTHD 	 = "6";
TypeView.TDD 		     = "7";
TypeView.PF 		     = "8";
TypeView.DistortionPF 	 = "9";
TypeView.KW 		     = "10";
TypeView.KVAR 		     = "11";
TypeView.KVA 		     = "12";
TypeView.DistortionKVA 	 = "13";
TypeView.vol_1st_harmonic_mag  = "14";
TypeView.vol_2nd_harmonic_mag  = "15";
TypeView.curr_1st_harmonic_mag = "16";
TypeView.curr_2nd_harmonic_mag = "17";
TypeView.vol_2nd_harmonic      = "18";
TypeView.CurrentHarmonic       = "19";
TypeView.ph_fund_vol  = "20";
TypeView.ph_vol_pqm   = "21";
TypeView.ph_fund_curr = "22";
TypeView.ph_curr_pqm  = "23";


var GroupType = new Object();
GroupType.Location  = "Location";
GroupType.Operator  = "Operator";
GroupType.Contract  = "Contract";
GroupType.MCU       = "MCU";
GroupType.Modem     = "Modem";
GroupType.Meter     = "Meter";
GroupType.EndDevice = "EndDevice";     


// Load Management
var LoadMgmtView = new Object();
LoadMgmtView.MemberList 	= "1";
LoadMgmtView.LoadControl 	= "2";
LoadMgmtView.LoadLimit		= "3";
LoadMgmtView.LoadShed		= "4";
LoadMgmtView.TraceLog		= "5";

// TOU Report Excel partition unit
var ReportPartition = new Object();
ReportPartition.Default = "";
ReportPartition.Value1  = "30";
ReportPartition.Value2  = "50";
ReportPartition.Value3  = "100";
ReportPartition.Value4  = "200";

//Occur Frequency Daily
var OccurFreqDaily = new Object();
OccurFreqDaily.Default = "12";
OccurFreqDaily.Value1  = "24";
OccurFreqDaily.Value2  = "12";
OccurFreqDaily.Value3  = "6";
OccurFreqDaily.Value4  = "3";

//Occur Frequency Weekly
var OccurFreqWeekly = new Object();
OccurFreqWeekly.Default = "4";
OccurFreqWeekly.Value1  = "7";
OccurFreqWeekly.Value2  = "6";
OccurFreqWeekly.Value3  = "5";
OccurFreqWeekly.Value4  = "4";
OccurFreqWeekly.Value5  = "3";
OccurFreqWeekly.Value6  = "2";
OccurFreqWeekly.Value7  = "1";

//Occur Frequency Monthly
var OccurFreqMonthly = new Object();
OccurFreqMonthly.Default = "15";
OccurFreqMonthly.Value1  = "30";
OccurFreqMonthly.Value2  = "20";
OccurFreqMonthly.Value3  = "15";
OccurFreqMonthly.Value4  = "10";

//Occur Frequency Seasonal
var OccurFreqSeasonal = new Object();
OccurFreqSeasonal.Default = "30";
OccurFreqSeasonal.Value1  = "90";
OccurFreqSeasonal.Value2  = "60";
OccurFreqSeasonal.Value3  = "30";
OccurFreqSeasonal.Value4  = "15";

//MeteringData Detail Rate Tab
var DateTabOther = new Object();
DateTabOther.RATE = "20";
DateTabOther.INTERVAL = "21";

var PeakType = new Object();
PeakType.OFF_PEAK = "OFF_PEAK";
PeakType.PEAK = "PEAK";
PeakType.CRITICAL_PEAK = "CRITICAL_PEAK";

/*4-Code_en.xml 기초데이터 참조 */
var Meterstatus = new Object();
Meterstatus.NORMAL = "1.3.3.1";	// 정상
Meterstatus.BREAK_DOWN  = "1.3.3.2";	// 고장
Meterstatus.REPAIR   = "1.3.3.3";	// 수리중
Meterstatus.CUT_OFF = "1.3.3.4";	// 단전(단수)
Meterstatus.POWER_DOWN = "1.3.3.5";	// 정전
Meterstatus.NEW_REGISTERED = "1.3.3.8";	// 신규등록
Meterstatus.DELETE = "1.3.3.9";	// 철거
Meterstatus.ACTIVATION = "1.3.3.10";	// 활성
Meterstatus.DEACTIVATION = "1.3.3.11";	// 비활성