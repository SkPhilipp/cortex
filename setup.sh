#!/usr/bin/env bash
(
  cd cortex-processing/src/main/docker || exit 1
  docker-compose up
)
