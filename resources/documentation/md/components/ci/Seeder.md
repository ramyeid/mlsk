# Continuous Integration - Seeder

In order to make it simpler to run the [pipeline](Pipeline.md), we decided to create a [seeder](../../../../../ci/seeder/seeder.groovy) that will enable us to create a multi branch pipeline.

## Content

The seeder contains all the configuration needed to create a multi branch pipeline job with _script path_ being our pipeline

## Configuration

To create a configure a seeder job

1. Create Freestyle Project
  ![Create Seeder Job](../../images/ci/Configure_Seeder/Configure_Seeder_1.png)
2. Configure seeder job
  ![Step1](../../images/ci/Configure_Seeder/Configure_Seeder_2.png)
  ![Step2](../../images/ci/Configure_Seeder/Configure_Seeder_3.png)
  ![Step3](../../images/ci/Configure_Seeder/Configure_Seeder_4.png)
  ![Step4](../../images/ci/Configure_Seeder/Configure_Seeder_5.png)
  ![Step5](../../images/ci/Configure_Seeder/Configure_Seeder_6.png)
  ![Step6](../../images/ci/Configure_Seeder/Configure_Seeder_7.png)

## Reference

[API of Jenkisn DSL](https://jenkinsci.github.io/job-dsl-plugin/#path/job)

[Multi branch pipeline](https://www.jenkins.io/doc/book/pipeline/multibranch/)

[How to create seeder](https://www.serverlab.ca/tutorials/dev-ops/automation/how-to-seed-jenkins-build-jobs/)
