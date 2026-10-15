#!/usr/bin/env bash
set -e

export MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=KeychainStore"

mvn clean package -Dmaven.jarsigner.skip=true
