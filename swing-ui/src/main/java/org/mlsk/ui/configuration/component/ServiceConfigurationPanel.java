package org.mlsk.ui.configuration.component;

import com.google.common.annotations.VisibleForTesting;

import javax.swing.*;
import java.awt.*;

import static org.mlsk.ui.component.builder.GridBagConstraintsBuilder.buildGridBagConstraints;
import static org.mlsk.ui.component.builder.JLabelBuilder.buildJLabel;
import static org.mlsk.ui.component.builder.JTextFieldBuilder.buildJTextField;

public class ServiceConfigurationPanel extends JPanel {

  private final JTextField serviceHostValue;
  private final JTextField servicePortValue;

  public ServiceConfigurationPanel() {
    this(buildJTextField(10), buildJTextField(10));

    this.setLayout(new GridBagLayout());

    this.add(buildJLabel("Service Host"), buildGridBagConstraints(0, 0));
    this.add(serviceHostValue, buildGridBagConstraints(1, 0));
    this.add(buildJLabel("Service Port"), buildGridBagConstraints(0, 1));
    this.add(servicePortValue, buildGridBagConstraints(1, 1));
  }

  @VisibleForTesting
  ServiceConfigurationPanel(JTextField serviceHostValue, JTextField servicePortValue) {
    this.serviceHostValue = serviceHostValue;
    this.servicePortValue = servicePortValue;
  }

  public String getServiceHost() {
    return serviceHostValue.getText();
  }

  public void setServiceHost(String serviceHost) {
    serviceHostValue.setText(serviceHost);
  }

  public String getServicePort() {
    return servicePortValue.getText();
  }

  public void setServicePort(String servicePort) {
    servicePortValue.setText(servicePort);
  }
}
