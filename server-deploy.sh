#!/usr/bin/env bash
ssh cortex "pkill -f java"
scp ./build/libs/cortex-FIXED.jar cortex:cortex-FIXED.jar
ssh cortex "nohup java -jar cortex-FIXED.jar > cortex.log &"
