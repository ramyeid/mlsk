package org.machinelearning.swissknife;

public interface Engine extends TimeSeriesAnalysis {

    EngineState getState();

    ServiceInformation getServiceInformation();
}
