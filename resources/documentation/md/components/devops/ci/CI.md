# Continuous Integration

Under the [ci](../../../../../../devops/ci/) folder you can find all necessary scripts, jenkinsfile and docker files to deploy the jenkins.

These files will ensure continuous integration of MLSK.

## Components

> - [Deployment](Deployment.md)
> - [Sonar](Sonar.md)
> - [Agent](Agent.md)
> - [Seeder](Seeder.md)
> - [Pipeline](Pipeline.md)
> - [Library](Library.md)
> - [Docker](Docker.md)

## Miscellaneous

- To stop an unstoppable zombie job

  > Go to "Manage Jenkins" > "Script Console" and run a script:

  ```python
  Jenkins.instance.getItemByFullName("MLSK/master")
      .getBuildByNumber(28)
      .finish(hudson.model.Result.ABORTED, 
              new java.io.IOException("Aborting build")); 
  ```
