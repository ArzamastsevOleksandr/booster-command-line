#!/usr/bin/env bash

java -jar notes-service/target/notes-service-0.0.1-SNAPSHOT.jar &
java -jar settings-service/target/settings-service-0.0.1-SNAPSHOT.jar &
java -jar tag-service/target/tag-service-0.0.1-SNAPSHOT.jar &
java -jar upload-service/target/upload-service-0.0.1-SNAPSHOT.jar &
java -jar vocabulary-service/target/vocabulary-service-0.0.1-SNAPSHOT.jar
