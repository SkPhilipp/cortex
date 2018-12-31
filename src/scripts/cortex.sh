#!/usr/bin/env bash
# > cortex attack --source src/test/resources/assembly/winner-basic.cxasm
# [{"possibleValues":{"CALL_DATA[1]":24690},"solvable":true}]
# > cortex run --source src/test/resources/assembly/winner-basic.cxasm --call-data 1=24690
# Exception at position 9, reason: WINNER
java -jar build/libs/cortex.jar "$@"
