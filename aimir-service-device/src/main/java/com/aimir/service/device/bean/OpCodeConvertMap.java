package com.aimir.service.device.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nuri - 2014. 7. 8.
 * 
 */
public class OpCodeConvertMap {
    private Map<String, ConvOper> operationMap;

    private Map<String, ConvOper> getMap() {
        if (operationMap == null) {
            operationMap = new HashMap<String, ConvOper>();
        }
        return operationMap;
    }

    public void addOperation(String opCode, ConvOper op) {
        getMap().put(opCode, op);
    }

    public ConvOper getOperation(String opCode) {
        return getMap().get(opCode);
    }
    
    
    public static class ConvOper {
        private String code;
        private String name;
        private String btext;
        private List<Saver> savers;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBtext() {
            return btext;
        }

        public void setBtext(String btext) {
            this.btext = btext;
        }

        public List<Saver> getSavers() {
            return savers;
        }

        public void setSavers(List<Saver> savers) {
            this.savers = savers;
        }
    }
    
    public static class Saver {
        private String name;
        private String operationMsg;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOperationMsg() {
            return operationMsg;
        }

        public void setOperationMsg(String opMsg) {
            this.operationMsg = opMsg;
        }
    }
}



