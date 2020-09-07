#!/usr/bin/env bash
docker build . -t minesec/cortex-build:latest
docker push minesec/cortex-build:latest
