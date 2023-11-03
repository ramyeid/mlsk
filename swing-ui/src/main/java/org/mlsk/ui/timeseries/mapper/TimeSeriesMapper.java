package org.mlsk.ui.timeseries.mapper;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;

import java.text.SimpleDateFormat;

import static java.lang.String.format;

public final class TimeSeriesMapper {

  private TimeSeriesMapper() {
  }

  public static TimeSeries toTimeSeries(TimeSeriesModel timeSeriesIn,
                                        String timeSeriesTitle) {
    try {
      TimeSeries series = new TimeSeries(timeSeriesTitle);
      SimpleDateFormat dateFormatter = new SimpleDateFormat(timeSeriesIn.getDateFormat());

      for (TimeSeriesRowModel row : timeSeriesIn.getRows()) {
        Millisecond period = new Millisecond(dateFormatter.parse(row.getDate()));
        series.add(new TimeSeriesDataItem(period, row.getValue()));
      }

      return series;
    } catch (Exception exception) {
      throw new MappingTimeSeriesException(exception);
    }
  }
}
