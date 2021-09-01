# Deployment

We created a [docker compose](../../../../../ci/deployment/docker-compose.yaml) in order to deploy the CI tools (Jenkins & Sonar).

You can also find the [Jenkins Configuration as Code](../../../../../ci/deployment/jenkins/jenkins.yaml) that will give you the minimum requirements of the Jenkins service.

Also [Plugins](../../../../../ci/deployment/jenkins/plugins.txt) is the list of plugins needed in Jenkins.

We want to make it easy to deploy all aspects of this project including the CI.

## Content

### Docker compose

In the docker compose you can find three different services being deployed

- Jenkins
- Sonar
- Socat

Socat is being deployed in order to export the docker api port **2375**.

### Jenkins as Code

With jenkins as code we can have the minimum requirement to run all scripts and jobs in the jenkins instance.

### Plugins

Jenkins configuration as code does not support plugins.
We exported the list of plugins needed and that will be insatlled on start up.

## Configuration

### Jenkins

A dedicated [dockerfile](../../../../../ci/deployment/jenkins/Dockerfile) for jenkins was created to install all plugins.

This image is already pushed as [ramyeid/mlsk-jenkins](https://hub.docker.com/repository/docker/ramyeid/mlsk-jenkins)

When jenkins is launched we need to initialize it to do so, we need to check the logs and copy paste the admin password.

```text
Jenkins initial setup is required. An admin user has been created and a password generated.
Please use the following password to proceed to installation:

c5ef8937f2aa481da466bec037da517b

This may also be found at: /var/jenkins_home/secrets/initialAdminPassword
```

The docker compose will launch jenkins adding the configuration as code.

## Launch

### Start

```bash
cd ci/deployment
docker-compose up -d
```

### Stop

```bash
cd ci/deployment
docker-compose down
```

## Reference

[Docker compose](https://docs.docker.com/compose/)

[Configuration as Code](https://www.jenkins.io/projects/jcasc/)

## Remarks

- You probably have to regenerate the Sonar Token with our jenkins as code configuration.

  Please check [here](Sonar.md#Configuration)

- To list all plugins installed
  1. Go to ```_http://${jenkins-url}/script_```

  2. Launch the following script

      ```text
      Jenkins.instance.pluginManager.plugins.each{
        plugin -> 
          println "${plugin.getShortName()}:${plugin.getVersion()}"
      }
      ```
