# Cortex

Smart contracts open up new attack surfaces. Their machine-code, storage, and any interactions with them are public. Cortex takes smart contracts apart, analyses them & tries to extract value from them.

For examples, see https://cortex.minesec.net/symbolic.html#_symbolic_explorer.

## Documentation

Gradle generates the documentation, partially during test execution.
You can find the latest documentation at https://cortex.minesec.net/.

## (Windows) Development Setup

Install Microsoft Z3 on your machine:
- Download https://github.com/Z3Prover/z3/releases/download/z3-4.8.8/z3-4.8.8-x64-win.zip
- Extract into your Program Files and add the `bin` folder to your `%PATH%`.
- Install the Z3 jar file in your Maven repository using:

    
    mvn install:install-file -Dfile=com.microsoft.z3.jar -DgroupId=com.microsoft.z3 -DartifactId=z3 -Dversion=4.8.8 -Dpackaging=jar

- Install Docker
- Configure your `MSYS_NO_PATHCONV` environment variable to ` 1`

## Production Setup

See [install.sh](./install.sh).
