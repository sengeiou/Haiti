create or replace PACKAGE AIMIR_NM AS 

    NOTNULLEXCEPTION EXCEPTION;
    PRAGMA EXCEPTION_INIT(NOTNULLEXCEPTION, -1400);
    CHECKCONSTRAINT EXCEPTION;
    PRAGMA EXCEPTION_INIT(NOTNULLEXCEPTION, -2290);

    procedure get_options;

    procedure generate_outbound_nm;
    procedure run_nm;

    procedure run_all;
    PROCEDURE INSERT_NM_DATA(
        P_IS_SAVEDATA     VARCHAR2,
        P_YYYYMMDDHHMMSS  VARCHAR2,
        P_CHANNEL         NUMBER,
        P_MDEV_ID         VARCHAR2,
        P_MDEV_TYPE       VARCHAR2,
        P_LOCATION_ID     NUMBER,
        P_MV_VALUE        FLOAT,
        P_MV_VALID        NUMBER,
        P_CAP_DATE_METER  NUMBER,
        P_CAP_DATE_DCU    NUMBER,
        P_CAP_DEVICE_TYPE VARCHAR2,
        P_CAP_DEVICE_ID   VARCHAR2
      );
END AIMIR_NM;
/

create or replace PACKAGE BODY AIMIR_NM AS

  /*
   * History
   * 1.0 
   */

  /*
   * Local variables to store configurations
   */
  VAR_MINIMUM_BATCH_SIZE NUMBER;
  VAR_MAXIMUM_BATCH_SIZE NUMBER;
  VAR_DELIVER_NM VARCHAR2(20);

  PROCEDURE INSERT_NM_DATA(
    P_IS_SAVEDATA IN VARCHAR2,
    P_YYYYMMDDHHMMSS IN VARCHAR2,
    P_CHANNEL IN NUMBER,
    P_MDEV_ID IN VARCHAR2,
    P_MDEV_TYPE IN VARCHAR2,
    P_LOCATION_ID IN NUMBER,
    P_MV_VALUE IN FLOAT,
    P_MV_VALID IN NUMBER,
    P_CAP_DATE_METER IN NUMBER,
    P_CAP_DATE_DCU IN NUMBER,
    P_CAP_DEVICE_TYPE IN VARCHAR2,
    P_CAP_DEVICE_ID IN VARCHAR2
  ) AS
  BEGIN
    IF P_IS_SAVEDATA = 'TRUE' THEN
      BEGIN
          INSERT INTO IP_NM_DATA(YYYYMMDDHHMMSS,CHANNEL,MDEV_ID,MDEV_TYPE,LOCATION_ID,MV_VALUE,MV_VALID,CAP_DATE_METER,CAP_DATE_DCU,CAP_DEVICE_TYPE,CAP_DEVICE_ID)
          VALUES(P_YYYYMMDDHHMMSS,P_CHANNEL,P_MDEV_ID,P_MDEV_TYPE,P_LOCATION_ID,P_MV_VALUE,P_MV_VALID,P_CAP_DATE_METER,P_CAP_DATE_DCU,P_CAP_DEVICE_TYPE,P_CAP_DEVICE_ID);
        EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
          NULL;
      END;
    END IF;
    
      BEGIN
          INSERT INTO IP_NM_DELIVERY(YYYYMMDD,YYYYMMDDHHMMSS,CHANNEL,MDEV_ID,MDEV_TYPE,LOCATION_ID,MV_VALUE,MV_VALID,CAP_DATE_METER,CAP_DATE_DCU,CAP_DEVICE_TYPE,CAP_DEVICE_ID)
          VALUES(SUBSTR(P_YYYYMMDDHHMMSS,0,8),P_YYYYMMDDHHMMSS,P_CHANNEL,P_MDEV_ID,P_MDEV_TYPE,P_LOCATION_ID,P_MV_VALUE,P_MV_VALID,P_CAP_DATE_METER,P_CAP_DATE_DCU,P_CAP_DEVICE_TYPE,P_CAP_DEVICE_ID);

        EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
          NULL;
      END;
    
  END INSERT_NM_DATA;

-- Function: get_option_str
-- Parameters: attributename_ - keyvalue from ip_nm_option
-- Parameters: defaultvalue - defaultvalue if no row can be found in ip_nm_option
-- Returns: value from ip_nm_option with given attributename or defaultvalue if not found
  function get_option_str(attributename_ in ip_nm_option.attributename%type,defaultvalue in ip_nm_option.attributevalue%type default null) return ip_nm_option.attributevalue%type is
    retval ip_nm_option.attributevalue%type;
  begin
    SELECT ATTRIBUTEVALUE 
    INTO retval
    FROM IP_NM_OPTION 
    WHERE ATTRIBUTENAME=attributename_;
    return retval;
  exception
    when others then
      return defaultvalue;
  end get_option_str;

-- Function: get_option_number
-- Parameters: attributename_ - keyvalue from ip_nm_option
-- Parameters: defaultvalue - defaultvalue if no row can be found in ip_nm_option or data retrived is not a string
-- Returns: numeric value from ip_nm_option with given attributename or defaultvalue if not found or not a number
  function get_option_number(attributename_ in ip_nm_option.attributename%type,defaultvalue in number default null) return number is
    retval number;
  begin
    SELECT ATTRIBUTEVALUE 
    INTO retval
    FROM IP_NM_OPTION 
    WHERE ATTRIBUTENAME=attributename_;
    return retval;
  exception
    when others then
      return defaultvalue;
  end get_option_number;

-- Procedure: get_options
-- Parameters: None
-- Sets up all global variables taken from the table ip_nm_option, reasonable defaults are set
-- Returns: None
  procedure get_options as
  begin
    VAR_MINIMUM_BATCH_SIZE:=get_option_number('CNF_BATCH_MINSIZE',0); -- Minimum batch size, default 0. If greater than zero metervalues will be delayed until batch reaches atleast this size
    VAR_MAXIMUM_BATCH_SIZE:=get_option_number('CNF_BATCH_MAXSIZE',10000); -- Maximum batch size
    VAR_DELIVER_NM:=UPPER(get_option_str('CNF_DELIVERY_NM','FALSE'));
  end;

-- Procedure: generate_outbound_nm
-- Generates output batch
-- Returns: None
  procedure generate_outbound_nm as
    batch_id_ ip_nm_batches.batch_id%type;
    delivery_count number;
    sqlerrcode number;
    sqlerrmessage varchar2(2000);
    batch_size number;
    loop_count number;
   begin

    select count(*)
    into delivery_count 
    from ip_nm_delivery where location_id is not null;
    
    IF delivery_count>VAR_MINIMUM_BATCH_SIZE THEN
      loop_count := 1;
      LOOP
        select ip_nm_batch_sequence.nextval 
        into batch_id_
        from dual;

        insert into ip_nm_outbound
        (rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obis_id,mdev_id,mdev_type,mv_value,cap_date_meter,
         cap_date_dcu,cap_device_type,cap_device_id,location_id,location_name,mv_valid)
        select rowidfordelete,batch_id,yyyymmdd,yyyymmddhhmmss,channel,obisid,mdev_id,mdev_type,mv_value,cap_date_meter,
               cap_date_dcu,cap_device_type,cap_device_id,location_id,geocode,mv_valid
        from (select a.*,b.obisid,c.geocode
              from (select * 
                    from (select rowid as rowidfordelete,batch_id_ as batch_id,yyyymmdd,yyyymmddhhmmss,
                                 channel,mdev_id,mdev_type,mv_value,mv_valid, 
                                 cap_date_meter,cap_date_dcu,cap_device_type,cap_device_id,location_id
                          from ip_nm_delivery
                          order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
                         ) 
                    where rownum <= VAR_MAXIMUM_BATCH_SIZE
                   ) a 
              inner join ip_nm_channelobis b on a.channel=b.aimirchannel and b.tablename='NM' 
              inner join location c on a.location_id=c.id 
              order by mdev_type,mdev_id,yyyymmdd,channel,yyyymmddhhmmss 
             );
        batch_size:=sql%rowcount;
        insert into ip_nm_batches(batch_id,batch_status,target_table,number_of_rows) values(batch_id_, 2, 'NM', batch_size);
        delete from ip_nm_delivery where rowid in (select rowidfordelete from ip_nm_outbound where batch_id=batch_id_);
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

  end generate_outbound_nm;

-- Procedure: run_all_nm
-- Parameters: None
-- Processes batchwise from ip_nm_delivery,
-- Repeats making batches as long as there are rows in ip_nm_delivery
-- Returns: None
  procedure run_nm AS
    num_rows number;
    start_timestamp timestamp;
  BEGIN
    generate_outbound_nm;
  END run_nm;

  procedure run_all AS
  BEGIN
    get_options;
    IF VAR_DELIVER_NM = 'TRUE' THEN
      run_nm;
    END IF;
  END run_all;
END AIMIR_NM;
/
-- schedule
begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_generate_outbound_nm',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin aimir_nm.run_all; end;',
       repeat_interval      => 'FREQ=MINUTELY;INTERVAL=2;BYSECOND=10', 
       enabled              =>  TRUE,
       comments             => 'Generate outboundmeter netstation-monitoring value');
end;
/