package org.mlsk.ui.configuration.service;

import java.util.Arrays;

public enum ConfigurationCommand {

  SAVE("Save"),
  CANCEL("Cancel");

  private final String title;

  ConfigurationCommand(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public static ConfigurationCommand fromString(String title) {
    return Arrays.stream(ConfigurationCommand.values())
        .filter(t -> t.title.equalsIgnoreCase(title))
        .findFirst()
        .orElse(null);
  }
}
