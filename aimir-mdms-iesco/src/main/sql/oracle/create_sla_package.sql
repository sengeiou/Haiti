CREATE OR REPLACE PACKAGE AIMIR_SLA AS
  PROCEDURE INSERT_SLATARGET;
  PROCEDURE UPDATE_SLATARGET;
  PROCEDURE INSERT_SLATARGETLOG;
  PROCEDURE EXECUTE_SLARAWDATA;
END;
/

create or replace PACKAGE BODY AIMIR_SLA AS

  PROCEDURE INSERT_SLATARGET AS
  BEGIN
    insert into sla_target(meter_type,meter_id,location_id,devicemodel_id,supplier_id)
    select meter,mds_id,location_id,devicemodel_id,supplier_id
    from meter
    where mds_id not in (select meter_id from sla_target);
  END;

  PROCEDURE UPDATE_SLATARGET AS
  BEGIN
    update (select sla_target.location_id as old, meter.location_id as new 
            from sla_target inner join meter on sla_target.meter_id=meter.mds_id) A
    set a.old=a.new
    where a.old!=a.new or (a.old is null and a.new is not null);

    update (select sla_target.devicemodel_id as old, meter.devicemodel_id as new 
            from sla_target inner join meter on sla_target.meter_id=meter.mds_id) A
    set a.old=a.new
    where a.old!=a.new or (a.old is null and a.new is not null);

    update (select sla_target.supplier_id as old, meter.supplier_id as new 
            from sla_target inner join meter on sla_target.meter_id=meter.mds_id) A
    set a.old=a.new
    where a.old!=a.new or (a.old is null and a.new is not null);

    update sla_target set targetsla=1
    where meter_id in (select meter_id 
                       from (select a.meter_id,sum(a.current_mv_count) as sumcurrentcount,
                                    sum(24*60/b.LP_INTERVAL) as sumexpectcount
                             from sla_rawdata a inner join meter b
                               on a.meter_id=b.mds_id
                             where a.yyyymmdd>=to_char(sysdate-5,'yyyymmdd')
                               and a.yyyymmdd<=to_char(sysdate-1,'yyyymmdd') 
                               and b.mds_id in (select meter_id from sla_target 
                                                    where targetsla is null)
                                                    -- TARGETSLA will use special code. 2: no sla target  ....
                                                    -- where targetsla is null or targetsla != '1')
                               and b.lp_interval is not null
                             group by a.meter_id
                            )
                       where sumcurrentcount>=sumexpectcount
                      );
  END;

  PROCEDURE INSERT_SLATARGETLOG AS
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';
    insert into sla_target_log(yyyymmdd,meter_type,meter_id,location_id,devicemodel_id,supplier_id,expect_mv_count)
    select to_char(current_date,'yyyymmdd'),a.meter_type,a.meter_id,a.location_id,a.devicemodel_id,a.supplier_id,24*60/nvl(lp_interval,60) as expect_count
    from sla_target a
    inner join meter b on a.meter_type=b.meter and a.meter_id=b.mds_id
    where a.targetsla=1 and  meter_id not in (select meter_id from sla_target_log where yyyymmdd=to_char(current_date,'yyyymmdd'));
  END;

  PROCEDURE EXECUTE_SLARAWDATA AS
    stmt_mvbatches VARCHAR2(2000);
    TYPE MVBatchType IS REF CURSOR;
    cursor_mvbatches MVBatchType;
    var_mvbatches GTT_MV_BATCHES%ROWTYPE;
    stmt_slastatrefresh VARCHAR2(2000);
    TYPE SLAStatRefreshType IS REF CURSOR;
    cursor_slastatrefresh SLAStatRefreshType;
    var_slastatrefresh SLA_STAT_REFRESH%ROWTYPE;
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';

    insert into gtt_mv_batches
    select batch_id,batch_status,target_table,sla_date
    from ip_mv_batches
    where batch_id in (
      select batch_id
      from (select * from ip_mv_batches where batch_status=3 and create_date > sysdate-45 order by batch_id)
      where rownum<100
    );

    stmt_mvbatches := 'select batch_id,batch_status,target_table,sla_date from gtt_mv_batches order by batch_id';
    OPEN cursor_mvbatches FOR stmt_mvbatches;
    LOOP
      FETCH cursor_mvbatches INTO var_mvbatches;
        EXIT WHEN cursor_mvbatches%NOTFOUND;

      IF var_mvbatches.target_table = 'EM' THEN

        merge into sla_rawdata t
        using (select mdev_id,yyyymmdd,count(*) as lpcount from ip_mv_outbound_em where batch_id=var_mvbatches.batch_id and channel=1 group by mdev_id,yyyymmdd) s
        on (t.meter_id=s.mdev_id and t.yyyymmdd=s.yyyymmdd)
        when matched then
          update set current_mv_count=current_mv_count+lpcount,last_update_date=current_date
        when not matched then
          insert (yyyymmdd,meter_id,current_mv_count,last_update_date) values(s.yyyymmdd,s.mdev_id,s.lpcount,current_date);

        begin
          insert into sla_stat_refresh(yyyymmdd) select distinct yyyymmdd from ip_mv_outbound_em where batch_id=var_mvbatches.batch_id;
          exception
            when others then
              null;
	      end;

        update ip_mv_data_em a
        set deliver_date = (select b.delivered_date 
                            from ip_mv_batches b
                            where b.batch_id=var_mvbatches.batch_id)
        where deliver_date is null 
          and exists (select 1 from ip_mv_outbound_em d 
                      where d.mdev_type=a.mdev_type and d.mdev_id=a.mdev_id 
                        and d.channel=a.channel and d.yyyymmddhhmmss=a.yyyymmddhhmmss
                        and d.batch_id=var_mvbatches.batch_id);

        delete from ip_mv_outbound_em where batch_id=var_mvbatches.batch_id;

      ELSIF var_mvbatches.target_table = 'WM' THEN

        merge into sla_rawdata t
        using (select mdev_id,yyyymmdd,count(*) as lpcount from ip_mv_outbound_wm where batch_id=var_mvbatches.batch_id and channel=1 group by mdev_id,yyyymmdd) s
        on (t.meter_id=s.mdev_id and t.yyyymmdd=s.yyyymmdd)
        when matched then
          update set current_mv_count=current_mv_count+lpcount,last_update_date=current_date
        when not matched then
          insert (yyyymmdd,meter_id,current_mv_count,last_update_date) values(s.yyyymmdd,s.mdev_id,s.lpcount,current_date);

        begin
          insert into sla_stat_refresh(yyyymmdd) select distinct yyyymmdd from ip_mv_outbound_wm where batch_id=var_mvbatches.batch_id;
          exception
            when others then
              null;
        end;

        update ip_mv_data_wm a
        set deliver_date = (select b.delivered_date 
                            from ip_mv_batches b
                            where b.batch_id=var_mvbatches.batch_id)
        where deliver_date is null 
          and exists (select 1 from ip_mv_outbound_wm d 
                      where d.mdev_type=a.mdev_type and d.mdev_id=a.mdev_id 
                        and d.channel=a.channel and d.yyyymmddhhmmss=a.yyyymmddhhmmss
                        and d.batch_id=var_mvbatches.batch_id);

        delete from ip_mv_outbound_wm where batch_id=var_mvbatches.batch_id;

      ELSIF var_mvbatches.target_table = 'GM' THEN

        merge into sla_rawdata t
        using (select mdev_id,yyyymmdd,count(*) as lpcount from ip_mv_outbound_gm where batch_id=var_mvbatches.batch_id and channel=1 group by mdev_id,yyyymmdd) s
        on (t.meter_id=s.mdev_id and t.yyyymmdd=s.yyyymmdd)
        when matched then
          update set current_mv_count=current_mv_count+lpcount,last_update_date=current_date
        when not matched then
          insert (yyyymmdd,meter_id,current_mv_count,last_update_date) values(s.yyyymmdd,s.mdev_id,s.lpcount,current_date);

        begin
          insert into sla_stat_refresh(yyyymmdd) select distinct yyyymmdd from ip_mv_outbound_gm where batch_id=var_mvbatches.batch_id;
          exception
            when others then
              null;
        end;

        update ip_mv_data_gm a
        set deliver_date = (select b.delivered_date 
                            from ip_mv_batches b
                            where b.batch_id=var_mvbatches.batch_id)
        where deliver_date is null 
          and exists (select 1 from ip_mv_outbound_gm d 
                      where d.mdev_type=a.mdev_type and d.mdev_id=a.mdev_id 
                        and d.channel=a.channel and d.yyyymmddhhmmss=a.yyyymmddhhmmss
                        and d.batch_id=var_mvbatches.batch_id);

        delete from ip_mv_outbound_gm where batch_id=var_mvbatches.batch_id;

      ELSIF var_mvbatches.target_table = 'HM' THEN

        merge into sla_rawdata t
        using (select mdev_id,yyyymmdd,count(*) as lpcount from ip_mv_outbound_hm where batch_id=var_mvbatches.batch_id and channel=1 group by mdev_id,yyyymmdd) s
        on (t.meter_id=s.mdev_id and t.yyyymmdd=s.yyyymmdd)
        when matched then
          update set current_mv_count=current_mv_count+lpcount,last_update_date=current_date
        when not matched then
          insert (yyyymmdd,meter_id,current_mv_count,last_update_date) values(s.yyyymmdd,s.mdev_id,s.lpcount,current_date);
 
        begin
          insert into sla_stat_refresh(yyyymmdd) select distinct yyyymmdd from ip_mv_outbound_hm where batch_id=var_mvbatches.batch_id;
          exception
            when others then
              null;
        end;

        update ip_mv_data_hm a
        set deliver_date = (select b.delivered_date 
                            from ip_mv_batches b
                            where b.batch_id=var_mvbatches.batch_id)
        where deliver_date is null 
          and exists (select 1 from ip_mv_outbound_hm d 
                      where d.mdev_type=a.mdev_type and d.mdev_id=a.mdev_id 
                        and d.channel=a.channel and d.yyyymmddhhmmss=a.yyyymmddhhmmss
                        and d.batch_id=var_mvbatches.batch_id);

        delete from ip_mv_outbound_hm where batch_id=var_mvbatches.batch_id;
      END IF;

      stmt_slastatrefresh := 'select yyyymmdd from sla_stat_refresh';
      open cursor_slastatrefresh for stmt_slastatrefresh;
      LOOP
        FETCH cursor_slastatrefresh INTO var_slastatrefresh;
        EXIT WHEN cursor_slastatrefresh%NOTFOUND;

        IF to_char(current_date,'yyyymmdd')=var_slastatrefresh.yyyymmdd and to_number(to_char(current_date,'hhmmss'))<=60000 THEN
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_6h=sumcurrentcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_6h,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,s.sumcurrentcount,current_date);
        ELSIF to_char(current_date,'yyyymmdd') = var_slastatrefresh.yyyymmdd THEN
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_1d=sumcurrentcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_1d,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,s.sumcurrentcount,current_date);
        ELSIF to_char(current_date-1,'yyyymmdd') = var_slastatrefresh.yyyymmdd THEN
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_2d=sumcurrentcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_2d,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,s.sumcurrentcount,current_date);
        ELSIF to_char(current_date-2,'yyyymmdd') = var_slastatrefresh.yyyymmdd THEN
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_3d=sumcurrentcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_3d,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,s.sumcurrentcount,current_date);
        ELSIF to_char(current_date-3,'yyyymmdd') = var_slastatrefresh.yyyymmdd THEN
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_4d=sumcurrentcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_4d,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,s.sumcurrentcount,current_date);
        ELSIF to_char(current_date-4,'yyyymmdd') = var_slastatrefresh.yyyymmdd THEN
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_5d=sumcurrentcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_5d,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,s.sumcurrentcount,current_date);
        ELSE
          merge into sla_stat t
          using (select a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id,
                        count(a.meter_id) as metercount,sum(a.expect_mv_count) as sumexpectcount,
                        sum(b.current_mv_count) as sumcurrentcount
                 from sla_target_log a
                 left outer join sla_rawdata b
                 on a.meter_id=b.meter_id and a.yyyymmdd=b.yyyymmdd and b.yyyymmdd=var_slastatrefresh.yyyymmdd
                 where a.yyyymmdd=var_slastatrefresh.yyyymmdd
                 group by a.yyyymmdd,a.meter_type,a.location_id,a.devicemodel_id,a.supplier_id) s
          on (t.yyyymmdd=s.yyyymmdd and t.meter_type=s.meter_type and t.location_id=s.location_id and ((t.devicemodel_id is null and s.devicemodel_id is null) or (t.devicemodel_id=s.devicemodel_id)) and t.supplier_id=s.supplier_id)
          when matched then
            update set meter_count=s.metercount,expect_mv_count=s.sumexpectcount,sla_final=sumcurrentcount,sla_last_update_date=current_date
          when not matched then
            insert (yyyymmdd,meter_type,location_id,devicemodel_id,supplier_id,meter_count,expect_mv_count,sla_final,sla_last_update_date)
            values(s.yyyymmdd,s.meter_type,s.location_id,s.devicemodel_id,s.supplier_id,s.metercount,s.sumexpectcount,s.sumcurrentcount,current_date);
        END IF;
      END LOOP;
      close cursor_slastatrefresh;
      delete from sla_stat_refresh;
      update ip_mv_batches set batch_status=4,sla_date=current_date where batch_id=var_mvbatches.batch_id;
      commit work;
    END LOOP;
    
  END;

END;
/



begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'slatarget_refresh',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_sla.insert_slatarget; aimir_sla.update_slatarget; aimir_sla.insert_slatargetlog; end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=30;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Refresh slatarget'
    );
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'slarawdata_refresh',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_sla.execute_slarawdata; end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=5;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'Refresh slarawdata'
    );

 end;
/
