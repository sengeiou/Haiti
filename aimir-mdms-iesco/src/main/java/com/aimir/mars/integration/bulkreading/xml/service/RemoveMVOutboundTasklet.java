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

public class RemoveMVOutboundTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(RemoveMVOutboundTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverDataEM = false;
    protected boolean isDeliverDataWM = false;
    protected boolean isDeliverDataGM = false;
    protected boolean isDeliverDataHM = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("delete ip_mv_outbound_xx table.");

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
            deleteOutboundData("em");
        }
        if (isDeliverDataWM) {
            deleteOutboundData("wm");
        }
        if (isDeliverDataGM) {
            deleteOutboundData("gm");
        }
        if (isDeliverDataHM) {
            deleteOutboundData("hm");
        }

        return RepeatStatus.FINISHED;
    }

    private void deleteOutboundData(String tablename) {
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_mv_outbound_" + tablename
                        + " a, ip_mv_batches b where a.batch_id=b.batch_id and batch_status=3");
        for (Map<String, Object> batch_id : batch_ids) {
            jdbcTemplate.update(
                    "delete from ip_mv_outbound_" + tablename
                            + " where batch_id=?",
                    new Object[] { batch_id.get("BATCH_ID") });
        }
    }
}
