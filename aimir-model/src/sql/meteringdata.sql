서초 url : jdbc:derby://222.106.33.29:1527/javadb;
aimir/aimirdb

# 위치를 트리 구조로 가져온다.
select p.id pid, c.id cid
from location p left outer join location c on p.id = c.parent_id and p.supplier_id=?

# 위치별 계량기 개수 파악 쿼리
select p.id, c.id, count(m.id) 
from location p left outer join location c on p.id = c.parent_id and p.supplier_id=?, meter m
where p.id = m.location_id and m.meter = ?
and m.install_date <= ?
group by p.id, c.id;

# 일별 검침테이블을 이용한 위치별 검침개수
select m.location_id, count(m.mdev_id) cnt
from (
    select location_id, mdev_id
    from day_em
    where yyyymmdd between ? and ?
    and mdev_type='Meter' and channel=1
    group by location_id, mdev_id
) m
group by m.location_id