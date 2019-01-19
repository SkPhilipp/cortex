#!/usr/bin/env bash
CORTEX_HOME=$(dirname "$0")/../..
java -jar ${CORTEX_HOME}/build/libs/cortex.jar "$@"
