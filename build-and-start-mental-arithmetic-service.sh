#!/usr/bin/env bash

# stop mental-arithmetic-service
sudo kill \
$(sudo lsof -t -i:8086)

# build mental-arithmetic-service while skipping tests
./mvnw clean package -pl notes-service -am -Dmaven.test.skip

# start mental-arithmetic-service
java -jar mental-arithmetic-service/target/mental-arithmetic-service-0.0.1-SNAPSHOT.jar
