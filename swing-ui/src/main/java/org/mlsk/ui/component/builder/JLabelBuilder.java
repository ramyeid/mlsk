package org.mlsk.ui.component.builder;

import javax.swing.*;

public final class JLabelBuilder {

  private JLabelBuilder() {
  }

  public static JLabel buildJLabel(String text) {
    return new JLabel(text);
  }
}
