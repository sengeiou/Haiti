select m.location_id, count(m.mdev_id) cnt
from (
    select location_id, mdev_id
    from day_em
    where yyyymmdd between '20120501' and '20120501'
    and mdev_type='Meter' and channel=1
    group by location_id, mdev_id
) m
group by m.location_id;

select p.id, c.id, count(m.id) 
from location p left outer join location c on p.id = c.parent_id and p.supplier_id=11, meter m
where p.id = m.location_id and m.meter = 'EnergyMeter'
and m.install_date <= '20120510235959'
group by p.id, c.id;

select p.id pid, c.id cid
from location p left outer join location c on p.id = c.parent_id and p.supplier_id=11;

select * from contractcapacity;
select * from supplytypelocation;
select * from meter where location_id=10;

select * from contractcapacity c
inner join supplytypelocation s on c.id = s.contractcapacity_id and c.id=2;

select * from contractcapacity c
inner join supplytypelocation s on c.id = s.contractcapacity_id and c.id=2, 
meter m, lp_em lp
where m.location_id = s.location_id and m.mds_id = lp.mdev_id and lp.channel=1 and lp.yyyymmdd='20120510' and lp.mdev_type='Meter';

select * from contractcapacity c
inner join supplytypelocation s on c.id = s.contractcapacity_id
inner join meter m on m.location_id = s.location_id
,lp_em lp
where m.mds_id = lp.mdev_id and lp.channel=1 and lp.yyyymmdd='20120510' and lp.mdev_type='Meter';

select * from supplytypelocation s 
inner join meter m on m.location_id = s.location_id and s.contractcapacity_id=2
inner join lp_em lp on m.mds_id = lp.mdev_id and lp.mdev_type='Meter' and yyyymmddhh between '2012051010' and '2012051012' and channel=1;

select * from supplytypelocation s, meter m, lp_em lp
where s.contractcapacity_id=2 and s.location_id = m.location_id 
and m.meter = 'EnergyMeter' and m.mds_id = lp.mdev_id and lp.mdev_type='Meter' and lp.yyyymmddhh between '2012051015' and '2012051016' and lp.channel=1;

select * from lp_em where yyyymmdd='20120510' and mdev_type='Meter' and channel=1 and mdev_id='KTSUMNEW';
