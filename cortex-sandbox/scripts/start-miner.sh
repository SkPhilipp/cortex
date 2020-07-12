#!/usr/bin/env bash
sleep 3
EXISTING_ACCOUNT=$(geth account list --datadir volume/datadir/ 2>/dev/null | grep -o -e "{.*}")
if test -z "${EXISTING_ACCOUNT}"; then
  PUBLIC_ADDRESS=$(geth --datadir /volume/datadir account new --password <(echo i laugh) | grep "Public address of the key:" | grep -o -e "0x.*")
else
  PUBLIC_ADDRESS="${EXISTING_ACCOUNT:1:-1}"
fi

geth \
  --datadir ./volume/datadir \
  --networkid 1488 \
  --bootnodes enode://832465276f89e0c4d1503c4b31f4815bbbef6d4f7cd25715b496ff1320440f7003bc1dad482e61212333c025efd3541b2bbeb714db3dea463c50827791319061@127.0.0.1:0?discport=30301 \
  --syncmode full \
  --mine \
  --miner.threads=1 \
  --etherbase="${PUBLIC_ADDRESS}" \
  --ipcpath=/root/.ethereum/geth.ipc
