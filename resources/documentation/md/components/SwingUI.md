# Swing UI

The Swing UI is a Java project that enables you to have UI for MLSK Service

> **_NOTE:_**  You will be able to modify the url (host & port) of the *server* in a configuration page in the ui.

## Java

### Requirements

> - jdk11

### Launch Server

```bash
java -jar mlsk-ui-jar-with-dependencies.jar [--service-host localhost] --service-port 6766
```

## Docker Image

This service is deployed on docker under [ramyeid/mlsk-swing-ui](https://hub.docker.com/repository/docker/ramyeid/mlsk-swing-ui)

> Information on how to launch a container can be found [here](devops/scripts/Deployment.md)
