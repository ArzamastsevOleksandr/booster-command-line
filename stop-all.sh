#!/usr/bin/env bash

# stop the services running on ports
# 8081: notes-service
# 8082: vocabulary-service
# 8083: upload-service
# 8084: settings-service
# 8085: tag-service
sudo kill \
$(sudo lsof -t -i:8081) \
$(sudo lsof -t -i:8082) \
$(sudo lsof -t -i:8083) \
$(sudo lsof -t -i:8084) \
$(sudo lsof -t -i:8085)
