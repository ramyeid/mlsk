package org.mlsk.ui.component.builder;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.ui.component.builder.GridBagConstraintsBuilder.buildGridBagConstraints;

public class GridBagConstraintsBuilderTest {

  @Test
  public void should_build_grid_bag_constraints_with_and_y() {

    GridBagConstraints actual = buildGridBagConstraints(10, 200);

    assertOnGridBagConstraints(actual, 10, 200, 1);
  }

  @Test
  public void should_build_grid_bag_constraints_with_x_y_and_width() {

    GridBagConstraints actual = buildGridBagConstraints(1210, 3200, 1240);

    assertOnGridBagConstraints(actual, 1210, 3200, 1240);
  }

  private static void assertOnGridBagConstraints(GridBagConstraints actual, int gridX, int gridY, int gridWidth) {
    assertEquals(gridX, actual.gridx);
    assertEquals(gridY, actual.gridy);
    assertEquals(gridWidth, actual.gridwidth);
  }
}