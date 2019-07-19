drop table ip_mv_channelobis;
create table ip_mv_channelobis(
  tablename varchar2(2),
  aimirchannel varchar2(30),
  obisid varchar2(30),
  constraint ip_mv_channelobis_PK primary key(tablename,aimirchannel)
) TABLESPACE AIMIRINT;

-- CHANNEL_CONFIG 비교해서 입력.
-- select distinct data_type,channel_index,channel_id 
-- from channel_config order by data_type,channel_index,channel_id;
-- EM
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','1','1.0.1.8.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','2','1.0.2.8.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','3','1.0.3.8.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','4','1.0.4.8.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','981','1.0.32.23.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','982','1.0.32.24.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','983','1.0.32.26.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','984','1.0.52.23.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','985','1.0.52.24.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','986','1.0.52.26.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','987','1.0.72.23.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','988','1.0.72.24.0.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('EM','989','1.0.72.26.0.255');
-- GM
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('GM','1','3.1.24.2.1.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('GM','2','3.2.24.2.1.255');
-- WM
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('WM','1','4.1.24.2.1.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('WM','2','4.2.24.2.1.255');
-- HM
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('HM','1','5.1.24.2.1.255');
insert into ip_mv_channelobis(tablename,aimirchannel,obisid) values('HM','2','5.2.24.2.1.255');

commit work;

