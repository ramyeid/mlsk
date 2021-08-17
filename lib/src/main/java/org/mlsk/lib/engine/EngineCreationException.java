package org.mlsk.lib.engine;

import static java.lang.String.format;

public class EngineCreationException extends RuntimeException {

  public EngineCreationException(String message) {
    super(format("Failed to create engine %s", message));
  }
}
