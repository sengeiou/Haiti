#!/bin/sh
#
# properties : config/ota_soria.properties (default)

nohup mvn -e -f pom-OTABatchForSORIAbyDCU.xml antrun:run -Dproperties=config/ota_soria.properties 2>&1 > /dev/null &