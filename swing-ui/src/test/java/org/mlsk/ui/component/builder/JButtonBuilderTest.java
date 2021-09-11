package org.mlsk.ui.component.builder;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.ui.component.builder.JButtonBuilder.buildJButton;

public class JButtonBuilderTest {

  @Test
  public void should_build_button_with_title_and_action_command() {

    JButton actual = buildJButton("myTitle");

    assertOnJButton(actual, "myTitle");
  }

  @Test
  public void should_build_button_with_title_action_command_and_listener() {
    ActionListener actionListener = e -> {
      // doNothing
    };

    JButton actual = buildJButton("myTitleWithListener", actionListener);

    assertOnJButton(actual, "myTitleWithListener", actionListener);
  }

  private static void assertOnJButton(JButton actual, String title, Object... actionListener) {
    assertEquals(title, actual.getText());
    assertEquals(title, actual.getActionCommand());
    assertEquals(newArrayList(actionListener), newArrayList(actual.getActionListeners()));
  }
}