package org.machinelearning.swissknife.model.timeseries;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class TimeSeries {

    private final List<TimeSeriesRow> rows;
    private final String dateColumnName;
    private final String valueColumnName;
    private final String dateFormat;

    public TimeSeries(List<TimeSeriesRow> rows, String dateColumnName, String valueColumnName, String dateFormat) {
        this.rows = rows;
        this.dateColumnName = dateColumnName;
        this.valueColumnName = valueColumnName;
        this.dateFormat = dateFormat;
    }

    public TimeSeries() {
        this(new ArrayList<>(), "", "", "");
    }

    public static TimeSeries concat(TimeSeries firstTimeSeries, TimeSeries secondTimeSeries) {
        List<TimeSeriesRow> concatenatedRows = Stream.concat(firstTimeSeries.rows.stream(), secondTimeSeries.rows.stream())
                                                    .collect(Collectors.toList());
        return new TimeSeries(concatenatedRows, firstTimeSeries.dateColumnName, firstTimeSeries.valueColumnName, firstTimeSeries.dateFormat);
    }

    public List<TimeSeriesRow> getRows() {
        return rows;
    }

    public String getDateColumnName() {
        return dateColumnName;
    }

    public String getValueColumnName() {
        return valueColumnName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSeries that = (TimeSeries) o;
        return Objects.equals(rows, that.rows) &&
                Objects.equals(dateColumnName, that.dateColumnName) &&
                Objects.equals(valueColumnName, that.valueColumnName) &&
                Objects.equals(dateFormat, that.dateFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, dateColumnName, valueColumnName, dateFormat);
    }

    @Override
    public String toString() {
        return "TimeSeries{" +
                "rows=" + rows +
                ", dateColumnName='" + dateColumnName + '\'' +
                ", valueColumnName='" + valueColumnName + '\'' +
                ", dateFormat='" + dateFormat + '\'' +
                '}';
    }

    public static TimeSeries buildFromCsv(String csvAbsolutePath, String dateColumnName, String valueColumnName, String dateFormat) throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(csvAbsolutePath))) {
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
        }
    }
}
