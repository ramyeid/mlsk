package org.mlsk.lib.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.Endpoint;
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
public class ResilientEngineProcessTest {

  private static final String LOGS_PATH = "LogsPath";
  private static final String ENGINE_PATH = "EnginePath";
  private static final Endpoint ENDPOINT = new Endpoint("localhost", 6767L);

  @Mock
  private EngineLauncher engineLauncher;
  @Mock
  private Process process;
  @Mock
  private CompletableFuture<Process> onExitFuture;

  private ResilientEngineProcess resilientEngineProcess;

  @BeforeEach
  public void setUp() throws IOException {
    this.resilientEngineProcess = new ResilientEngineProcess(ENDPOINT, engineLauncher, LOGS_PATH, ENGINE_PATH);
  }

  @Test
  public void should_launch_process() throws Exception {
    onLaunchEngineReturn(process);
    onIsProcessAliveReturn(true);
    onProcessExitReturn(onExitFuture);

    resilientEngineProcess.launchEngine(mock(Runnable.class));

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineLauncher).launchEngine(ENDPOINT, LOGS_PATH, ENGINE_PATH);
    inOrder.verify(process).waitFor(3, TimeUnit.SECONDS);
    inOrder.verify(process).onExit();
    inOrder.verify(onExitFuture).thenAcceptAsync(any());
    inOrder.verify(process).pid();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_throw_exception_if_process_is_not_alive_after_start() throws IOException {
    onLaunchEngineReturn(process);
    onIsProcessAliveReturn(false);
    when(process.getErrorStream()).thenReturn(new ByteArrayInputStream("Custom Error".getBytes()));

    try {
      resilientEngineProcess.launchEngine(mock(Runnable.class));
      fail("should fail since process is not alive");

    } catch (Exception exception) {
      assertInstanceOf(EngineCreationException.class, exception);
      assertEquals("Failed to create engine Custom Error", exception.getMessage());
    }
  }

  @Test
  public void should_return_engine_down_if_process_null() {

    boolean actualValue = resilientEngineProcess.isEngineUp();

    assertFalse(actualValue);
  }

  @Test
  public void should_return_engine_down_if_process_instantiated_but_down() throws Exception {
    onLaunchEngineReturn(process);
    onIsProcessAliveReturn(true);
    onProcessExitReturn(onExitFuture);
    resilientEngineProcess.launchEngine(mock(Runnable.class));
    onIsProcessAliveReturn(false);

    boolean engineUp = resilientEngineProcess.isEngineUp();

    assertFalse(engineUp);
  }

  @Test
  public void should_return_engine_up_if_process_instantiated_and_up() throws Exception {
    onLaunchEngineReturn(process);
    onIsProcessAliveReturn(true);
    onProcessExitReturn(onExitFuture);
    resilientEngineProcess.launchEngine(mock(Runnable.class));

    boolean engineUp = resilientEngineProcess.isEngineUp();

    assertTrue(engineUp);
  }

  private void onLaunchEngineReturn(Process process) throws IOException {
    when(engineLauncher.launchEngine(ENDPOINT, LOGS_PATH, ENGINE_PATH)).thenReturn(process);
  }

  private void onProcessExitReturn(CompletableFuture<Process> future) {
    when(process.onExit()).thenReturn(future);
  }

  private void onIsProcessAliveReturn(boolean isAlive) {
    when(process.isAlive()).thenReturn(isAlive);
  }

  private InOrder buildInOrder() {
    return inOrder(process, engineLauncher, onExitFuture);
  }
}