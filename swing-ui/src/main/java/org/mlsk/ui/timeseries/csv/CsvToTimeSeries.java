package org.mlsk.ui.timeseries.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.ui.exception.CsvParsingException;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public final class CsvToTimeSeries {

  private CsvToTimeSeries() {
  }

  public static TimeSeriesModel toTimeSeries(String csvAbsolutePath, String dateColumnName, String valueColumnName, String dateFormat) throws CsvParsingException {
    try (CSVReader reader = new CSVReader(new FileReader(csvAbsolutePath))) {
      List<String> header = newArrayList(reader.peek());
      assertHeaderContains(header, dateColumnName);
      assertHeaderContains(header, valueColumnName);

      Map<String, String> columnMapping = new HashMap<>();
      columnMapping.put(dateColumnName, "date");
      columnMapping.put(valueColumnName, "value");

      HeaderColumnNameTranslateMappingStrategy<TimeSeriesRowModel> beanStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
      beanStrategy.setType(TimeSeriesRowModel.class);
      beanStrategy.setColumnMapping(columnMapping);

      List<TimeSeriesRowModel> rows = new CsvToBeanBuilder<TimeSeriesRowModel>(reader)
          .withType(TimeSeriesRowModel.class)
          .withIgnoreLeadingWhiteSpace(true)
          .withMappingStrategy(beanStrategy)
          .build()
          .parse();

      return new TimeSeriesModel()
          .rows(rows)
          .dateColumnName(dateColumnName)
          .valueColumnName(valueColumnName)
          .dateFormat(dateFormat);
    } catch (CsvParsingException exception) {
      throw exception;
    } catch (Exception exception) {
      throw new CsvParsingException(exception);
    }
  }

  private static void assertHeaderContains(List<String> header, String columnName) throws CsvParsingException {
    if (!header.contains(columnName)) {
      throw new CsvParsingException(format("Header does not contain '%s' column", columnName));
    }
  }
}
