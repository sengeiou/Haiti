@echo on
mvn -e -f pom-event.xml antrun:run -DfepName=FEP1 -DdcuCount=1000 -DnodeCount=100 -DthreadCount=1000 -DtestClassName=TestDCUInstall -DfepIp=187.1.10.58 -DfepPort=8000
