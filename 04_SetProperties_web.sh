echo 'SetProperties files.....'
cp ./properties/command.properties ./aimir-web/target/aimir-web-3.4-SNAPSHOT/WEB-INF/classes
cp ./properties/command.properties ./aimir-web/src/main/resources/command.properties
cp ./properties/jdbc.properties ./aimir-web/target/aimir-web-3.4-SNAPSHOT/WEB-INF/classes
cp ./properties/jdbc.properties ./aimir-web/src/main/resources/jdbc.properties
echo 'done.'
