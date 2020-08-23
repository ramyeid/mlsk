package org.machinelearning.swissknife;

import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.model.ServiceInformation;

public interface Engine extends TimeSeriesAnalysis {

    EngineState getState();

    ServiceInformation getServiceInformation();
}
