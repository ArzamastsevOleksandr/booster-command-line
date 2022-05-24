#!/usr/bin/env bash

# stop the booster service
sudo kill \
$(sudo lsof -t -i:8081)

# build booster with its dependencies while skipping tests
./mvnw clean package -pl booster -am -Dmaven.test.skip

# start the service
java -jar booster/target/booster-0.0.1-SNAPSHOT.jar
