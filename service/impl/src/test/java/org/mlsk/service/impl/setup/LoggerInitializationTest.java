package org.mlsk.service.impl.setup;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import static org.apache.logging.log4j.Level.INFO;
import static org.mlsk.service.impl.setup.ServiceConfiguration.buildServiceConfiguration;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggerInitializationTest {

  @Mock
  private ConfigurationBuilder<BuiltConfiguration> builder;

  private LoggerInitialization loggerInitialization;

  @BeforeEach
  public void setUp() {
    this.loggerInitialization = spy(new LoggerInitialization(builder));
    doNothing().when(loggerInitialization).reconfigure(any());
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void should_correctly_build_logger_configuration() throws ParseException {
    buildServiceConfiguration("", "--engine-ports", "ports", "--logs-path", "logsPath", "-engine-path", "enginePath");
    mockBuilder();
    ComponentBuilder policyComponentBuilder = mockPolicyComponentBuilder();
    ComponentBuilder sizeBasedTriggerPolicy = mockSizeBasedTriggerPolicy();
    LayoutComponentBuilder layoutComponentBuilder = mockLayoutComponentBuilder();
    AppenderComponentBuilder appenderComponentBuilder = mockAppenderComponentBuilder();
    RootLoggerComponentBuilder rootLoggerComponentBuilder = mockRootLoggerComponentBuilder();
    AppenderRefComponentBuilder appenderRefComponentBuilder = mockAppenderRefComponentBuilder();

    loggerInitialization.initialize(mock(ConfigurableApplicationContext.class));

    InOrder inOrder = inOrder(builder, policyComponentBuilder, sizeBasedTriggerPolicy, layoutComponentBuilder, appenderComponentBuilder, rootLoggerComponentBuilder, appenderRefComponentBuilder);
    verifyOnPolicy(policyComponentBuilder, sizeBasedTriggerPolicy, inOrder);
    verifyOnPattern(layoutComponentBuilder, inOrder);
    verifyOnRollingFileAppender(policyComponentBuilder, layoutComponentBuilder, appenderComponentBuilder, inOrder);
    verifyOnRootLogger(inOrder, rootLoggerComponentBuilder, appenderRefComponentBuilder);
    inOrder.verify(builder).setStatusLevel(INFO);
    inOrder.verify(builder).setConfigurationName("RollingBuilder");
    inOrder.verify(builder).add(appenderComponentBuilder);
    inOrder.verify(builder).add(rootLoggerComponentBuilder);
    inOrder.verify(builder).build();
    inOrder.verifyNoMoreInteractions();
  }

  private void verifyOnRootLogger(InOrder inOrder, RootLoggerComponentBuilder rootLoggerComponentBuilder, AppenderRefComponentBuilder appenderRefComponentBuilder) {
    inOrder.verify(builder).newRootLogger(INFO);
    inOrder.verify(builder).newAppenderRef("FileAppender");
    inOrder.verify(rootLoggerComponentBuilder).add(appenderRefComponentBuilder);

  }

  @SuppressWarnings("rawtypes")
  private void verifyOnRollingFileAppender(ComponentBuilder policyComponentBuilder, LayoutComponentBuilder layoutComponentBuilder, AppenderComponentBuilder appenderComponentBuilder, InOrder inOrder) {
    inOrder.verify(builder).newAppender("FileAppender", "RollingFile");
    inOrder.verify(appenderComponentBuilder).addAttribute("fileName", "logsPath/service.log");
    inOrder.verify(appenderComponentBuilder).addAttribute("filePattern", "logsPath/service-%d{yyyy-MM-dd}-%i.log");
    inOrder.verify(appenderComponentBuilder).add(layoutComponentBuilder);
    inOrder.verify(appenderComponentBuilder).addComponent(policyComponentBuilder);
  }

  private void verifyOnPattern(LayoutComponentBuilder layoutComponentBuilder, InOrder inOrder) {
    inOrder.verify(builder).newLayout("PatternLayout");
    inOrder.verify(layoutComponentBuilder).addAttribute("pattern", "[%d{dd-MM-yyyy HH:mm:ss}] [%-5p] [%c] - %m%n");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void verifyOnPolicy(ComponentBuilder policyComponentBuilder, ComponentBuilder sizeBasedTriggerPolicy, InOrder inOrder) {
    inOrder.verify(builder).newComponent("Policies");
    inOrder.verify(builder).newComponent("SizeBasedTriggeringPolicy");
    inOrder.verify(sizeBasedTriggerPolicy).addAttribute("size", "500MB");
    inOrder.verify(policyComponentBuilder).addComponent(sizeBasedTriggerPolicy);
  }

  private void mockBuilder() {
    when(builder.setStatusLevel(INFO)).thenReturn(builder);
    when(builder.setConfigurationName("RollingBuilder")).thenReturn(builder);
    when(builder.add(any(AppenderComponentBuilder.class))).thenReturn(builder);
    when(builder.add(any(RootLoggerComponentBuilder.class))).thenReturn(builder);
  }

  private AppenderRefComponentBuilder mockAppenderRefComponentBuilder() {
    AppenderRefComponentBuilder appenderRefComponentBuilder = mock(AppenderRefComponentBuilder.class);
    when(builder.newAppenderRef("FileAppender")).thenReturn(appenderRefComponentBuilder);
    return appenderRefComponentBuilder;
  }

  private RootLoggerComponentBuilder mockRootLoggerComponentBuilder() {
    RootLoggerComponentBuilder rootLoggerComponentBuilder = mock(RootLoggerComponentBuilder.class);
    when(builder.newRootLogger(INFO)).thenReturn(rootLoggerComponentBuilder);
    when(rootLoggerComponentBuilder.add(any(AppenderRefComponentBuilder.class))).thenReturn(rootLoggerComponentBuilder);
    return rootLoggerComponentBuilder;
  }

  private AppenderComponentBuilder mockAppenderComponentBuilder() {
    AppenderComponentBuilder appenderComponentBuilder = mock(AppenderComponentBuilder.class);
    when(builder.newAppender("FileAppender", "RollingFile")).thenReturn(appenderComponentBuilder);
    when(appenderComponentBuilder.addAttribute(anyString(), anyString())).thenReturn(appenderComponentBuilder);
    when(appenderComponentBuilder.add(any(LayoutComponentBuilder.class))).thenReturn(appenderComponentBuilder);
    when(appenderComponentBuilder.addComponent(any())).thenReturn(appenderComponentBuilder);
    return appenderComponentBuilder;
  }

  private LayoutComponentBuilder mockLayoutComponentBuilder() {
    LayoutComponentBuilder layoutComponentBuilder = mock(LayoutComponentBuilder.class);
    when(builder.newLayout("PatternLayout")).thenReturn(layoutComponentBuilder);
    when(layoutComponentBuilder.addAttribute(anyString(), anyString())).thenReturn(layoutComponentBuilder);
    return layoutComponentBuilder;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private ComponentBuilder mockPolicyComponentBuilder() {
    ComponentBuilder policyComponentBuilder = mock(ComponentBuilder.class);
    when(builder.newComponent("Policies")).thenReturn(policyComponentBuilder);
    when(policyComponentBuilder.addComponent(any())).thenReturn(policyComponentBuilder);
    return policyComponentBuilder;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private ComponentBuilder mockSizeBasedTriggerPolicy() {
    ComponentBuilder sizeBasedTriggerPolicy = mock(ComponentBuilder.class);
    when(builder.newComponent("SizeBasedTriggeringPolicy")).thenReturn(sizeBasedTriggerPolicy);
    when(sizeBasedTriggerPolicy.addAttribute(anyString(), anyString())).thenReturn(sizeBasedTriggerPolicy);
    return sizeBasedTriggerPolicy;
  }
}