package com.aimir.mars.integration.bulkreading.xml.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class RemoveMVDeliveryTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(RemoveMVDeliveryTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverDataEM = false;
    protected boolean isDeliverDataWM = false;
    protected boolean isDeliverDataGM = false;
    protected boolean isDeliverDataHM = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("delete ip_mv_delivery_xx table.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_mv_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_EM")) {
                isDeliverDataEM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_WM")) {
                    isDeliverDataWM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_GM")) {
                isDeliverDataGM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_HM")) {
                isDeliverDataHM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }

        if (isDeliverDataEM) {
            deleteDeliveryData("em");
        }
        if (isDeliverDataWM) {
            deleteDeliveryData("wm");
        }
        if (isDeliverDataGM) {
            deleteDeliveryData("gm");
        }
        if (isDeliverDataHM) {
            deleteDeliveryData("hm");
        }

        return RepeatStatus.FINISHED;
    }

    private void deleteDeliveryData(String tablename) {
        int updateCount = jdbcTemplate
                .update("delete from ip_mv_delivery_" + tablename
                        + " where location_id is null");

        updateCount = jdbcTemplate
                .update("delete from ip_mv_delivery_" + tablename + " a \n"
                      + "where exists (select 1 from ip_mv_outbound_" + tablename + " b \n"
                      + "where a.mdev_type=b.mdev_type and a.mdev_id=b.mdev_id and a.channel=b.channel \n"
                      + "  and substr(a.yyyymmddhhmmss,0,8)=b.yyyymmdd and a.yyyymmddhhmmss=b.yyyymmddhhmmss)");

        if (log.isDebugEnabled()) {
            log.debug("Removed ip_mv_delivery_" + tablename + " data. count:"
                    + updateCount);
        }
    }
}