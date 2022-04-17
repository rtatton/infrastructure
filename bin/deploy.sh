#!/bin/sh

(cd core && rm -rf cdk.out && cdk deploy --no-previous-parameters --outputs-file ./cdk-outputs.json)
