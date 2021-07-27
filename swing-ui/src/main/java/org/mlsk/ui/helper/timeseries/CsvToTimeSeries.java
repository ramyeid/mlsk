package org.mlsk.ui.helper.timeseries;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mlsk.ui.helper.CsvParsingException;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class CsvToTimeSeries {

  public static TimeSeries toTimeSeries(String csvAbsolutePath, String dateColumnName, String valueColumnName, String dateFormat) throws CsvParsingException {
    try (CSVReader reader = new CSVReader(new FileReader(csvAbsolutePath))) {
      List<String> header = newArrayList(reader.peek());
      assertHeaderContains(header, dateColumnName);
      assertHeaderContains(header, valueColumnName);

      Map<String, String> columnMapping = new HashMap<>();
      columnMapping.put(dateColumnName, "date");
      columnMapping.put(valueColumnName, "value");

      HeaderColumnNameTranslateMappingStrategy<TimeSeriesRow> beanStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
      beanStrategy.setType(TimeSeriesRow.class);
      beanStrategy.setColumnMapping(columnMapping);

      List<TimeSeriesRow> rows = new CsvToBeanBuilder<TimeSeriesRow>(reader)
          .withType(TimeSeriesRow.class)
          .withIgnoreLeadingWhiteSpace(true)
          .withMappingStrategy(beanStrategy)
          .build()
          .parse();

      return new TimeSeries(rows, dateColumnName, valueColumnName, dateFormat);
    } catch (CsvParsingException exception) {
      throw exception;
    } catch (Exception exception) {
      throw new CsvParsingException(exception);
    }
  }

  private static void assertHeaderContains(List<String> header, String columnName) throws CsvParsingException {
    if (!header.contains(columnName)) {
      throw new CsvParsingException(String.format("Header does not contain '%s' column", columnName));
    }
  }
}
