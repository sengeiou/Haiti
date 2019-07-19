create or replace procedure datasync as
begin
  update meter set gpioz=0 where gpiox is not null and gpioy is not null and gpioz is null;

  merge into modem t
  using (select meter.id as meterid,meter.mds_id,
                meter.gpiox as agpiox,meter.gpioy as agpioy,meter.gpioz as agpioz,
                modem.id as modemid,modem.device_serial,
                modem.gpiox as bgpiox,modem.gpioy as bgpioy,modem.gpioz as bgpioz
        from meter
        inner join (select r.modem_id,max(r.install_date) as minstalldate from meter r
                    group by r.modem_id) z on meter.modem_id=z.modem_id and meter.install_date=z.minstalldate
        inner join modem on meter.modem_id=modem.id and z.modem_id=modem.id
        where meter.gpiox is not null and meter.gpioy is not null
          and (modem.gpiox is null or modem.gpioy is null or meter.gpiox!=modem.gpiox or meter.gpioy!=modem.gpioy)) s
  on (t.id=s.modemid)
  when matched then
    update set gpiox=s.agpiox,gpioy=s.agpioy,gpioz=s.agpioz;

  merge into modem t
  using (select meter.id as meterid,meter.mds_id,meter.location_id as alocationid,
                modem.id as modemid,modem.device_serial,modem.location_id as blocationid
        from meter
        inner join (select r.modem_id,max(r.install_date) as minstalldate from meter r
                    group by r.modem_id) z on meter.modem_id=z.modem_id and meter.install_date=z.minstalldate
        inner join modem on meter.modem_id=modem.id and z.modem_id=modem.id
        where meter.location_id is not null
          and (modem.location_id is null or meter.location_id!=modem.location_id )) s
  on (t.id=s.modemid)
  when matched then
    update set location_id=s.alocationid;

end;
/

begin
    DBMS_SCHEDULER.CREATE_JOB (
       job_name             => 'process_datasync',
       job_type             => 'PLSQL_BLOCK',
       job_action           => 'begin datasync; end;',
       repeat_interval      => 'FREQ=HOURLY;INTERVAL=2;BYSECOND=17', 
       enabled              =>  TRUE,
       comments             => 'datasync');
end;
/

create or replace trigger MODEM_SIM_CARD_SYNC
before insert or update on MODEM
for each row
declare
begin
  if (:old.ICC_ID is null and :new.ICC_ID is not null) or :old.ICC_ID!=:new.ICC_ID then
    begin
      select msisdn,imsi,imsi into :new.phone_number,:new.sim_number,:new.imsi from siminfo where iccid=:new.ICC_ID and rownum<2;
    exception when others then
      :new.phone_number := '';
      :new.sim_number :='';
      :new.imsi :='';
    end;
  end if;
end;
/

create or replace trigger MCU_SIM_CARD_SYNC
before insert or update on MCU
for each row
declare
begin
  if (:old.ami_network_address is null and :new.ami_network_address is not null) or :old.ami_network_address!=:new.ami_network_address then
    begin
      select msisdn,imsi,iccid into :new.sys_phone_number,:new.sim_number,:new.icc_id from siminfo where ipaddr=:new.ami_network_address and rownum<2;
    exception when others then
      :new.sys_phone_number := '';
      :new.sim_number :='';
      :new.icc_id :='';
    end;
  end if;
end;
/

create or replace trigger temp_mcu01
before insert on MCU
for each row
declare
  supplierid number;
  devicemodelid number;
begin
  select id into supplierid from supplier where name='SORIA';
  select id into devicemodelid from devicemodel where name='NDC-I336';
  if :new.supplier_id is null then
    :new.supplier_id := supplierid;
  end if;
  if :new.devicemodel_id is null then
    :new.devicemodel_id := devicemodelid;
  end if;
end;
/

