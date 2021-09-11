package org.mlsk.ui.component.panel;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static org.mlsk.ui.component.builder.JLabelBuilder.buildJLabel;

public class EmptyPanel extends JPanel {

  public EmptyPanel() {
    this.setLayout(new BorderLayout());

    JLabel label = buildJLabel("NOT SUPPORTED YET");

    this.add(label, CENTER);
  }
}
