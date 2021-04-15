#!/bin/sh

#mvn -e antrun:run -DthreadSleep=3000  -DfepName=FEP1 -DdcuCount=1000 -DnodeCount=100 -DthreadCount=1000 -DlpCount=6 -DlpPeriod=60 -DtestClassName=TestKaifa -DfepIp=10.40.211.11 -DfepPort=8000 2>&1 > /dev/null &

#mvn -e antrun:run -DthreadSleep=3000 -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1 -DthreadCount=1 -DlpCount=6 -DlpPeriod=60 -DtestClassName=TestKaifa -DfepIp=10.40.211.11 -DfepPort=8000 2>&1 > /dev/null &

#mvn -e antrun:run -DthreadSleep=3000 -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1 -DthreadCount=1 -DlpCount=6 -DlpPeriod=60 testClassName=TestKaifa -DfepIp=localhost -DfepPort=8000 2>&1 > /dev/null &

#mvn -e antrun:run -DthreadSleep=3000 -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1 -DthreadCount=1 -DlpCount=240 -DlpPeriod=1  -DtestClassName=TestKaifa -DfepIp=FD00:0:0:100::100 -DfepPort=8000 2>&1 > sim.log &

#mvn -e antrun:run -DthreadSleep=3000 -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1000 -DthreadCount=1 -DlpCount=96 -DlpPeriod=15  -DtestClassName=TestKaifa3 -DfepIp=FD00:0:0:100::100 -DfepPort=8000 -DstartDcuId=20000 2>&1 > sim.log &

#mvn -e antrun:run -DthreadSleep=3000 -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1 -DthreadCount=1 -DlpCount=8 -DlpPeriod=30  -DtestClassName=TestKaifa -DfepIp=FD00:0:0:100::100 -DfepPort=8000 2>&1 > sim.log &

mvn -e antrun:run  -DfepName=FEP1 -DtestClassName=TestI210Plus -DfepIp=localhost -DfepPort=8000 2>&1 > sim.log &
