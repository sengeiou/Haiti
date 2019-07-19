@echo on
mvn -e -f pom-feph.xml antrun:run -DfepName=FEP1 -Dfile.encoding=EUC_KR -DcommPort=8000 -DbypassPort=8900 -DjmxPort=1199 -Declipselink.path=C:/Users/nuri/.m2/repository/org/eclipse/persistence/eclipselink/2.6.0-M3 -DlocalRepository=C:/Users/nuri/.m2/repository

