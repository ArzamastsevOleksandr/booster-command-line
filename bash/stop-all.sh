#!/usr/bin/env bash

# stop the services running on ports
# 8081: booster
# 8083: upload-service

sudo kill \
$(sudo lsof -t -i:8081) \
$(sudo lsof -t -i:8083)
