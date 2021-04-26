# Confluent 
## Install
  Notes:
  https://docs.confluent.io/platform/current/quickstart/ce-quickstart.html#ce-quickstart - Step 1 only 
-  use local host template config 
-  Create a local file (for example, at $HOME/.confluent/java.config) with configuration parameters to connect to your Kafka cluster.
  `bootstrap.servers=localhost:9092`
- JDK 1.8 or 1.11 required for kafak.  I have 1.8 and 1.12
- Make sure right version of java is set in terminal.   
  `export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_271.jdk/Contents/Home"` 
- Need to install confluent command and confluent control center

## Start
`confluent local services start`
## Stop
`confluent local services stop`


# Applications 

## Install 
`cd producer/gs-spring-boot/complete`  
`cd consumer/gs-spring-boot/complete`  
Run below install command, Skip tests when installing as tests were not updated   
 `mvn install -DskipTests`  

## Start

Download java agents  
`curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.1.0/opentelemetry-javaagent-all.jar > /opt/agents/otel.jar`  

Set environment variables for Honeycomb api  

`export OTEL_TRACES_EXPORTER=otlp`  
`export OTEL_METRICS_EXPORTER=none`  
`export OTEL_EXPORTER_OTLP_ENDPOINT=https://api.honeycomb.io`  
`export OTEL_EXPORTER_OTLP_HEADERS=x-honeycomb-team=$HONEYCOMB_APIKEY,x-honeycomb-dataset=$HONEYCOMB_DATASET`  

Start up apps from "complete" folder  

`java -javaagent:/opt/agents/otel.jar -Dotel.resource.attributes=service.name=producer -jar target/*.jar --server.port=8080`  

`java -javaagent:/opt/agents/otel.jar -Dotel.resource.attributes=service.name=consumer -jar target/*.jar --server.port=8081`  



