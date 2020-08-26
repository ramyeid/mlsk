package org.machinelearning.swissknife.service.engine.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.service.ServiceConfiguration;
import org.machinelearning.swissknife.service.engine.exceptions.EngineCreationException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResilientProcessTest {

    private static final String LOGS_PATH = "LogsPath";
    private static final String ENGINE_PATH = "EnginePath";
    private static final String ENGINE_PORT = "6767";
    private static final List<String> ENGINE_PORTS = Collections.singletonList(ENGINE_PORT);
    private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation("localhost", "6767");

    @Mock
    private ProcessBuilder processBuilder;
    @Mock
    private Process process;
    @Mock
    private CompletableFuture<Process> onExitFuture;

    @BeforeEach
    public void setup() throws IOException {
        ServiceConfiguration.buildForTest(LOGS_PATH, ENGINE_PATH, ENGINE_PORTS);
        when(processBuilder.command(ArgumentMatchers.<String>any())).thenReturn(processBuilder);
        when(processBuilder.directory(any())).thenReturn(processBuilder);
        when(processBuilder.start()).thenReturn(process);
        when(process.isAlive()).thenReturn(true);
    }

    @Test
    public void should_launch_process_on_creation() throws IOException, InterruptedException {
        when(process.onExit()).thenReturn(onExitFuture);

        new ResilientProcess(SERVICE_INFORMATION, processBuilder, mock(Runnable.class));

        verify(processBuilder).command("python3", "engine.py", "--port", ENGINE_PORT, "--logs-path", LOGS_PATH);
        verify(processBuilder).directory(new File(ENGINE_PATH));
        verify(processBuilder).start();
    }

    @Test
    public void should_assign_on_killed_callback_to_process() throws IOException, InterruptedException {
        when(process.onExit()).thenReturn(onExitFuture);

        new ResilientProcess(SERVICE_INFORMATION, processBuilder, mock(Runnable.class));

        verify(process).onExit();
        verify(onExitFuture).thenAcceptAsync(any());
    }

    @Test
    public void should_throw_exception_if_process_is_not_alive_after_start() throws IOException, InterruptedException {
        when(process.isAlive()).thenReturn(false);
        when(process.getErrorStream()).thenReturn(new ByteArrayInputStream("Error".getBytes()));

        try {
            new ResilientProcess(SERVICE_INFORMATION, processBuilder, mock(Runnable.class));
            fail("should fail since process is not alive");
        } catch (EngineCreationException ignored) {
        }
    }
}