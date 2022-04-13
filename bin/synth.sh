#!/bin/sh

RUNTIME_PATH=src/main/resources/aries-cloudagent-python-0.7.3.zip

(cd core && cdk acknowledge 19836  && cdk synth -c runtime-path=$RUNTIME_PATH)
