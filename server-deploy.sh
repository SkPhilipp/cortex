#!/usr/bin/env bash
ssh cortex "pkill -f java"
scp ./build/libs/cortex.jar cortex:cortex.jar
ssh cortex "nohup java -jar cortex.jar > cortex.log &"
