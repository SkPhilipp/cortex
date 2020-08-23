#!/usr/bin/env bash
for f in *.sol; do
  docker run -v "$(pwd):/volume" -w /volume ethereum/solc:0.5.7 --bin "$f" \
  | grep -v Binary \
  | grep -v barrier \
  | grep -v "^$" \
  >"$f".bin
done
