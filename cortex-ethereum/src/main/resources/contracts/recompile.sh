#!/usr/bin/env bash
for f in *.sol; do
  docker run -v "$(pwd):/volume" -w /volume ethereum/solc:0.5.7 --bin "$f" \
  | grep -v Binary \
  | grep -v barrier \
  | grep -v "^$" \
  >"$f".bin
  docker run -v "$(pwd):/volume" -w /volume ethereum/solc:0.5.7 --abi "$f" \
  | grep -v 'Contract JSON' \
  | grep -v barrier \
  | grep -v "^$" \
  >"$f".abi
done
