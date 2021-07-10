package org.mlsk.service.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

public class LoggerConfiguration {

  public static void setUpLogger(String logsPath) throws IOException {
    Logger rootLogger = Logger.getRootLogger();
    rootLogger.setLevel(Level.INFO);
    PatternLayout layout = new PatternLayout("[%d{dd-MM-yyyy HH:mm:ss}] [%-5p] [%c] - %m%n");
    String logFile = String.format("%s/service.log", logsPath);
    RollingFileAppender fileAppender = new RollingFileAppender(layout, logFile);
    rootLogger.addAppender(fileAppender);
  }
}
