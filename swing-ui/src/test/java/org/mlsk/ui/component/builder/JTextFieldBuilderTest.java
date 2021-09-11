package org.mlsk.ui.component.builder;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.ui.component.builder.JTextFieldBuilder.buildJTextField;

public class JTextFieldBuilderTest {

  @Test
  public void should_build_text_field_with_text() {

    JTextField actual = buildJTextField(123);

    assertOnJTextField(actual, 123);
  }

  private static void assertOnJTextField(JTextField actual, int columns) {
    assertEquals(actual.getColumns(), columns);
  }

}