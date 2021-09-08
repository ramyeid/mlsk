# Server

The Server is a Spring boot Rest Service project that acts as an orchestrator and an aggregator of results

## Java

### Requirements

> - jdk11

### Launch Server

```bash
java -Dserver.port=6766 -jar mlsk-service-impl.jar --engine-ports 6767,6768 --logs-path  /Users/ramyeid/Documents/FYP/V1/mlsk/build/logs/ --engine-path  /Users/ramyeid/Documents/FYP/V1/mlsk/build/components/engine
```

### Engines

The Service owns the engines; this means that it is the service that will launch and destroy the engines.

## Docker Image

This service is deployed on docker under [ramyeid/mlsk-serivce](https://hub.docker.com/repository/docker/ramyeid/mlsk-service)

> Information on how to launch a container can be found [here](devops/scripts/Deployment.md)
