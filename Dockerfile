FROM openjdk:13-jdk-alpine

# Z3
ENV Z3_VERSION "4.8.8"
RUN apk add g++ make apache-ant binutils python \
 && Z3_DIR="$(mktemp -d)" \
 && cd "$Z3_DIR" \
 && wget -qO- https://github.com/Z3Prover/z3/archive/z3-${Z3_VERSION}.tar.gz | tar xz --strip-components=1 \
 && python scripts/mk_make.py --java \
 && cd build \
 && make \
 && make install \
 && cd / \
 && rm -rf "$Z3_DIR" \
 && apk del python binutils apache-ant make g++

RUN apk add libstdc++

# Gradle
ENV GRADLE_VERSION "6.5.1"
RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -O gradle-${GRADLE_VERSION}-bin.zip \
 && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
 && rm gradle-${GRADLE_VERSION}-bin.zip
ENV GRADLE_HOME="/opt/gradle-${GRADLE_VERSION}"
ENV PATH=$PATH:$GRADLE_HOME/bin

# Git
RUN apk add git

# SSH, SCP
RUN apk add openssh-client

# Tar
RUN apk --update add tar