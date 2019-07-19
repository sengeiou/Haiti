package com.aimir.mars.integration.event;

import java.math.BigDecimal;
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

public class GenerateEVOutboundTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(GenerateEVOutboundTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverData = false;
    protected int batchMaxSize = 50000;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {
        log.info("generate ip_ev_outbound table.");

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_ev_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_DELIVERY")) {
                isDeliverData = Boolean.parseBoolean(row.get("ATTRIBUTEVALUE").toString().toLowerCase());
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_BATCH_MAXSIZE")) {
                int tempBatchMaxSize = Integer.parseInt(row.get("ATTRIBUTEVALUE").toString());
                if(tempBatchMaxSize > 0) {
                    batchMaxSize = tempBatchMaxSize;
                }
            }
        }
        if (isDeliverData) {
            generateOutboundDataMeterEvent();
            generateOutboundDataAlert();
        } else {
            throw new Exception("CNF_DELIVERY is 'FALSE'");
        }
        return RepeatStatus.FINISHED;
    }

    private void generateOutboundDataMeterEvent() {
        Map<String, Object> dataCount = jdbcTemplate.queryForMap("select count(*) as c from ip_ev_delivery where evtype='M'");
        long c = ((BigDecimal) dataCount.get("C")).longValue();
        if (c>0) {
            boolean nextRun = true;
            while(nextRun) { 
                Map<String, Object> batchInfo = jdbcTemplate
                        .queryForMap("select ip_ev_batch_sequence.nextval from dual");
                long batch_id = ((BigDecimal) batchInfo.get("NEXTVAL")).longValue();

                int count = jdbcTemplate.update(
                        "insert into ip_ev_outbound \n"
                                + "(rowidfordelete,batch_id,evtype,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_NAME,WRITETIME,YYYYMMDD) \n"
                                + "select A.rowid,?,A.EVTYPE,A.ACTIVATOR_ID,A.METEREVENT_ID,A.EVENTOBIS_ID,A.OPEN_TIME,A.ACTIVATOR_TYPE,A.INTEGRATED,A.MESSAGE,A.SUPPLIER_ID,B.GEOCODE,A.WRITETIME,A.YYYYMMDD \n"
                                + "from ip_ev_delivery A \n"
                                + "inner join location B on A.LOCATION_ID=B.id \n"
                                + "where rownum <= ? and A.evtype='M' ",
                        new Object[] { batch_id, batchMaxSize });
        
                jdbcTemplate.update(
                        "insert into ip_ev_batches(batch_id,batch_status,number_of_rows) values(?,?,?)",
                        new Object[] { batch_id, 2, count });
                int updateCount = jdbcTemplate
                        .update("delete from ip_ev_delivery where rowid in (select rowidfordelete from ip_ev_outbound where batch_id=?)", new Object[] { batch_id});

                if (log.isDebugEnabled()) {
                    log.debug("Removed ip_ev_delivery data. count:"
                            + updateCount);
                }
                if(count == batchMaxSize) {
                    nextRun = true;
                } else {
                    nextRun = false;
                }
            }
        }
    }
    
    private void generateOutboundDataAlert() {
        Map<String, Object> dataCount = jdbcTemplate.queryForMap("select count(*) as c from ip_ev_delivery where evtype='A'");
        long c = ((BigDecimal) dataCount.get("C")).longValue();
        if (c>0) {
            boolean nextRun = true;
            while(nextRun) { 
                Map<String, Object> batchInfo = jdbcTemplate
                        .queryForMap("select ip_ev_batch_sequence.nextval from dual");
                long batch_id = ((BigDecimal) batchInfo.get("NEXTVAL")).longValue();

                int count = jdbcTemplate.update(
                        "insert into ip_ev_outbound \n"
                                + "(rowidfordelete,batch_id,evtype,ACTIVATOR_ID,METEREVENT_ID,EVENTOBIS_ID,OPEN_TIME,ACTIVATOR_TYPE,INTEGRATED,MESSAGE,SUPPLIER_ID,LOCATION_NAME,WRITETIME,YYYYMMDD) \n"
                                + "select A.rowid,?,A.EVTYPE,A.ACTIVATOR_ID,A.METEREVENT_ID,A.EVENTOBIS_ID,A.OPEN_TIME,A.ACTIVATOR_TYPE,A.INTEGRATED,A.MESSAGE,A.SUPPLIER_ID,B.GEOCODE,A.WRITETIME,A.YYYYMMDD \n"
                                + "from ip_ev_delivery A \n"
                                + "inner join location B on A.LOCATION_ID=B.id \n"
                                + "where rownum <= ? and A.evtype='A' ",
                        new Object[] { batch_id, batchMaxSize });
        
                jdbcTemplate.update(
                        "insert into ip_ev_batches(batch_id,batch_status,number_of_rows) values(?,?,?)",
                        new Object[] { batch_id, 2, count });
                int updateCount = jdbcTemplate
                        .update("delete from ip_ev_delivery where rowid in (select rowidfordelete from ip_ev_outbound where batch_id=?)", new Object[] { batch_id});

                if (log.isDebugEnabled()) {
                    log.debug("Removed ip_ev_delivery data. count:"
                            + updateCount);
                }
                if(count == batchMaxSize) {
                    nextRun = true;
                } else {
                    nextRun = false;
                }
            }
        }
    }
}
