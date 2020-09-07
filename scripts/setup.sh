#!/usr/bin/env bash
(
  cd ../cortex-ethereum/src/main/docker || exit 1
  docker-compose up
)
