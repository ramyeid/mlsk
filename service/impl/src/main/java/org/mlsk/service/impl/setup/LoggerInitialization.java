package org.mlsk.service.impl.setup;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mlsk.service.impl.setup.ServiceConfiguration.getLogLevel;
import static org.mlsk.service.impl.setup.ServiceConfiguration.getLogsPath;

public class LoggerInitialization implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String PATTERN = "[%d{dd-MM-yyyy HH:mm:ss}] [%-5p] [%c] - %m%n";
  private static final String LOG_FILE_NAME = "/service.log";
  private static final String LOG_FILE_PATTERN = "/service-%d{yyyy-MM-dd}-%i.log";
  private static final String ROLLING_FILE_APPENDER_NAME = "FileAppender";

  private final ConfigurationBuilder<BuiltConfiguration> builder;

  public LoggerInitialization() {
    this(ConfigurationBuilderFactory.newConfigurationBuilder());
  }

  @VisibleForTesting
  public LoggerInitialization(ConfigurationBuilder<BuiltConfiguration> builder) {
    this.builder = builder;
  }

  @Override
  public void initialize(ConfigurableApplicationContext ignored) {
    ComponentBuilder<?> triggeringPolicy = buildSizeBasedTriggeringPolicy(builder);
    LayoutComponentBuilder layoutBuilder = buildPatternLayout(builder);
    AppenderComponentBuilder appenderBuilder = buildRollingFileAppender(builder, triggeringPolicy, layoutBuilder);
    RootLoggerComponentBuilder rootLogger = buildRootLogger(builder);

    BuiltConfiguration configuration = builder.setStatusLevel(Level.valueOf(getLogLevel()))
        .setConfigurationName("RollingBuilder")
        .add(appenderBuilder)
        .add(rootLogger)
        .build();

    reconfigure(configuration);
  }

  @VisibleForTesting
  void reconfigure(BuiltConfiguration configuration) {
    Configurator.reconfigure(configuration);
  }

  private static RootLoggerComponentBuilder buildRootLogger(ConfigurationBuilder<BuiltConfiguration> builder) {
    return builder.newRootLogger(Level.valueOf(getLogLevel()))
        .add(builder.newAppenderRef(ROLLING_FILE_APPENDER_NAME));
  }

  private static AppenderComponentBuilder buildRollingFileAppender(ConfigurationBuilder<BuiltConfiguration> builder, ComponentBuilder<?> triggeringPolicy, LayoutComponentBuilder layoutBuilder) {
    return builder.newAppender(ROLLING_FILE_APPENDER_NAME, "RollingFile")
        .addAttribute("fileName", getLogsPath() + LOG_FILE_NAME)
        .addAttribute("filePattern", getLogsPath() + LOG_FILE_PATTERN)
        .add(layoutBuilder)
        .addComponent(triggeringPolicy);
  }

  private static LayoutComponentBuilder buildPatternLayout(ConfigurationBuilder<BuiltConfiguration> builder) {
    return builder.newLayout("PatternLayout").addAttribute("pattern", PATTERN);
  }

  private static ComponentBuilder<?> buildSizeBasedTriggeringPolicy(ConfigurationBuilder<BuiltConfiguration> builder) {
    return builder.newComponent("Policies")
        .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "500MB"));
  }
}
