@echo on
mvn -e -f pom-smpp.xml antrun:run -DfepName=FEP1 -Dfile.encoding=EUC_KR -DjmxPort=1499 -Declipselink.path=C:/Users/nuri/.m2/repository/org/eclipse/persistence/eclipselink/2.6.0-M3 -DlocalRepository=C:/Users/nuri/.m2/repository

