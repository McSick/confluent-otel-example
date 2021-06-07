# Confluent 
## Install
  Notes:
  https://docs.confluent.io/platform/current/quickstart/ce-quickstart.html#ce-quickstart - Step 1 only 
-  use local host template config 
-  Create a local file (for example, at $HOME/.confluent/java.config) with configuration parameters to connect to your Kafka cluster.
  `bootstrap.servers=localhost:9092`
- JDK 1.8 or 1.11 required for kafka.
- Make sure right version of java is set in terminal.   
  `export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_271.jdk/Contents/Home"` 
- Need to install confluent command and confluent control center
- Assumes cluster is on port 9092. 

## Start
`confluent local services start`
## Stop
`confluent local services stop`


# Applications 

1. Download java agents  
`curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.1.0/opentelemetry-javaagent-all.jar > /opt/agents/otel.jar`  


2. Set up your local environment with your Honeycomb API Key and Dataset name. You will need a Honeycomb Team in order to 
   get your API key. If you don't have a Honeycomb Team you can sign up for a free one [here](https://honeycomb.io/signup).
   For Dataset name, we recommend `kafkaexample` but you can make this whatever you like. 
   The `-w` option will write the environment variables to your shell profile.  
```shell
source setup-env.sh YOUR_API_KEY kafkaexample -w
```   

3. Run the run script   

The syntax for the run script is:
```shell
run.sh <producer port> <consumer port>
```

The syntax for the stop script is:
```shell
stop.sh
```
# Usage   

 1 Message on topic `Topic1`   

```
http://localhost:<producerport>/
```  

N Messages on topic `Topic1`   
```
http://localhost:<producerport>/Topic1?numMessages=<n>
```   
N Messages on topic `Topic2`   
```
http://localhost:<producerport>/Topic2?numMessages=<n>
```   
N Messages on topic `Topic3`   
```
http://localhost:<producerport>/Topic3?numMessages=<n>
```   

