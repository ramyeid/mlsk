package org.mlsk.ui.components.startup;

import org.mlsk.ui.components.timeseries.TimeSeriesPanel;
import org.mlsk.ui.components.utils.GridBagUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.mlsk.service.utils.TimeSeriesAnalysisAlgorithmNames.TIME_SERIES_ANALYSIS;
import static org.mlsk.ui.components.utils.ComponentBuilder.newJButton;

public class MainFrame extends JFrame implements ActionListener {

  JPanel algorithmPanel = new JPanel();

  public MainFrame() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(200, 600);
    this.setLayout(new BorderLayout());

    JButton openTimeSeriesButton = newJButton(TIME_SERIES_ANALYSIS, this);
    JButton openNeuralNetwork = newJButton("Neural Network", this);
    JButton openSupportVectorMachine = newJButton("Support Vector Machine", this);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridBagLayout());
    buttonPanel.add(openTimeSeriesButton, GridBagUtils.buildGridBagConstraints(0, 0));
    buttonPanel.add(openNeuralNetwork, GridBagUtils.buildGridBagConstraints(0, 1));
    buttonPanel.add(openSupportVectorMachine, GridBagUtils.buildGridBagConstraints(0, 2));

    buttonPanel.setVisible(true);
    algorithmPanel.setVisible(false);

    this.add(buttonPanel, BorderLayout.WEST);
    this.add(algorithmPanel, BorderLayout.CENTER);

    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    algorithmPanel.removeAll();
    switch (e.getActionCommand()) {
      case TIME_SERIES_ANALYSIS:
        algorithmPanel.add(new TimeSeriesPanel());
        break;
      default:
        JPanel empty = new JPanel();
        empty.add(new JLabel("NOT SUPPORTED YET"));
        algorithmPanel.add(empty);
        break;
    }
    algorithmPanel.setVisible(true);
    this.setSize(900, 600);
    SwingUtilities.updateComponentTreeUI(this);
  }
}
