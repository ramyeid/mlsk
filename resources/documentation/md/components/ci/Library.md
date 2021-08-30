# Continuous Integration - Shared Library

We created a [shared library](../../../../../ci/shared-library) in jenkins in order to reuse and test some functionality as well as write clean declarative pipeline.

The shared library consists of:

- [vars](../../../../../ci/shared-library/vars) folder containing the methods that will be able to call in jenkins pipeline.

- [src](../../../../../ci/shared-library/src) folder containing the implementations of these methods

## Configuration

In order to configure a shared library we need to go to _Dashboard > Manage Jenkins > Configure System_

![Step1](../../images/ci/Configure_Shared_Library/Configure_Shared_Library_1.png)
![Step2](../../images/ci/Configure_Shared_Library/Configure_Shared_Library_2.png)
![Step3](../../images/ci/Configure_Shared_Library/Configure_Shared_Library_3.png)

## Reference

[Shared Library](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
