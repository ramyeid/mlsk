# DevOps

We made sure that [devops](../../../../devops) is also easily accessible and runnable.

We have three different tools to offer

- [CI](devops/ci/CI.md) - To deploy a Jenkins continuous integration for building, testing and running sonar.

- [Packaging](devops/scripts/Packaging.md) - To locally deploy and run the solution

- [Deployment](devops/scripts/Deployment.md) - To pull an already build docker image and run the solution

## Docker Images

MLSK owns many docker images

- **ramyeid/mlsk-jenkins**
  > This serves as the image of the jenkins server
  - [locally](../../../../devops/ci/deployment/jenkins/Dockerfile)
  - [remote](https://hub.docker.com/repository/docker/ramyeid/mlsk-jenkins)
  - [documentation](devops/ci/Deployment.md)

- **ramyeid/mlsk-base**
  > This serves as base for other images. *containing different java, python and angular dependencies*
  - [locally](../../../../devops/base-image/Dockerfile)
  - [remote](https://hub.docker.com/repository/docker/ramyeid/mlsk-base)

- **ramyeid/mlsk-build-agent**
  > This serves as the image for the jenkins agent
  - [locally](../../../../devops/ci/agent/Dockerfile)
  - [remote](https://hub.docker.com/repository/docker/ramyeid/mlsk-build-agent)
  - [documentation](devops/ci/Agent.md)

- **ramyeid/mlsk-service**
  > This serves as the image of the service component
  - [locally](../../../../devops/scripts/deployment/service/Dockerfile)
  - [remote](https://hub.docker.com/repository/docker/ramyeid/mlsk-service)
  - [documentation](devops/scripts/Deployment.md)

- **ramyeid/mlsk-web-ui**
  > This serves as the image of the web ui component
  - [locally](../../../../devops/scripts/deployment/web-ui/Dockerfile)
  - [remote](https://hub.docker.com/repository/docker/ramyeid/mlsk-web-ui)
  - [documentation](devops/scripts/Deployment.md)

- **ramyeid/mlsk-swing-ui**
  > This serves as the image of the java swing ui component
  - [locally](../../../../devops/scripts/deployment/swing-ui/Dockerfile)
  - [remote](https://hub.docker.com/repository/docker/ramyeid/mlsk-swing-ui)
  - [documentation](devops/scripts/Deployment.md)
