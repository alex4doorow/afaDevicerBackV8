package com.afa.devicer.back.utils;

@SuppressWarnings("PMD.TooManyMethods")
public final class NumericHelper {

    private NumericHelper() {
    }

    public static boolean isNumeric(final String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str); // или Long.parseLong(str)
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
