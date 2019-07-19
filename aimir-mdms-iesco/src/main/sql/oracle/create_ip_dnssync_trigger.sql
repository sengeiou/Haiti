create or replace trigger IP_DNSSYNC_MCU
after insert or update or delete on MCU
for each row
declare
  var_input_type varchar2(10);
begin
  var_input_type := null;

  if inserting then
    var_input_type := 'INSERT';
  elsif updating then
    var_input_type := 'UPDATE';
  else
    var_input_type := 'DELETE';
  end if;

  if inserting or updating then
    if (:old.IP_ADDR is null and :new.IP_ADDR is not null) or (:new.IP_ADDR is not null and :old.IP_ADDR != :new.IP_ADDR) then
      insert into IP_DNSSYNC_LOG(logtype, domainname, ipv4) values(var_input_type, :new.SYS_ID||'.dcu.aimir.int', :new.IP_ADDR);
    end if;
    if (:old.AMI_NETWORK_ADDRESS is null and :new.AMI_NETWORK_ADDRESS is not null) or (:new.AMI_NETWORK_ADDRESS is not null and :old.AMI_NETWORK_ADDRESS != :new.AMI_NETWORK_ADDRESS) then
      insert into IP_DNSSYNC_LOG(logtype, domainname, ipv4) values(var_input_type, :new.SYS_ID||'.dcu.aimir.int', :new.AMI_NETWORK_ADDRESS);
    end if;
    if (:old.IPV6_ADDR is null and :new.IPV6_ADDR is not null) or (:new.IPV6_ADDR is not null and :old.IPV6_ADDR != :new.IPV6_ADDR) then
      insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, :new.SYS_ID||'.dcu.aimir.int', :new.IPV6_ADDR);
    end if;
    if (:old.AMI_NETWORK_ADDRESS_V6 is null and :new.AMI_NETWORK_ADDRESS_V6 is not null) or (:new.AMI_NETWORK_ADDRESS_V6 is not null and :old.AMI_NETWORK_ADDRESS_V6 != :new.AMI_NETWORK_ADDRESS_V6) then
      insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, :new.SYS_ID||'.dcu.aimir.int', :new.AMI_NETWORK_ADDRESS_V6);
    end if;
  else
    insert into IP_DNSSYNC_LOG(logtype, domainname) values(var_input_type, :old.SYS_ID||'.dcu.aimir.int');
  end if;

  exception
    when others then
      null;
end;
/

create or replace trigger IP_DNSSYNC_MODEM
after insert or update or delete on MODEM
for each row
declare
  var_input_type varchar2(10);
  var_meter_serial varchar2(30);
begin
  var_input_type := null;
  var_meter_serial := null;

  if inserting then
    var_input_type := 'INSERT';
  elsif updating then
    var_input_type := 'UPDATE';
  else
    var_input_type := 'DELETE';
  end if;

  if inserting or updating then
    if (:old.IP_ADDR is null and :new.IP_ADDR is not null) or (:new.IP_ADDR is not null and :old.IP_ADDR != :new.IP_ADDR) then
      begin
        select mds_id into var_meter_serial from meter where modem_id = :new.ID;
      exception when others then
        null;
      end;

      if(INSTR(:new.IP_ADDR ,':')>0) then
        insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, :new.DEVICE_SERIAL||'.modem.aimir.int', :new.IP_ADDR);

        if (var_meter_serial is not null) then
          insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, var_meter_serial||'.meter.aimir.int', :new.IP_ADDR);
        end if;
      else
        insert into IP_DNSSYNC_LOG(logtype, domainname, ipv4) values(var_input_type, :new.DEVICE_SERIAL||'.modem.aimir.int', :new.IP_ADDR);

        if (var_meter_serial is not null) then
          insert into IP_DNSSYNC_LOG(logtype, domainname, ipv4) values(var_input_type, var_meter_serial||'.meter.aimir.int', :new.IP_ADDR);
        end if;
      end if;
    end if;
    if (:old.IPV6_ADDRESS is null and :new.IPV6_ADDRESS is not null) or (:new.IPV6_ADDRESS is not null and :old.IPV6_ADDRESS != :new.IPV6_ADDRESS) then
      begin
        select mds_id into var_meter_serial from meter where modem_id = :new.ID;
      exception when others then
        null;
      end;

      insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, :new.DEVICE_SERIAL||'.modem.aimir.int', :new.IPV6_ADDRESS);
      if (var_meter_serial is not null) then
        insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, var_meter_serial||'.meter.aimir.int', :new.IPV6_ADDRESS);
      end if;
    end if;
  else
    begin
      select mds_id into var_meter_serial from meter where modem_id = :new.ID;
    exception when others then
      null;
    end;

    insert into IP_DNSSYNC_LOG(logtype, domainname) values(var_input_type, :old.DEVICE_SERIAL||'.modem.aimir.int');
    if (var_meter_serial is not null) then
      insert into IP_DNSSYNC_LOG(logtype, domainname) values(var_input_type, var_meter_serial||'.meter.aimir.int');
    end if;
  end if;

  exception
    when others then
      null;
end;
/


create or replace trigger IP_DNSSYNC_METER
after insert or update or delete on METER
for each row
declare
  var_input_type varchar2(10);
  var_ipv4_addr varchar2(30);
  var_ipv6_addr varchar2(50);
begin
  var_input_type := null;
  var_ipv4_addr := null;
  var_ipv6_addr := null;

  if inserting then
    var_input_type := 'INSERT';
  elsif updating then
    var_input_type := 'UPDATE';
  else
    var_input_type := 'DELETE';
  end if;

  if inserting or updating then
    --if (:old.MODEM_ID is null and :new.MODEM_ID is not null) or (:new.MODEM_ID is not null and :old.MODEM_ID != :new.MODEM_ID) then
    if (:new.MODEM_ID is not null) then
      begin
        select ip_addr,ipv6_address 
        into var_ipv4_addr, var_ipv6_addr
        from modem where id=:new.MODEM_ID;
      exception when others then
        null;
      end;

      if (var_ipv4_addr is not null) then
        if(INSTR(var_ipv4_addr ,':')>0) then
          insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, :new.mds_id||'.meter.aimir.int', var_ipv4_addr);
        else
          insert into IP_DNSSYNC_LOG(logtype, domainname, ipv4) values(var_input_type, :new.mds_id||'.meter.aimir.int', var_ipv4_addr);
        end if;
      end if;
      if (var_ipv6_addr is not null) then
        insert into IP_DNSSYNC_LOG(logtype, domainname, ipv6) values(var_input_type, :new.mds_id||'.meter.aimir.int', var_ipv6_addr);
      end if;
    end if;
  else
    insert into IP_DNSSYNC_LOG(logtype, domainname) values(var_input_type, :new.mds_id||'.meter.aimir.int');
  end if;

  exception
    when others then
      null;
end;
/
