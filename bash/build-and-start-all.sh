#!/usr/bin/env bash

# kill services running on ports
# 8081: booster
# 8083: upload-service
sudo kill \
$(sudo lsof -t -i:8081) \
$(sudo lsof -t -i:8083)

# build the whole project and create executable jars while skipping tests
./mvnw clean package -Dmaven.test.skip

# start the services
java -jar booster/target/booster-0.0.1-SNAPSHOT.jar &
java -jar upload-service/target/upload-service-0.0.1-SNAPSHOT.jar
