@echo on
mvn -e -f pom-SnmpTrapManager.xml antrun:run -DfepName=FEP1 -Dfile.encoding=UTF-8 -Dcommunity=asm -DtrapPort=162 -DsubPort=163 -DjmxPort=1399 -Declipselink.path=C:/muzi/workspace10-m2/repository/org/eclipse/persistence/eclipselink/2.6.2 -DlocalRepository=C:/muzi/workspace10-m2/repository

#-Dfile.encoding=EUC_KR 