#!/usr/bin/env bash

# make sure the vocabulary service is down
sudo kill \
$(sudo lsof -t -i:8082)

# build vocabulary-service with its dependencies while skipping tests
./mvnw clean package -pl vocabulary-service -am -Dmaven.test.skip

# run an executable jar
java -jar vocabulary-service/target/vocabulary-service-0.0.1-SNAPSHOT.jar
