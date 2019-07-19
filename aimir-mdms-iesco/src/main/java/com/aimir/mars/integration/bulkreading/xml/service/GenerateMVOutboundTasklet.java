package com.aimir.mars.integration.bulkreading.xml.service;

import java.math.BigDecimal;
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

public class GenerateMVOutboundTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(GenerateMVOutboundTasklet.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected boolean isDeliverDataEM = false;
    protected boolean isDeliverDataWM = false;
    protected boolean isDeliverDataGM = false;
    protected boolean isDeliverDataHM = false;
    protected int batchMaxSize = 50000;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
            throws Exception {

        log.info("generate ip_mv_outbound_xx table.");

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
            } else if(row.get("ATTRIBUTENAME").toString().toUpperCase().equals("CNF_BATCH_MAXSIZE")) {
                int tempBatchMaxSize = Integer.parseInt(row.get("ATTRIBUTEVALUE").toString());
                if(tempBatchMaxSize > 0) {
                    batchMaxSize = tempBatchMaxSize;
                }
            }
        }
        if (isDeliverDataEM) {
            generateOutboundData("em");
        }
        if (isDeliverDataWM) {
            generateOutboundData("wm");
        }
        if (isDeliverDataGM) {
            generateOutboundData("gm");
        }
        if (isDeliverDataHM) {
            generateOutboundData("hm");
        }

        return RepeatStatus.FINISHED;
    }

    private void generateOutboundData(String tablename) {

        int countLoop = 0;
        Map<String, Object> dataCount = jdbcTemplate.queryForMap("select count(*) as c from ip_mv_delivery_" + tablename + " where location_id is not null");
        long c = ((BigDecimal) dataCount.get("C")).longValue();
        if (c>0) {
            boolean nextRun = true;
            while(nextRun) { 
                Map<String, Object> batchInfo = jdbcTemplate
                        .queryForMap("select ip_mv_batch_sequence.nextval from dual");
                long batch_id = ((BigDecimal)  batchInfo.get("NEXTVAL")).longValue();
        
                int count = jdbcTemplate.update(
                        "insert into ip_mv_outbound_" + tablename + " \n"
                                + "(rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obis_id,mdev_id,mdev_type,mv_value,cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id,location_name,mv_valid) \n"
                                + "select rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obisid,mdev_id,mdev_type,mv_value,cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id,geocode,mv_valid \n"
                                + "from (select a.*,b.obisid,c.geocode \n"
                                + "      from (select * \n"
                                + "            from (select rowid as rowidfordelete,? as batch_id,yyyymmdd,yyyymmddhhmmss, \n"
                                + "                         channel,mdev_id,mdev_type,mv_value,mv_valid, \n"
                                + "                         cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id \n"
                                + "                  from ip_mv_delivery_" + tablename + " \n"
                                + "                  order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss \n"
                                + "                 ) \n"
                                + "            where rownum <= ? \n"
                                + "           ) a \n"
                                + "      inner join ip_mv_channelobis b on a.channel=b.aimirchannel and b.tablename='" + tablename.toUpperCase() + "' \n"
                                + "      inner join location c on a.location_id=c.id \n"
                                + "      order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss \n"
                                + "     ) ",
                        new Object[] { batch_id, batchMaxSize });
    
                log.debug("inserted count : " + count);
    
                jdbcTemplate.update(
                        "insert into ip_mv_batches(batch_id,batch_status,target_table,number_of_rows) values(?,?,?,?)",
                        new Object[] { batch_id, 2, tablename.toUpperCase(), count });

                int updateCount = jdbcTemplate
                        .update("delete from ip_mv_delivery_" + tablename
                                + " where rowid in (select rowidfordelete from ip_mv_outbound_"
                                + tablename + " where batch_id=?)",new Object[] { batch_id });
                if (log.isDebugEnabled()) {
                    log.debug("Removed ip_mv_delivery_" + tablename + " data. count:"
                            + updateCount);
                }

                if(count == batchMaxSize) {
                    nextRun = true;
                } else {
                    nextRun = false;
                }
                countLoop++;
                if(countLoop > 1000) {
                    break;
                }
            }
        }
    }
}
