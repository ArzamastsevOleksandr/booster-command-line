#!/usr/bin/env bash

# stop the tag service
sudo kill \
$(sudo lsof -t -i:8085)

# build tag-service with its dependencies while skipping tests
./mvnw clean package -pl tag-service -am -Dmaven.test.skip

# start the service
java -jar tag-service/target/tag-service-0.0.1-SNAPSHOT.jar
