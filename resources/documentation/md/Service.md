# Server

The Server is a Spring boot Rest Service project that acts as an orchestrator and an aggregator of results

## Java

### Requirements

> - jdk11

### Launch Server

```bash
java -Dserver.port=6766 -jar service.jar --engine-ports 6767,6768 --logs-path  /Users/ramyeid/Documents/machine-learning-swissknife/build/logs/ --engine-path  /Users/ramyeid/Documents/machine-learning-swissknife/build/components/engine
```

### Engines

The Service owns the engines; this means that it is the service that will launch and destroy the engines.
