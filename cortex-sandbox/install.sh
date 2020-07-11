#!/usr/bin/env bash
ls geth-installer.exe || curl https://gethstore.blob.core.windows.net/builds/geth-windows-amd64-1.9.15-0f77f34b.exe --output geth-installer.exe
mkdir -p datadir
echo to install Geth run: ./geth-installer.exe
echo run installed Geth using: geth --datadir datadir init genesis.json
