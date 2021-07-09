package org.machinelearning.swissknife.ui.components.utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CsvFileChooserButton extends JButton implements ActionListener {

  private final JComponent parentComponent;
  private final JTextField fileLocationTextField;
  private final JFileChooser fileChooser;

  public CsvFileChooserButton(JComponent parentComponent, JTextField fileLocationTextField) {
    super("Choose file");
    this.parentComponent = parentComponent;
    this.fileLocationTextField = fileLocationTextField;
    this.fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Csv Files", "csv"));
    this.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int returnValue = fileChooser.showOpenDialog(parentComponent);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      fileLocationTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }
    SwingUtilities.updateComponentTreeUI(parentComponent);
  }
}
