#!/usr/bin/env bash

# stop the settings service
sudo kill \
$(sudo lsof -t -i:8084)

# build settings-service with its dependencies while skipping tests
./mvnw clean package -pl notes-service -am -Dmaven.test.skip

# start the service
java -jar settings-service/target/settings-service-0.0.1-SNAPSHOT.jar
