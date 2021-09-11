package org.mlsk.ui.component.button;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.CANCEL_OPTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CsvFileChooserButtonTest {

  @Mock
  private JFileChooser fileChooser;
  @Mock
  private Consumer<String> onFileChosen;

  private CsvFileChooserButton csvFileChooserButton;

  @BeforeEach
  public void setUp() {
    this.csvFileChooserButton = new CsvFileChooserButton(fileChooser, onFileChosen);
  }

  @Test
  public void should_show_file_chooser_on_action_performed() {
    ActionEvent actionEvent = mock(ActionEvent.class);
    onShowOpenDialogReturn(CANCEL_OPTION);

    csvFileChooserButton.actionPerformed(actionEvent);

    InOrder inOrder = buildInOrder();
    verifyCreation(inOrder, fileChooser);
    inOrder.verify(fileChooser).showOpenDialog(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_call_on_file_chosen_callback_on_action_performed_with_approve_option() {
    ActionEvent actionEvent = mock(ActionEvent.class);
    onShowOpenDialogReturn(APPROVE_OPTION);
    onGetSelectedFileReturn("myFile.csv");

    csvFileChooserButton.actionPerformed(actionEvent);

    InOrder inOrder = buildInOrder();
    verifyCreation(inOrder, fileChooser);
    inOrder.verify(fileChooser).showOpenDialog(any());
    inOrder.verify(fileChooser).getSelectedFile();
    inOrder.verify(onFileChosen).accept("myFile.csv");
    inOrder.verifyNoMoreInteractions();
  }

  private void onShowOpenDialogReturn(int option) {
    when(fileChooser.showOpenDialog(any())).thenReturn(option);
  }

  private void onGetSelectedFileReturn(String absolutePath) {
    File file = mock(File.class);
    when(fileChooser.getSelectedFile()).thenReturn(file);
    when(file.getAbsolutePath()).thenReturn(absolutePath);
  }

  private InOrder buildInOrder() {
    return inOrder(fileChooser, onFileChosen);
  }

  private static void verifyCreation(InOrder inOrder, JFileChooser fileChooser) {
    inOrder.verify(fileChooser).setAcceptAllFileFilterUsed(false);
    inOrder.verify(fileChooser).addChoosableFileFilter(argThat(fileFilterMatcher(new FileNameExtensionFilter("Csv Files", "csv"))));
  }

  private static ArgumentMatcher<FileNameExtensionFilter> fileFilterMatcher(FileNameExtensionFilter expected) {
    return actual -> actual.getDescription().equals(expected.getDescription())
        && Arrays.equals(actual.getExtensions(), expected.getExtensions());
  }
}