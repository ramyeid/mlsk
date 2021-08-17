package org.mlsk.service.impl;

import org.apache.commons.cli.ParseException;
import org.mlsk.service.impl.configuration.LoggerInitialization;
import org.mlsk.service.impl.configuration.ServiceConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

  public static void main(String... args) throws ParseException {
    ServiceConfiguration.buildServiceConfiguration(args);
    new SpringApplicationBuilder(Application.class)
        .initializers(new LoggerInitialization())
        .run(args);
  }
}
