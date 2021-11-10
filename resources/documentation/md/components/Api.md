# API Documentation

In order to document our endpoints we decided to use swagger/open api yaml files.

Also, to ensure that the yaml is always up to date with the service, we decided to use open api code generation tool.

There are number of tools we gain by using swagger/open api:

    - Code Gen (HTML Documentation, Server Stubs & Client)
    - API Validation
    - etc.

## Java Code Generation

We can find the code generation plugin and configuration options under [api/pom.xml](../../../../api/pom.xml)

We wanted a code generation plugin that will generate:

> - Models with annotation and equals and hashcode
>
> - Generated Api should have spring annotations and documentation
>
> - Skip generation of Implementations

The different generators languages can be found [here](https://openapi-generator.tech/docs/generators)

### Remarks

- There are a number of Java Server Stubs _language_ we can use (MSF4J, Spring, Undertow, JAX-RS: CDI, CXF, Inflector, Jersey, RestEasy, Play Framework, PKMST, Vert.x)
- _spring_ was chosen since it matches the requirements needed
- Currently we are using a previous version of the plugin, **some problems were faced with the newest version**
- The different configuration option for spring language can be found [here](https://openapi-generator.tech/docs/generators/spring)

## APIs

> You can find the API Documentation under _api_ module.
>
> To visualize the API, please visit **_SWAGGER UI_**.

### Time Series Analysis

- [Service API](../../../../api/src/main/java/org/mlsk/api/timeseries/service/TimeSeriesAnalysisServiceApi.yaml)
- [Engine API](../../../../api/src/main/java/org/mlsk/api/timeseries/engine/TimeSeriesAnalysisEngineApi.yaml)

## References & Documentation

```text
https://editor.swagger.io
https://openapi-generator.tech/docs/generators
```

## Problems found with different JAVA languages Server Stubs code generation

- _spring_
  - GOOD.
  - Equals and hashcode in models.
  - API Interface.
- _java-msf4j_
  - OK.
  - Generates default implementation of the API and injects a Service that needs to be implemented.
- _java-undertow-server_
  - NOK.
  - no API.
- _jaxrs-cxf-cdi_
  - OK.
  - Generates default implementation of the API and injects a Service that needs to be implemented.
- _jaxrs-cxf_
  - GOOD.
  - No equals and hashcode in models.
- _java-inflector_
  - NOK.
- _jaxrs-jersey_
  - NOK.
- _jaxrs-resteasy_
  - NOK.
  - Similar to _cxf-cdi_.
- _jaxrs-resteasy-eap_
  - GOOD.
  - Additional @Context parameter added in API.
  - 404 did not work at runtime.
