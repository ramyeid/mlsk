# Continuous Integration - Agent

The agent is a [Dockerfile](../../../../../../devops/ci/agent/Dockerfile) that contains all tools necessary to build, test and check sonar quality gate of the different components of MLSK.

> This Dockerfile is already built and deployed on docker hub under [ramyeid/mlsk-build-agent](https://hub.docker.com/repository/docker/ramyeid/mlsk-build-agent)

## Content

This image contain all tools needed to build and test Java, Python and Angular projects

- _python3_
- _pip_
- _java_
- _maven_
- _npm_
- _ng_
- _chrome (for testing)_
- _sonar-scanner_

## Configuration

To configure a docker agent we need to:

1. Download _Docker Plugin_.

2. Go to _Dashboard > Manage Jenkins > Manage Nodes and Clouds > Configure Clouds > Add a new Clouds_.

3. Follow the images below
WRITE HERE HOW TO CONFIGURE DOCKER IN JENKSIN

    ![Step1](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_1.png)
    ![Step2](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_2.png)
    ![Step3](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_3.png)
    ![Step4](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_4.png)
    ![Step5](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_5.png)
    ![Step6](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_6.png)
    ![Step7](../../../images/devops/ci/configure_Agent/Configure_Docker_Agent_7.png)

## How to

### Docker

[How to Docker](Docker.md)
