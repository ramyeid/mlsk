package org.mlsk.ui.helper;

public class CsvParsingException extends Exception {

  public CsvParsingException(Exception cause) {
    super(cause);
  }

  public CsvParsingException(String message) {
    super(message);
  }
}
