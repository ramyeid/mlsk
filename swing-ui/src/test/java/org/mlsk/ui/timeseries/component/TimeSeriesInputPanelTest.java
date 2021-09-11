package org.mlsk.ui.timeseries.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mlsk.ui.timeseries.request.TimeSeriesAnalysisRequestBuilder;
import org.mlsk.ui.timeseries.request.TimeSeriesAnalysisRequestBuilderException;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisCommand;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisServiceCaller;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisServiceException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.timeseries.service.TimeSeriesAnalysisCommand.PREDICT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesInputPanelTest {

  @Mock
  private TimeSeriesAnalysisServiceCaller serviceCaller;
  @Mock
  private TimeSeriesAnalysisRequestBuilder requestBuilder;
  @Mock
  private JTextField csvAbsolutePathValue;
  @Mock
  private JTextField dateColumnNameValue;
  @Mock
  private JTextField valueColumnNameValue;
  @Mock
  private JTextField dateFormatValue;
  @Mock
  private JTextField numberOfValuesValue;

  private TimeSeriesInputPanel timeSeriesInputPanel;

  @BeforeEach
  public void setUp() {
    this.timeSeriesInputPanel = new TimeSeriesInputPanel(serviceCaller, requestBuilder, csvAbsolutePathValue, dateColumnNameValue,
        valueColumnNameValue, dateFormatValue, numberOfValuesValue);
  }

  @Test
  public void should_call_request_builder_and_service_caller_on_click() {
    ActionEvent actionEvent = mockActionEvent(PREDICT);
    onGetTextReturn(csvAbsolutePathValue, "csvPath");
    onGetTextReturn(dateColumnNameValue, "Date");
    onGetTextReturn(valueColumnNameValue, "Value");
    onGetTextReturn(dateFormatValue, "dateFormat");
    onGetTextReturn(numberOfValuesValue, "123");
    onBuildRequestReturn(buildTimeSeriesRequest());

    timeSeriesInputPanel.actionPerformed(actionEvent);

    InOrder inOrder = inOrder(requestBuilder, serviceCaller);
    inOrder.verify(requestBuilder).buildRequest("Date", "Value", "dateFormat", "csvPath", "123");
    inOrder.verify(serviceCaller).callService(PREDICT, buildTimeSeriesRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_call_try_popup_if_building_request_fails() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
      ActionEvent actionEvent = mockActionEvent(PREDICT);
      onGetTextReturn(csvAbsolutePathValue, "csvPath");
      onGetTextReturn(dateColumnNameValue, "Date");
      onGetTextReturn(valueColumnNameValue, "Value");
      onGetTextReturn(dateFormatValue, "dateFormat");
      onGetTextReturn(numberOfValuesValue, "123");
      doThrowExceptionOnBuildRequest(new TimeSeriesAnalysisRequestBuilderException("errorMessage", new UnsupportedOperationException("errorMessage2")));

      try {
        timeSeriesInputPanel.actionPerformed(actionEvent);
        fail("should fail since request builder threw exception");

      } catch (Exception exception) {
        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals("java.lang.UnsupportedOperationException: errorMessage2", exception.getMessage());
        mockedStatic.verify(() -> JOptionPane.showMessageDialog(null, "Could not build request\nCause:\n\t\terrorMessage", "TimeSeriesAnalysisRequestBuilderException: Could not build request", ERROR_MESSAGE));
      }
    }
  }

  @Test
  public void should_call_try_popup_if_calling_service_fails() {
    try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
      ActionEvent actionEvent = mockActionEvent(PREDICT);
      onGetTextReturn(csvAbsolutePathValue, "csvPath");
      onGetTextReturn(dateColumnNameValue, "Date");
      onGetTextReturn(valueColumnNameValue, "Value");
      onGetTextReturn(dateFormatValue, "dateFormat");
      onGetTextReturn(numberOfValuesValue, "123");
      doThrowExceptionOnCallService(new TimeSeriesAnalysisServiceException("errorMessage", new UnsupportedOperationException("errorMessage2")));

      try {
        timeSeriesInputPanel.actionPerformed(actionEvent);
        fail("should fail since request builder threw exception");

      } catch (Exception exception) {
        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals("java.lang.UnsupportedOperationException: errorMessage2", exception.getMessage());
        mockedStatic.verify(() -> JOptionPane.showMessageDialog(null, "Could not complete PREDICT service\nCause:\n\t\terrorMessage", "TimeSeriesAnalysisServiceException: Could not complete PREDICT service", ERROR_MESSAGE));
      }
    }
  }

  private void onBuildRequestReturn(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    when(requestBuilder.buildRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(timeSeriesAnalysisRequest);
  }

  private void doThrowExceptionOnBuildRequest(TimeSeriesAnalysisRequestBuilderException exception) {
    doThrow(exception).when(requestBuilder).buildRequest(anyString(), anyString(), anyString(), anyString(), anyString());
  }

  private void doThrowExceptionOnCallService(TimeSeriesAnalysisServiceException exception) {
    doThrow(exception).when(serviceCaller).callService(any(), any());
  }

  private static void onGetTextReturn(JTextField textField, String text) {
    when(textField.getText()).thenReturn(text);
  }

  private static ActionEvent mockActionEvent(TimeSeriesAnalysisCommand command) {
    ActionEvent actionEvent = mock(ActionEvent.class);
    when(actionEvent.getActionCommand()).thenReturn(command.getTitle());
    return actionEvent;
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesRequest() {
    TimeSeriesRow row = new TimeSeriesRow("1990", 1.);
    List<TimeSeriesRow> rows = newArrayList(row);

    TimeSeries timeSeries = new TimeSeries(rows, "date", "values", "yyyy");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }
}