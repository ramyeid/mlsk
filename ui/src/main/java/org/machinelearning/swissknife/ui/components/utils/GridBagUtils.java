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
}
