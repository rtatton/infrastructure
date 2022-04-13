#!/bin/sh

RUNTIME_PATH=src/main/resources/aries-cloudagent-python-0.7.3.zip

(cd core && deploy.sh && cdk deploy -c runtime-path=$RUNTIME_PATH)
