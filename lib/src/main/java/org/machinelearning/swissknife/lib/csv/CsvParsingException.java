package org.machinelearning.swissknife.lib.csv;

public class CsvParsingException extends Exception{
    public CsvParsingException(Exception cause) {
        super(cause);
    }
    public CsvParsingException(String message) {
        super(message);
    }
}
