@echo on
mvn -e antrun:run -DfepName=FEP1 -DdcuCount=1000 -DnodeCount=100 -DthreadCount=1000 -DtestClassName=TestNamjun -DfepIp=187.1.10.58 -DfepPort=8000
#mvn -e antrun:run -DfepName=FEP1 -DdcuCount=1000 -DnodeCount=100 -DthreadCount=1000 -DtestClassName=TestOmniPower -DfepIp=187.1.10.58 -DfepPort=8000
mvn -e antrun:run -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1 -DthreadCount=1 -DtestClassName=TestNamjun -DfepIp=187.1.10.58 -DfepPort=8000
#mvn -e antrun:run -DfepName=FEP1 -DdcuCount=1 -DnodeCount=1 -DthreadCount=1 -DtestClassName=TestOmniPower -DfepIp=187.1.10.58 -DfepPort=8000