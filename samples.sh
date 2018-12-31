#!/usr/bin/env bash
java -jar build/libs/cortex.jar run --source src/test/resources/assembly/winner-immediate.cxasm
java -jar build/libs/cortex.jar analyze --source src/test/resources/assembly/winner-basic.cxasm
java -jar build/libs/cortex.jar attack --source src/test/resources/assembly/winner-basic.cxasm
