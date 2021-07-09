package org.machinelearning.swissknife.ui.components.utils;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ComponentBuilder {

  private ComponentBuilder() {
  }

  public static JButton newJButton(String title) {
    JButton button = new JButton(title);
    button.setActionCommand(title);
    return button;
  }

  public static JButton newJButton(String title, ActionListener actionListener) {
    JButton button = newJButton(title);
    button.addActionListener(actionListener);
    return button;
  }
}
