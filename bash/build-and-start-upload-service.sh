#!/usr/bin/env bash

# stop the upload-service
sudo kill \
$(sudo lsof -t -i:8083)

# build upload-service with its dependencies while skipping tests
./mvnw clean package -pl upload-service -am -Dmaven.test.skip

# start the service
java -jar upload-service/target/upload-service-0.0.1-SNAPSHOT.jar
