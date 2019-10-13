# Cortex

## Windows Development Setup

Install Microsoft Z3 on your machine:
- Download https://github.com/Z3Prover/z3/releases/download/z3-4.8.6/z3-4.8.6-x64-win.zip
- Extract into your Program Files and add the `bin` folder to your `%PATH%`.
- Install the Z3 jar file in your Maven repository using:

    
    mvn install:install-file -Dfile=com.microsoft.z3.jar -DgroupId=com.microsoft.z3 -DartifactId=z3 -Dversion=4.8.6 -Dpackaging=jar

- Install Docker
- Configure your `MSYS_NO_PATHCONV` environment variable to ` 1`

## Production Setup

See [install.sh](./install.sh).

## Documentation

Visit https://cortex.minesec.net/.
