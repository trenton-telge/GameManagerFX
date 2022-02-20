package com.trentontelge.gamemanagerfx.util.exceptions;

import javax.swing.*;

public class ErrorHandler {
    public static void handleException(Exception e, boolean isFatal) {
        e.printStackTrace();
        showErrorDialog(e, isFatal, !isFatal);
    }
    private static void showErrorDialog(Exception e, boolean isFatal, boolean isReportable) {
        JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error", isFatal?JOptionPane.ERROR_MESSAGE:JOptionPane.WARNING_MESSAGE);
    }
}
