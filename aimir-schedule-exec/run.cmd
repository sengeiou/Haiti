@echo on
mvn -e antrun:run  -DscName=SC -DjmxPort=9000 -DspringContext=spring-quartz-analysis.xml