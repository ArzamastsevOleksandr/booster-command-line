package com.booster.util;

// todo: test
public class NumberUtil {

    public static boolean isPositiveLong(String check) {
        try {
            long result = Long.parseLong(check);
            return result > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotPositiveLong(String check) {
        return !isPositiveLong(check);
    }

    public static boolean isPositiveInteger(String check) {
        try {
            int result = Integer.parseInt(check);
            return result > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotPositiveInteger(String check) {
        return !isPositiveInteger(check);
    }

}
