package org.mlsk.ui.component.button;

import com.google.common.annotations.VisibleForTesting;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public final class CsvFileChooserButton extends JButton implements ActionListener {

  private final JFileChooser fileChooser;
  private final Consumer<String> onFileChosen;

  public static CsvFileChooserButton buildCsvFileChooserButton(Consumer<String> onChooseFile) {
    return new CsvFileChooserButton(onChooseFile);
  }

  private CsvFileChooserButton(Consumer<String> onFileChosen) {
    this(new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()), onFileChosen);
  }

  @VisibleForTesting
  CsvFileChooserButton(JFileChooser fileChooser, Consumer<String> onFileChosen) {
    super("Choose file");
    this.fileChooser = fileChooser;
    this.onFileChosen = onFileChosen;

    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Csv Files", "csv"));
    this.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent ignored) {
    int returnValue = fileChooser.showOpenDialog(SwingUtilities.getRoot(this));
    if (returnValue == APPROVE_OPTION) {
      onFileChosen.accept(fileChooser.getSelectedFile().getAbsolutePath());
    }
  }
}
