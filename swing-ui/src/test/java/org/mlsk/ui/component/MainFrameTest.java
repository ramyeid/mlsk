package org.mlsk.ui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.ui.component.factory.PanelFactory;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.mlsk.ui.component.factory.MainCommand.EMPTY;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainFrameTest {

  @Mock
  private JPanel algorithmPanel;

  @Mock
  private MainFrame mainFrame;

  @BeforeEach
  public void setUp() {
    doCallRealMethod().when(mainFrame).actionPerformed(any());
    doCallRealMethod().when(mainFrame).setAlgorithmPanel(any());
    mainFrame.setAlgorithmPanel(algorithmPanel);
  }

  @Test
  public void should_clear_algorithm_panel_and_add_new_panel_on_action() {
    try (MockedStatic<SwingUtilities> swingUtilitiesMockedStatic = Mockito.mockStatic(SwingUtilities.class)) {
      try (MockedStatic<PanelFactory> panelFactoryMockedStatic = Mockito.mockStatic(PanelFactory.class)) {
        panelFactoryMockedStatic.when(() -> PanelFactory.buildPanel(any())).thenReturn(mock(JPanel.class));
        ActionEvent actionEvent = mockActionEvent();

        mainFrame.actionPerformed(actionEvent);

        InOrder inOrder = buildInOrder();
        inOrder.verify(algorithmPanel).removeAll();
        inOrder.verify(algorithmPanel).add(any(JPanel.class));
        inOrder.verify(algorithmPanel).setVisible(true);
        inOrder.verifyNoMoreInteractions();
        swingUtilitiesMockedStatic.verify(() -> SwingUtilities.updateComponentTreeUI(mainFrame));
      }
    }
  }

  private InOrder buildInOrder() {
    return inOrder(algorithmPanel);
  }

  private static ActionEvent mockActionEvent() {
    ActionEvent actionEvent = mock(ActionEvent.class);
    when(actionEvent.getActionCommand()).thenReturn(EMPTY.getTitle());
    return actionEvent;
  }
}