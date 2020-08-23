package org.machinelearning.swissknife.service.engine.deployment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngineCreatorTest {

    private static final long PROCESS_PID = 339L;
    private static final String PORT = "6767";
    @Mock
    private ProcessBuilder processBuilder;
    @Mock
    private Process process;

    @BeforeEach
    public void setup() throws IOException {
        when(processBuilder.command(ArgumentMatchers.<String>any())).thenReturn(processBuilder);
        when(processBuilder.directory(any())).thenReturn(processBuilder);
        when(processBuilder.start()).thenReturn(process);
        when(process.pid()).thenReturn(PROCESS_PID);
    }

    @Test
    public void should_return_engine_with_correct_service_information() throws IOException {
        EngineCreator engineCreator = new EngineCreator(processBuilder);

        Engine engine = engineCreator.createEngine(PORT);
        ServiceInformation actualServiceInformation = engine.getServiceInformation();

        ServiceInformation expectedServiceInformation = new ServiceInformation("localhost", PORT, String.valueOf(PROCESS_PID));
        assertEquals(expectedServiceInformation, actualServiceInformation);
    }

    @Test
    public void should_launch_python_engine_on_startup() throws IOException {
        EngineCreator engineCreator = new EngineCreator(processBuilder);

        engineCreator.createEngine(PORT);

        verify(processBuilder).command("python3", "engine.py", "--port", PORT);
        verify(processBuilder).directory(new File("components/engine"));
        verify(processBuilder).start();
    }
}