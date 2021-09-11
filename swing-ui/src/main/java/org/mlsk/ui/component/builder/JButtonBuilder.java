package org.mlsk.ui.component.builder;

import javax.swing.*;
import java.awt.event.ActionListener;

public final class JButtonBuilder {

  private JButtonBuilder() {
  }

  public static JButton buildJButton(String title) {
    JButton button = new JButton(title);
    button.setActionCommand(title);
    return button;
  }

  public static JButton buildJButton(String title, ActionListener actionListener) {
    JButton button = buildJButton(title);
    button.addActionListener(actionListener);
    return button;
  }
}
