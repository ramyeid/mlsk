package org.machinelearning.swissknife.service;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.service.engine.EngineFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.machinelearning.swissknife.service.LoggerConfiguration.setUpLogger;
import static org.machinelearning.swissknife.service.ServiceConfiguration.getLogsPath;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class);

    @Bean
    public Orchestrator buildOrchestrator() {
        List<Engine> engines = ServiceConfiguration.getEnginePorts()
            .stream()
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

    @PostConstruct
    private void postConstruct() {
        LOGGER.info("Service is up");
    }

    @PreDestroy
    private void preDestroy() {
        LOGGER.info("Service will shutdown");
    }

    public static void main(String... args) throws ParseException, IOException {
        ServiceConfiguration.buildServiceConfiguration(args);
        setUpLogger(getLogsPath());
        SpringApplication.run(Application.class, args);
    }
}
