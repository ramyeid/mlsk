package org.mlsk.ui.component.builder;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.ui.component.builder.JLabelBuilder.buildJLabel;

public class JLabelBuilderTest {

  @Test
  public void should_build_label_with_text() {

    JLabel actual = buildJLabel("myText");

    assertOnJLabel(actual, "myText");
  }

  private static void assertOnJLabel(JLabel actual, String text) {
    assertEquals(actual.getText(), text);
  }
}