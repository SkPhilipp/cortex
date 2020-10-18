# Cortex

Smart contracts open up new attack surfaces. Their machine-code, storage, and any interactions with them are public. Cortex takes smart contracts apart, analyses them & tries to extract value from them.

For examples, see https://cortex.minesec.net/.

## Server

See [install.sh](install.sh) for analysis host setup.

## CLI

- Add `cortex/cortex-processing/cortex` to your PATH.
- Create your own MongoDB instance or tunnel an environment using `ssh cortex-001 -L 27017:127.0.0.1:27017 -N`
- Use your own Ethereum blockchain or spin up a local instance using `docker-compose up`

When these steps are completed, the `cortex` command can run against your blockchain and MongoDB of choice as such;

    # cortex reset
    cortex barriers-deploy
    cortex search --blocks 100000
    cortex analyze --selection blocks --blocks 100000
    cortex report --selection blocks --blocks 100000
    cortex graph --selection address --program-address 0xe9d4e465a7d3c04e7b13b45a0c484f7d073e8674
