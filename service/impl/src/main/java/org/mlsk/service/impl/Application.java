package org.mlsk.service.impl;

import org.apache.commons.cli.ParseException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

@SpringBootApplication
public class Application {

  public static void main(String... args) throws ParseException, IOException {
    ServiceConfiguration.buildServiceConfiguration(args);
    new SpringApplicationBuilder(Application.class)
        .initializers(new LoggerInitialization())
        .run(args);
  }
}
