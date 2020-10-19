# Cortex

Smart contracts open up new attack surfaces. Their machine-code, storage, and any interactions with them are public. Cortex takes smart contracts apart, analyses them & tries to extract value from them.

For examples, see https://cortex.minesec.net/.

## Server

See [install.sh](install.sh) for analysis host setup.

## CLI

- Add `cortex/cortex-processing/cortex` to your PATH.
- Start a local Geth instance & tunnel to the development MongoDB using `docker-compose up`

When these steps are completed, the `cortex` command can run against your blockchain and MongoDB of choice as such;

    # cortex reset
    cortex barriers-deploy
    cortex search --blocks 1000000
    cortex barriers-allocate --blocks 1000000
    cortex analyze --selection blocks --blocks 1000000
    cortex report --selection blocks --blocks 1000000
    cortex graph --selection address --program-address 0xe9d4e465a7d3c04e7b13b45a0c484f7d073e8674
