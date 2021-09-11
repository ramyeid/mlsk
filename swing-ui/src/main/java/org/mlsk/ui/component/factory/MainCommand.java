package org.mlsk.ui.component.factory;


import java.util.Arrays;

public enum MainCommand {

  TIME_SERIES_ANALYSIS("TimeSeriesAnalysis"),
  NEURAL_NETWORK("NeuralNetwork"),
  SVM("Support Vector Machine"),
  CONFIGURATION("Configuration"),
  EMPTY("EMPTY");

  private final String title;

  MainCommand(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public static MainCommand fromString(String title) {
    return Arrays.stream(MainCommand.values())
        .filter(t -> t.title.equalsIgnoreCase(title))
        .findFirst()
        .orElse(null);
  }
}
