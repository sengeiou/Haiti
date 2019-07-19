create or replace PACKAGE AIMIR_MV AS 

    NOTNULLEXCEPTION EXCEPTION;
    PRAGMA EXCEPTION_INIT(NOTNULLEXCEPTION, -1400);
    CHECKCONSTRAINT EXCEPTION;
    PRAGMA EXCEPTION_INIT(NOTNULLEXCEPTION, -2290);

    procedure get_options;

    procedure generate_outbound_em;
    procedure run_em;

    procedure generate_outbound_hm;
    procedure run_hm;

    procedure generate_outbound_gm;
    procedure run_gm;

    procedure generate_outbound_wm;
    procedure run_wm;

    procedure run_all;

END AIMIR_MV;
/

create or replace PACKAGE BODY AIMIR_MV AS

  /*
   * History
   * 1.0 
   */

  /*
   * Local variables to store configurations
   */
  VAR_MINIMUM_BATCH_SIZE NUMBER;
  VAR_MAXIMUM_BATCH_SIZE NUMBER;
  VAR_DELIVER_EM VARCHAR2(20);
  VAR_DELIVER_WM VARCHAR2(20);
  VAR_DELIVER_GM VARCHAR2(20);
  VAR_DELIVER_HM VARCHAR2(20);

-- Function: get_option_str
-- Parameters: attributename_ - keyvalue from ip_mv_option
-- Parameters: defaultvalue - defaultvalue if no row can be found in ip_mv_option
-- Returns: value from ip_mv_option with given attributename or defaultvalue if not found
  function get_option_str(attributename_ in ip_mv_option.attributename%type,defaultvalue in ip_mv_option.attributevalue%type default null) return ip_mv_option.attributevalue%type is
    retval ip_mv_option.attributevalue%type;
  begin
    SELECT ATTRIBUTEVALUE 
    INTO retval
    FROM IP_MV_OPTION 
    WHERE ATTRIBUTENAME=attributename_;
    return retval;
  exception
    when others then
      return defaultvalue;
  end get_option_str;

-- Function: get_option_number
-- Parameters: attributename_ - keyvalue from ip_mv_option
-- Parameters: defaultvalue - defaultvalue if no row can be found in ip_mv_option or data retrived is not a string
-- Returns: numeric value from ip_mv_option with given attributename or defaultvalue if not found or not a number
  function get_option_number(attributename_ in ip_mv_option.attributename%type,defaultvalue in number default null) return number is
    retval number;
  begin
    SELECT ATTRIBUTEVALUE 
    INTO retval
    FROM IP_MV_OPTION 
    WHERE ATTRIBUTENAME=attributename_;
    return retval;
  exception
    when others then
      return defaultvalue;
  end get_option_number;

-- Procedure: get_options
-- Parameters: None
-- Sets up all global variables taken from the table ip_mv_option, reasonable defaults are set
-- Returns: None
  procedure get_options as
  begin
    VAR_MINIMUM_BATCH_SIZE:=get_option_number('CNF_BATCH_MINSIZE',0); -- Minimum batch size, default 0. If greater than zero metervalues will be delayed until batch reaches atleast this size
    VAR_MAXIMUM_BATCH_SIZE:=get_option_number('CNF_BATCH_MAXSIZE',10000); -- Maximum batch size
    VAR_DELIVER_EM:=UPPER(get_option_str('CNF_DELIVERY_EM','FALSE'));
    VAR_DELIVER_WM:=UPPER(get_option_str('CNF_DELIVERY_WM','FALSE'));
    VAR_DELIVER_GM:=UPPER(get_option_str('CNF_DELIVERY_GM','FALSE'));
    VAR_DELIVER_HM:=UPPER(get_option_str('CNF_DELIVERY_HM','FALSE'));
  end;

-- Procedure: generate_outbound_em
-- Generates output batch
-- Returns: None
  procedure generate_outbound_em as
    batch_id_ ip_mv_batches.batch_id%type;
    delivery_count number;
    sqlerrcode number;
    sqlerrmessage varchar2(2000);
    batch_size number;
    loop_count number;
   begin

    select count(*)
    into delivery_count 
    from ip_mv_delivery_em where location_id is not null;
    
    IF delivery_count>VAR_MINIMUM_BATCH_SIZE THEN
      loop_count := 1;
      LOOP
        select ip_mv_batch_sequence.nextval 
        into batch_id_
        from dual;

        insert into ip_mv_outbound_em
        (rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obis_id,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
         cap_date_dcu,cap_device_type,cap_device_id,location_id,location_name,mv_valid,lpcreatedtime)
        select rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obisid,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
               cap_date_dcu,cap_device_type,cap_device_id,location_id,geocode,mv_valid,lpcreatedtime
        from (select a.*,b.obisid,c.geocode
              from (select * 
                    from (select rowid as rowidfordelete,batch_id_ as batch_id,yyyymmdd,yyyymmddhhmmss,
                                 channel,mdev_id,mdev_type,mv_value,mv_valid,mv_status, 
                                 cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id,lpcreatedtime
                          from ip_mv_delivery_em
                          order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
                         ) 
                    where rownum <= VAR_MAXIMUM_BATCH_SIZE
                   ) a 
              inner join ip_mv_channelobis b on a.channel=b.aimirchannel and b.tablename='EM' 
              inner join location c on a.location_id=c.id 
              order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
             );
        batch_size:=sql%rowcount;
        insert into ip_mv_batches(batch_id,batch_status,target_table,number_of_rows) values(batch_id_, 2, 'EM', batch_size);
        delete from ip_mv_delivery_em where rowid in (select rowidfordelete from ip_mv_outbound_em where batch_id=batch_id_);
        commit work;
      
        IF batch_size < VAR_MAXIMUM_BATCH_SIZE THEN
          EXIT;
        END IF;
        loop_count := loop_count + 1;
        IF loop_count > 1000 THEN
          EXIT;
        END IF;

      END LOOP;
    END IF;

  end generate_outbound_em;

-- Procedure: run_all_em
-- Parameters: None
-- Processes batchwise from ip_mv_em,
-- Repeats making batches as long as there are rows in ip_mv_em
-- Returns: None
  procedure run_em AS
    num_rows number;
    start_timestamp timestamp;
  BEGIN
    generate_outbound_em;
  END run_em;

-- Procedure: generate_outbound_wm
-- Generates output batch
-- Returns: None
  procedure generate_outbound_wm as
    batch_id_ ip_mv_batches.batch_id%type;
    delivery_count number;
    sqlerrcode number;
    sqlerrmessage varchar2(2000);
    batch_size number;
    loop_count number;
   begin

    select count(*)
    into delivery_count 
    from ip_mv_delivery_wm where location_id is not null;
    
    IF delivery_count>VAR_MINIMUM_BATCH_SIZE THEN
      loop_count := 1;
      LOOP
        select ip_mv_batch_sequence.nextval 
        into batch_id_
        from dual;

        insert into ip_mv_outbound_wm
        (rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obis_id,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
         cap_date_dcu,cap_device_type,cap_device_id,location_id,location_name,mv_valid,lpcreatedtime)
        select rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obisid,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
               cap_date_dcu,cap_device_type,cap_device_id,location_id,geocode,mv_valid,lpcreatedtime
        from (select a.*,b.obisid,c.geocode
              from (select * 
                    from (select rowid as rowidfordelete,batch_id_ as batch_id,yyyymmdd,yyyymmddhhmmss,
                                 channel,mdev_id,mdev_type,mv_value,mv_valid,mv_status, 
                                 cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id,lpcreatedtime
                          from ip_mv_delivery_wm
                          order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
                         ) 
                    where rownum <= VAR_MAXIMUM_BATCH_SIZE
                   ) a 
              inner join ip_mv_channelobis b on a.channel=b.aimirchannel and b.tablename='WM' 
              inner join location c on a.location_id=c.id 
              order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
             );
        batch_size:=sql%rowcount;
        insert into ip_mv_batches(batch_id,batch_status,target_table,number_of_rows) values(batch_id_, 2, 'WM', batch_size);
        delete from ip_mv_delivery_wm where rowid in (select rowidfordelete from ip_mv_outbound_wm where batch_id=batch_id_);
        commit work;
      
        IF batch_size < VAR_MAXIMUM_BATCH_SIZE THEN
          EXIT;
        END IF;
        loop_count := loop_count + 1;
        IF loop_count < 1000 THEN
          EXIT;
        END IF;

      END LOOP;
    END IF;

  end generate_outbound_wm;

-- Procedure: run_all_wm
-- Parameters: None
-- Processes batchwise from ip_mv_wm,
-- Repeats making batches as long as there are rows in ip_mv_wm
-- Returns: None
  procedure run_wm AS
    num_rows number;
    start_timestamp timestamp;
  BEGIN
    generate_outbound_wm;
  END run_wm;

-- Procedure: generate_outbound_gm
-- Generates output batch
-- Returns: None
  procedure generate_outbound_gm as
    batch_id_ ip_mv_batches.batch_id%type;
    delivery_count number;
    sqlerrcode number;
    sqlerrmessage varchar2(2000);
    batch_size number;
    loop_count number;
   begin

    select count(*)
    into delivery_count 
    from ip_mv_delivery_gm where location_id is not null;
    
    IF delivery_count>VAR_MINIMUM_BATCH_SIZE THEN
      loop_count := 1;
      LOOP
        select ip_mv_batch_sequence.nextval 
        into batch_id_
        from dual;

        insert into ip_mv_outbound_gm
        (rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obis_id,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
         cap_date_dcu,cap_device_type,cap_device_id,location_id,location_name,mv_valid,lpcreatedtime)
        select rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obisid,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
               cap_date_dcu,cap_device_type,cap_device_id,location_id,geocode,mv_valid,lpcreatedtime
        from (select a.*,b.obisid,c.geocode
              from (select * 
                    from (select rowid as rowidfordelete,batch_id_ as batch_id,yyyymmdd,yyyymmddhhmmss,
                                 channel,mdev_id,mdev_type,mv_value,mv_valid,mv_status,
                                 cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id,lpcreatedtime
                          from ip_mv_delivery_gm
                          order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
                         ) 
                    where rownum <= VAR_MAXIMUM_BATCH_SIZE
                   ) a 
              inner join ip_mv_channelobis b on a.channel=b.aimirchannel and b.tablename='GM' 
              inner join location c on a.location_id=c.id 
              order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
             );
        batch_size:=sql%rowcount;
        insert into ip_mv_batches(batch_id,batch_status,target_table,number_of_rows) values(batch_id_, 2, 'GM', batch_size);
        delete from ip_mv_delivery_gm where rowid in (select rowidfordelete from ip_mv_outbound_gm where batch_id=batch_id_);
        commit work;
      
        IF batch_size < VAR_MAXIMUM_BATCH_SIZE THEN
          EXIT;
        END IF;
        loop_count := loop_count + 1;
        IF loop_count < 1000 THEN
          EXIT;
        END IF;

      END LOOP;
    END IF;

  end generate_outbound_gm;

-- Procedure: run_all_gm
-- Parameters: None
-- Processes batchwise from ip_mv_gm,
-- Repeats making batches as long as there are rows in ip_mv_gm
-- Returns: None
  procedure run_gm AS
    num_rows number;
    start_timestamp timestamp;
  BEGIN
    generate_outbound_gm;
  END run_gm;

-- Procedure: generate_outbound_hm
-- Generates output batch
-- Returns: None
  procedure generate_outbound_hm as
    batch_id_ ip_mv_batches.batch_id%type;
    delivery_count number;
    sqlerrcode number;
    sqlerrmessage varchar2(2000);
    batch_size number;
    loop_count number;
   begin

    select count(*)
    into delivery_count 
    from ip_mv_delivery_hm where location_id is not null;
    
    IF delivery_count>VAR_MINIMUM_BATCH_SIZE THEN
      loop_count := 1;
      LOOP
        select ip_mv_batch_sequence.nextval 
        into batch_id_
        from dual;

        insert into ip_mv_outbound_hm
        (rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obis_id,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
         cap_date_dcu,cap_device_type,cap_device_id,location_id,location_name,mv_valid,lpcreatedtime)
        select rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obisid,mdev_id,mdev_type,mv_value,mv_status,cap_date_meter,
               cap_date_dcu,cap_device_type,cap_device_id,location_id,geocode,mv_valid,lpcreatedtime
        from (select a.*,b.obisid,c.geocode
              from (select * 
                    from (select rowid as rowidfordelete,batch_id_ as batch_id,yyyymmdd,yyyymmddhhmmss,
                                 channel,mdev_id,mdev_type,mv_value,mv_valid,mv_status, 
                                 cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id,lpcreatedtime
                          from ip_mv_delivery_hm
                          order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
                         ) 
                    where rownum <= VAR_MAXIMUM_BATCH_SIZE
                   ) a 
              inner join ip_mv_channelobis b on a.channel=b.aimirchannel and b.tablename='HM' 
              inner join location c on a.location_id=c.id 
              order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
             );
        batch_size:=sql%rowcount;
        insert into ip_mv_batches(batch_id,batch_status,target_table,number_of_rows) values(batch_id_, 2, 'HM', batch_size);
        delete from ip_mv_delivery_hm where rowid in (select rowidfordelete from ip_mv_outbound_hm where batch_id=batch_id_);
        commit work;

        IF batch_size < VAR_MAXIMUM_BATCH_SIZE THEN
          EXIT;
        END IF;
        loop_count := loop_count + 1;
        IF loop_count < 1000 THEN
          EXIT;
        END IF;

      END LOOP;
    END IF;

  end generate_outbound_hm;

-- Procedure: run_all_hm
-- Parameters: None
-- Processes batchwise from ip_mv_hm,
-- Repeats making batches as long as there are rows in ip_mv_hm
-- Returns: None
  procedure run_hm AS
    num_rows number;
    start_timestamp timestamp;
  BEGIN
    generate_outbound_hm;
  END run_hm;

  procedure run_all AS
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''+00:00''';
    get_options;
    IF VAR_DELIVER_EM = 'TRUE' THEN
      run_em;
    END IF;
    IF VAR_DELIVER_HM = 'TRUE' THEN
      run_hm;
    END IF;
    IF VAR_DELIVER_WM = 'TRUE' THEN
      run_wm;
    END IF;
    IF VAR_DELIVER_GM = 'TRUE' THEN
      run_gm;
    END IF;

  END run_all;
END AIMIR_MV;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'generate outbound data',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_mv.run_all; end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=5', 
       enabled              =>  TRUE,
       comments             => 'generate outbound data'
    );

 end;
/


