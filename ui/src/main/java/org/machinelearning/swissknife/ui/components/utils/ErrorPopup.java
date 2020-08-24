package org.machinelearning.swissknife.ui.components.utils;

import javax.swing.*;
import java.util.concurrent.Callable;

public class ErrorPopup {

    public static void showErrorPopup(String errorMessage, String popupTitle) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error: " + popupTitle, JOptionPane.ERROR_MESSAGE);
    }

    public static <Return> Return tryPopup(Callable<Return> callable, String errorMessage) {
        try {
            return callable.call();
        } catch (Exception e) {
            showErrorPopup(errorMessage, e.getClass().getCanonicalName());
            throw new RuntimeException(e.getCause());
        }
    }
}
