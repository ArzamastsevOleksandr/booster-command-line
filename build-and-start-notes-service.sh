#!/usr/bin/env bash

# stop the notes service
sudo kill \
$(sudo lsof -t -i:8081)

# build notes-service with its dependencies while skipping tests
./mvnw clean package -pl notes-service -am -Dmaven.test.skip

# start the service
java -jar notes-service/target/notes-service-0.0.1-SNAPSHOT.jar
