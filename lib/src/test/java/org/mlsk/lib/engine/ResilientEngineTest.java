package org.mlsk.lib.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.ServiceInformation;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResilientEngineTest {

  private static final String LOGS_PATH = "LogsPath";
  private static final String ENGINE_PATH = "EnginePath";
  private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation("localhost", "6767");

  @Mock
  private EngineLauncher engineLauncher;
  @Mock
  private Process process;
  @Mock
  private CompletableFuture<Process> onExitFuture;

  @BeforeEach
  public void setup() throws IOException {
    when(engineLauncher.launchEngine(SERVICE_INFORMATION, LOGS_PATH, ENGINE_PATH)).thenReturn(process);
    when(process.isAlive()).thenReturn(true);
  }

  @Test
  public void should_launch_process_on_creation() throws IOException, InterruptedException {
    when(process.onExit()).thenReturn(onExitFuture);

    new ResilientEngine(SERVICE_INFORMATION, engineLauncher, LOGS_PATH, ENGINE_PATH, mock(Runnable.class));

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineLauncher).launchEngine(SERVICE_INFORMATION, LOGS_PATH, ENGINE_PATH);
    inOrder.verify(process).waitFor(3, TimeUnit.SECONDS);
    inOrder.verify(process).onExit();
    verify(onExitFuture).thenAcceptAsync(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_process_is_not_alive_after_start() {
    when(process.isAlive()).thenReturn(false);
    when(process.getErrorStream()).thenReturn(new ByteArrayInputStream("Custom Error".getBytes()));

    try {
      new ResilientEngine(SERVICE_INFORMATION, engineLauncher, LOGS_PATH, ENGINE_PATH, mock(Runnable.class));
      fail("should fail since process is not alive");

    } catch (Exception exception) {
      assertInstanceOf(EngineCreationException.class, exception);
      assertEquals("Failed to create engine Custom Error", exception.getMessage());
    }
  }

  private InOrder buildInOrder() {
    return inOrder(process, engineLauncher);
  }
}