#!/usr/bin/env bash

# kill services running on ports
# 8081: notes-service
# 8082: vocabulary-service
# 8083: upload-service
# 8084: settings-service
# 8085: tag-service
# 8086: mental-arithmetic-service
sudo kill \
$(sudo lsof -t -i:8081) \
$(sudo lsof -t -i:8082) \
$(sudo lsof -t -i:8083) \
$(sudo lsof -t -i:8084) \
$(sudo lsof -t -i:8085) \
$(sudo lsof -t -i:8086)

# build the whole project and create executable jars while skipping tests
./mvnw clean package -Dmaven.test.skip

# start the services
java -jar notes-service/target/notes-service-0.0.1-SNAPSHOT.jar &
java -jar settings-service/target/settings-service-0.0.1-SNAPSHOT.jar &
java -jar tag-service/target/tag-service-0.0.1-SNAPSHOT.jar &
java -jar upload-service/target/upload-service-0.0.1-SNAPSHOT.jar &
java -jar vocabulary-service/target/vocabulary-service-0.0.1-SNAPSHOT.jar &
java -jar mental-arithmetic-service/target/mental-arithmetic-service-0.0.1-SNAPSHOT.jar
