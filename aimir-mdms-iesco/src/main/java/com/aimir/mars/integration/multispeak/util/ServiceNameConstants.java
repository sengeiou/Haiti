package com.aimir.mars.integration.multispeak.util;

public class ServiceNameConstants {

    public enum ServiceNameMapper
    {
        SetLoadLimitation("Set Load Limitation", "setloadlimitation"),
        SetTariff("Set Tariff", "settariff"),
        SetPaymentMode("Set Payment Mode", "setpaymentmode"),
        SetEnergyToken("Set Energy Token", "setenergytoken"),
        MeterPasswordChange("Meter Password Change", "meterpasswordchange");

        private String clsName; //dlms class name
        private String paramName;

        ServiceNameMapper(String _clsName, String _paramName)
        {
            this.clsName = _clsName;
            this.paramName = _paramName;
        }

        public String getClsName() { return this.clsName; }
        public String getParamName() { return this.paramName; }

        public static String getClassName(String _paramName){
            for (ServiceNameMapper snm : ServiceNameMapper.values()) {
                if(snm.getParamName().equals(_paramName)){
                    return snm.getClsName().toString();
                }
            }
            return "";
        }

        }
}
