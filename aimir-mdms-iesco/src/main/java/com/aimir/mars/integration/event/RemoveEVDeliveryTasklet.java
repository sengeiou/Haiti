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

public class RemoveEVDeliveryTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(RemoveEVDeliveryTasklet.class);

    protected boolean isDeliverData = false;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("delete ip_ev_delivery table.");
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
            deleteDeliveryData();
        } else {
            throw new Exception("CNF_DELIVERY is 'FALSE'");
        }
        return RepeatStatus.FINISHED;
    }

    private void deleteDeliveryData() {
        int updateCount = jdbcTemplate.update(
                "delete from ip_ev_delivery where rowid in (select rowidfordelete from ip_ev_outbound)");

        if (log.isDebugEnabled()) {
            log.debug("Removed ip_ev_delivery data. count:" + updateCount);
        }
    }
}