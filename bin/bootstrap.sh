#!/bin/bash

RUNTIME_PATH=src/main/resources/aries-cloudagent-python-0.7.3.zip

(cd core && cdk bootstrap -c runtime-path=$RUNTIME_PATH)
