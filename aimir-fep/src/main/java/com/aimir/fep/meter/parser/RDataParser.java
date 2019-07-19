package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;
import java.util.List;

import com.aimir.fep.meter.parser.rdata.BPList;
import com.aimir.fep.meter.parser.rdata.LPList;
import com.aimir.fep.meter.parser.rdata.LogList;
import com.aimir.fep.meter.parser.rdata.MeteringDataRData;

public class RDataParser extends MeterDataParser {
    protected byte[] rawData = null;
    protected Double meteringValue = null;
    
    private MeteringDataRData rdata = null;
    
    @Override
    public byte[] getRawData() {
        return rawData;
    }

    @Override
    public int getLength() {
        if(rawData == null) {
            return 0;
        }

        return rawData.length;
    }

    @Override
    public void parse(byte[] data) throws Exception {
        rdata = new MeteringDataRData();
        // rdata.decode(data, 0, null);
        rawData = data;
        rdata.setPayload(rawData);
        rdata.parsingPayLoad();
    }

    @Override
    public Double getMeteringValue() {
        return meteringValue;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LinkedHashMap<?, ?> getData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFlag() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setFlag(int flag) {
        // TODO Auto-generated method stub
    }

    /**
     * @return the shortId
     */
    public int getShortId() {
        return rdata.getShortId();
    }

    /**
     * @return the channelCount
     */
    public int getChannelCount() {
        return rdata.getChannelCount();
    }

    /**
     * @return the bpCount
     */
    public int getBpCount() {
        return rdata.getBpCount();
    }

    /**
     * @return the bpLists
     */
    public List<BPList> getBpLists() {
        return rdata.getBpLists();
    }

    /**
     * @return the lpCount
     */
    public int getLpCount() {
        return rdata.getLpCount();
    }

    /**
     * @return the lpLists
     */
    public List<LPList> getLpLists() {
        return rdata.getLpLists();
    }

    /**
     * @return the logCategoryCount
     */
    public int getLogCategoryCount() {
        return rdata.getLogCategoryCount();
    }

    /**
     * @return the logCategories
     */
    public List<LogList> getLogCategories() {
        return rdata.getLogCategories();
    }
}
