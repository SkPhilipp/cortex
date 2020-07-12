#!/usr/bin/env bash
ls /volume/datadir || mkdir /volume/datadir
ls /volume/datadir || geth --datadir /volume/datadir init /root/genesis.json
ls /volume/boot.key || bootnode --genkey=/volume/boot.key
bootnode --nodekey=/volume/boot.key
