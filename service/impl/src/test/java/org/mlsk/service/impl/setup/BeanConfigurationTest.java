package org.mlsk.service.impl.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.impl.orchestrator.factory.OrchestratorFactory;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeanConfigurationTest {

  @Mock
  private OrchestratorFactory orchestratorFactory;

  private BeanConfiguration beanConfiguration;

  @BeforeEach
  public void setUp() {
    this.beanConfiguration = new BeanConfiguration(orchestratorFactory);
  }

  @Test
  public void should_correctly_build_orchestrator() {

    beanConfiguration.buildOrchestrator();

    InOrder inOrder = inOrder(orchestratorFactory);
    inOrder.verify(orchestratorFactory).buildAndLaunchOrchestrator();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_correctly_build_error_attributes() {
    WebRequest webRequest = mock(WebRequest.class);
    ErrorAttributeOptions emptyErrorAttributeOptions = ErrorAttributeOptions.defaults();

    ErrorAttributes errorAttributes = beanConfiguration.errorAttributes();
    Map<String, Object> actualErrorAttributes = errorAttributes.getErrorAttributes(webRequest, emptyErrorAttributeOptions);

    assertInstanceOf(DefaultErrorAttributes.class, errorAttributes);
    assertEquals(buildExpectedErrorAttributes(), actualErrorAttributes);
  }

  @Test
  public void should_correctly_build_web_mvc_configurer() {
    CorsRegistration corsRegistration = mockCorsRegistration();
    CorsRegistry corsRegistry = mockCorsRegistry(corsRegistration);

    WebMvcConfigurer webMvcConfigurer = beanConfiguration.webMvcConfigurer();
    webMvcConfigurer.addCorsMappings(corsRegistry);

    InOrder inOrder = inOrder(corsRegistry, corsRegistration);
    inOrder.verify(corsRegistry).addMapping("/**");
    inOrder.verify(corsRegistration).allowedOrigins("*");
    inOrder.verify(corsRegistration).allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
    inOrder.verifyNoMoreInteractions();
  }

  private CorsRegistration mockCorsRegistration() {
    CorsRegistration corsRegistration = mock(CorsRegistration.class);
    when(corsRegistration.allowedOrigins(any())).thenReturn(corsRegistration);
    return corsRegistration;
  }

  private CorsRegistry mockCorsRegistry(CorsRegistration corsRegistration) {
    CorsRegistry corsRegistry = mock(CorsRegistry.class);
    when(corsRegistry.addMapping(any())).thenReturn(corsRegistration);
    return corsRegistry;
  }

  private static Map<String, Object> buildExpectedErrorAttributes() {
    Map<String, Object> expectedErrorAttributes = newHashMap();
    expectedErrorAttributes.put("status", 999);
    expectedErrorAttributes.put("message", "No message available");
    return expectedErrorAttributes;
  }
}