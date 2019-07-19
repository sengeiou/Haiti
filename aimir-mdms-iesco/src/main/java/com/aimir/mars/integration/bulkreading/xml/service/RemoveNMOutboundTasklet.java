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

public class RemoveNMOutboundTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(RemoveNMOutboundTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverDataNM = false;
    protected boolean isSaveDataNM = false;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("delete ip_nm_outbound table.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_nm_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY_NM")) {
                isDeliverDataNM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
            else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_SAVEDATA_NM")) {
                isSaveDataNM = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            }
        }
        
        if (isSaveDataNM) {
            updateNmData();
        }
        if (isDeliverDataNM) {
            deleteOutboundData();
        }

        return RepeatStatus.FINISHED;
    }

    private void updateNmData() throws Exception  {
        log.info("update ip_nm_data.deliver_date");
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_nm_outbound "
                        + " a, ip_nm_batches b where a.batch_id=b.batch_id and batch_status=3");
        log.debug("size=" + batch_ids.size());

        for (Map<String, Object> batch_id : batch_ids) {
            log.debug("BATCH_ID=" + batch_id.get("BATCH_ID"));
            jdbcTemplate.update("update ip_nm_data a set deliver_date = "
                    + "(select b.delivered_date from ip_nm_batches b "
                    +     " join ip_nm_outbound c on b.batch_id=c.batch_id "
                    +     " where c.mdev_type=a.mdev_type " 
                    +     " and c.mdev_id=a.mdev_id and c.channel=a.channel "
                    +     " and c.yyyymmddhhmmss=a.yyyymmddhhmmss "
                    +     " and b.batch_id= ? )"
                    + "where deliver_date is null "
                    +        " and exists (select 1 from ip_nm_outbound d "
                    +                     " where d.mdev_type=a.mdev_type and d.mdev_id=a.mdev_id " 
                    +                     " and d.channel=a.channel and d.yyyymmddhhmmss=a.yyyymmddhhmmss "
                    +                     " and d.batch_id= ?) ",
                    new Object[] { batch_id.get("BATCH_ID") ,batch_id.get("BATCH_ID")});
            log.debug("update end");
        }
    }
    private void deleteOutboundData() throws Exception  {
        log.info("delete ip_nm_outbound");
        List<Map<String, Object>> batch_ids = jdbcTemplate.queryForList(
                "select distinct b.batch_id from ip_nm_outbound "
                        + " a, ip_nm_batches b where a.batch_id=b.batch_id and batch_status=3");
        for (Map<String, Object> batch_id : batch_ids) {
            jdbcTemplate.update(
                    "delete from ip_nm_outbound "
                            + " where batch_id=?",
                    new Object[] { batch_id.get("BATCH_ID") });
            jdbcTemplate.update(
                    "update ip_nm_batches set batch_status = 4 "
                            + " where batch_id=?",
                    new Object[] { batch_id.get("BATCH_ID") });
        }
    }
}
