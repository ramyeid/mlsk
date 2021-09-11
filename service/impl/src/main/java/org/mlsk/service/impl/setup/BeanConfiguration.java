package org.mlsk.service.impl.setup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.orchestrator.factory.OrchestratorFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@Configuration
public class BeanConfiguration {

  private static final Logger LOGGER = LogManager.getLogger(BeanConfiguration.class);

  private final OrchestratorFactory orchestratorFactory;

  @Autowired
  public BeanConfiguration(OrchestratorFactory orchestratorFactory) {
    this.orchestratorFactory = orchestratorFactory;
  }

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
    return orchestratorFactory.buildAndLaunchOrchestrator();
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
  public WebMvcConfigurer webMvcConfigurer() {
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
