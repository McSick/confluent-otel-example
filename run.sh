#!/bin/bash
print_usage() {
  echo "run.sh <producer port> <consumer port>"
}

if [[ -z "$1" ]]; then
  print_usage
  exit 1
fi

if [[ -z "$2" ]]; then
  print_usage
  exit 1
fi

build() {
  cd producer || exit
  echo "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Installing Producer-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
  ./mvnw package -DskipTests
  echo "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Starting Producer-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
  java -javaagent:../lib/opentelemetry-javaagent-all.jar -Dotel.resource.attributes=service.name=producer -Dconsumer_port="${CONSUMER_PORT}"  -jar target/*.jar --server.port=${PRODUCER_PORT} &
  cd ../
  cd consumer || exit
  echo "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Installing Consumer-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
  ./mvnw package -DskipTests
  echo "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Starting Consumer-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
  java -javaagent:../lib/opentelemetry-javaagent-all.jar -Dotel.resource.attributes=service.name=consumer -jar target/*.jar --server.port=${CONSUMER_PORT} &
  echo "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Run Complete-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
  
}

export PRODUCER_PORT="$1"
export CONSUMER_PORT="$2"
export OTEL_METRICS_EXPORTER="none"
export OTEL_EXPORTER_OTLP_ENDPOINT="https://api.honeycomb.io"
export OTEL_EXPORTER_OTLP_HEADERS="x-honeycomb-team=${HONEYCOMB_API_KEY},x-honeycomb-dataset=${HONEYCOMB_DATASET}"
build
