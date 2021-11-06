# Continuous Integration - Pipeline

The [build pipeline](../../../../../../Jenkinsfile) is a Jenkinsfile that defines the steps that will run on jenkins agent.
The pipeline is written in _Declarative_ way

## Parameters

- GENERATE_NODE_MODULES

  > - Used for WebUI angular project.
  > - In order to regenerate `node_modules` and overwrite the current cached `node_modules`.
  > - If false, will link to already cached node_modules via docker volumes.
  > - **Default is false**.

## Steps

In this pipeline we will build, test and check quality gates of all our projects (Service [Java], UI [Java], Engine[Python] & Web UI [Angular])

This pipeline delegates all methods to our [shared library](Library.md)

> We know that we can run the different steps in parallel (Java // Python // Angular) but for the scarcity of resources we decided to run them sequentially.

## Reference

[Declarative Pipeline](https://www.jenkins.io/doc/book/pipeline/syntax/)
