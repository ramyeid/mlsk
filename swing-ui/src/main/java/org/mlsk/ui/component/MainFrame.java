package org.mlsk.ui.component;

import com.google.common.annotations.VisibleForTesting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static org.mlsk.ui.component.builder.GridBagConstraintsBuilder.buildGridBagConstraints;
import static org.mlsk.ui.component.builder.JButtonBuilder.buildJButton;
import static org.mlsk.ui.component.factory.MainCommand.*;
import static org.mlsk.ui.component.factory.PanelFactory.buildPanel;

public class MainFrame extends JFrame implements ActionListener {

  private JPanel algorithmPanel;

  public MainFrame() {
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setSize(200, 600);
    this.setLayout(new BorderLayout());

    JButton openTimeSeriesButton = buildJButton(TIME_SERIES_ANALYSIS.getTitle(), this);
    JButton openNeuralNetwork = buildJButton(NEURAL_NETWORK.getTitle(), this);
    JButton openSupportVectorMachine = buildJButton(SVM.getTitle(), this);
    JButton openConfiguration = buildJButton(CONFIGURATION.getTitle(), this);
    JPanel buttonPanel = buildButtonPanel(openTimeSeriesButton, openNeuralNetwork, openSupportVectorMachine, openConfiguration);

    algorithmPanel = new JPanel();
    algorithmPanel.setVisible(false);

    this.add(buttonPanel, WEST);
    this.add(algorithmPanel, CENTER);

    this.setVisible(true);
  }

  @VisibleForTesting
  void setAlgorithmPanel(JPanel algorithmPanel) {
    this.algorithmPanel = algorithmPanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    algorithmPanel.removeAll();
    algorithmPanel.add(buildPanel(fromString(e.getActionCommand())));
    algorithmPanel.setVisible(true);

    this.setSize(900, 600);

    SwingUtilities.updateComponentTreeUI(this);
  }

  private static JPanel buildButtonPanel(JButton... buttons) {
    JPanel buttonPanel = new JPanel();

    buttonPanel.setLayout(new GridBagLayout());

    for (int i = 0; i < buttons.length; ++i) {
      buttonPanel.add(buttons[i], buildGridBagConstraints(0, i));
    }

    buttonPanel.setVisible(true);
    return buttonPanel;
  }
}
