#!/usr/bin/env bash
set -e

export MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=KeychainStore"

mvn package -Dmaven.jarsigner.skip=true

java -jar target/gedit-1.0.0-jar-with-dependencies.jar "$@"
