package com.aimir.service.mvm;

import java.util.List;

import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.bean.MvmChartViewData;

public interface MvmChartViewManager {

    @Deprecated
    public List<CustomerInfo> getCustomerInfo(String contractNo);

    public List<CustomerInfo> getCustomerInfo(String contractNo, String meterList);

    public List<ChannelInfo> getChannelInfo(String type, String[] meterNos);

    public List<MvmChartViewData> getSearchDataHour(String[] values, String type);

    public List<MvmChartViewData> getSearchDataDay(String[] values, String type);

    public List<MvmChartViewData> getSearchDataMonth(String[] values, String type);

    public List<MvmChartViewData> getSearchDataWeek(String[] values, String type);

    public List<MvmChartViewData> getSearchDataSeason(String[] values, String type);

    public List<MvmChartViewData> getSearchDataDayWeek(String[] values, String type);

    public List<MvmChartViewData> getSearchDataDayOverChart(String[] values, String type);

    public List<MvmChartViewData> getSearchDataMonthOverChart(String[] values, String type);

    public List<MvmChartViewData> getSearchDataWeekOverChart(String[] values, String type);

    public List<MvmChartViewData> getSearchDataDayWeekOverChart(String[] values, String type);

}
