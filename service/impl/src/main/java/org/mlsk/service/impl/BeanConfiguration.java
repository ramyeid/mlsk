package org.mlsk.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.engine.EngineFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.mlsk.service.impl.ServiceConfiguration.getEnginePorts;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Configuration
public class BeanConfiguration {

  private static final Logger LOGGER = LogManager.getLogger(BeanConfiguration.class);

  @PostConstruct
  private void postConstruct() {
    LOGGER.info("Service is up");
  }

  @PreDestroy
  private void preDestroy() {
    LOGGER.info("Service will shutdown");
  }

  @Bean
  public Orchestrator buildOrchestrator() {
    List<Engine> engines = getEnginePorts().stream()
        .map(EngineFactory::createEngine)
        .collect(toList());
    return new Orchestrator(engines);
  }

  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        ErrorAttributeOptions newOptions = options.including(MESSAGE);

        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, newOptions);
        errorAttributes.remove("timestamp");
        errorAttributes.remove("path");
        errorAttributes.remove("error");
        return errorAttributes;
      }
    };
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
      }
    };
  }
}
