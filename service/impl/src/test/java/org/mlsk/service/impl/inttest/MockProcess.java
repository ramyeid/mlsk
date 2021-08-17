package org.mlsk.service.impl.inttest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockProcess {

  private final Process process;
  private final CountDownLatch processRunningLatch;

  public MockProcess() {
    this.process = mock(Process.class);
    this.processRunningLatch = new CountDownLatch(1);
    mockProcess();
  }

  public Process getProcess() {
    return process;
  }

  public void killProcess() {
    processRunningLatch.countDown();
  }

  public void mockProcess() {
    when(process.isAlive()).thenReturn(true);

    when(process.onExit()).thenReturn(CompletableFuture.supplyAsync(() -> {
      try {
        processRunningLatch.await();
      } catch (Exception ignored) {
      }
      return null;
    }));
  }
}
