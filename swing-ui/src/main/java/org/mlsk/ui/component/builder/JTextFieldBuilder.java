package org.mlsk.ui.component.builder;

import javax.swing.*;

public final class JTextFieldBuilder {

  private JTextFieldBuilder() {
  }

  public static JTextField buildJTextField(int columns) {
    return new JTextField(columns);
  }
}
