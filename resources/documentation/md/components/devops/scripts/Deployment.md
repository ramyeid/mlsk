# Deploy, push and run

We worked hard to make machine learning easy and accessible for everyone. Deployment, pushing and running the solution should also be easy.

## Requirements

- mvn
- jdk11
- python3
- npm
- ng
- docker

## Deployment

To deploy the solution a script is offered under [deployment](../../../../../../devops/scripts/deployment.py).

The deployment will create a docker_deploy directory containing the dockerfiles for different services, the jars, python module and angular build directory.

> It is recommended to launch a python virtual environment locally \
> to do so [Setup venv](../../Engine.md#Setup-Python-Environment).

```bash
source .venv/bin/activate
cd devops/scripts
python deployment.py [--push]
```

## Launch Service

We offer an image for [MLSK-Service](https://hub.docker.com/repository/docker/ramyeid/mlsk-service).

The dockerfile of this image can be found [here](../../../../../../devops/scripts/deployment/service/Dockerfile).

> The port configured on the container is **6766**. \
> By default we are creating three engines.

```bash
docker run -d -p 6766:6766 -v /Users/ramyeid/Documents/FYP/V1/mlsk/docker_deployment/logs:/mlsk-logs ramyeid/mlsk-service
```

## Launch Web UI

We offer an image for [MLSK-Web-UI](https://hub.docker.com/repository/docker/ramyeid/mlsk-web-ui).

The dockerfile of this image can be found [here](../../../../../../devops/scripts/deployment/web-ui/Dockerfile).

> The port configured on the container is **80**. \
> By default we are connecting to the service under port **6766**. (*The URL of the service can be modified in the configuration page of the service*)

```bash
docker run -d -p 6765:80 ramyeid/mlsk-web-ui
```
