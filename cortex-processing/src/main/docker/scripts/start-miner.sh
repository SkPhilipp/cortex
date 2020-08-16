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
  --bootnodes enode://99b0aa351632ced25ecbd6919fda2edc18584db7c5c220f9d8724251603c8a7d36e13cbce5fbf3ef5c952c45be14bc8ce4e88ec7abe2181e5bbf08e2f28def31@127.0.0.1:0?discport=30301 \
  --syncmode full \
  --mine \
  --miner.threads=1 \
  --etherbase="${PUBLIC_ADDRESS}" \
  --ipcpath=/root/.ethereum/geth.ipc
