package org.mlsk.lib.engine.launcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.ServiceInformation;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EngineLauncherTest {

  @Mock
  private ProcessBuilder processBuilder;

  private EngineLauncher engineLauncher;

  @BeforeEach
  public void setUp() {
    engineLauncher = new EngineLauncher(processBuilder);
    when(processBuilder.command(ArgumentMatchers.<String>any())).thenReturn(processBuilder);
    when(processBuilder.directory(any())).thenReturn(processBuilder);
  }

  @Test
  public void should_call_process_builder_on_launch_engine() throws IOException {
    ServiceInformation serviceInformation = new ServiceInformation("host", 123L);
    String logsPath = "logsPath";
    String enginePath = "enginePath";

    engineLauncher.launchEngine(serviceInformation, logsPath, enginePath);

    InOrder inOrder = buildInOrder();
    inOrder.verify(processBuilder).command("python3", "engine.py", "--port", "123", "--logs-path", logsPath);
    inOrder.verify(processBuilder).directory(new File(enginePath));
    inOrder.verify(processBuilder).start();
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(processBuilder);
  }
}