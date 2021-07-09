package org.machinelearning.swissknife.ui.components.utils;

import java.awt.*;

public class GridBagUtils {

  private GridBagUtils() {
  }


  public static GridBagConstraints buildGridBagConstraints(int x, int y) {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = x;
    gridBagConstraints.gridy = y;
    return gridBagConstraints;
  }


  public static GridBagConstraints buildGridBagConstraints(int x, int y, int gridWidth) {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = x;
    gridBagConstraints.gridy = y;
    gridBagConstraints.gridwidth = gridWidth;
    return gridBagConstraints;
  }
}
