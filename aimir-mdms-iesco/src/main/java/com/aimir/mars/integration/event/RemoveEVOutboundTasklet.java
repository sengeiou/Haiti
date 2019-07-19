package com.aimir.mars.integration.event;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class RemoveEVOutboundTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(RemoveEVOutboundTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverData = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("delete ip_ev_outbound_xx table.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate.queryForList(
                "select attributename, attributevalue from ip_ev_option where codetype=?",
                "OP");
        for (Map<String, Object> row : ipEvOption) {
            if (row.get("ATTRIBUTENAME").toString().toUpperCase()
                    .equals("CNF_DELIVERY")) {
                isDeliverData = Boolean.parseBoolean(
                        row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }
        if (isDeliverData) {
            deleteOutboundData();
        } else {
            throw new Exception("CNF_DELIVERY is 'FALSE'");
        }

        return RepeatStatus.FINISHED;
    }

    private void deleteOutboundData() {
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_ev_outbound a, ip_ev_batches b where a.batch_id=b.batch_id and b.batch_status=3");
        for (Map<String, Object> batch_id : batch_ids) {
            jdbcTemplate.update("delete from ip_ev_outbound where batch_id=?",
                    new Object[] { batch_id.get("BATCH_ID") });
        }
    }
}
