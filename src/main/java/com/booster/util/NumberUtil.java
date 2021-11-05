package com.booster.util;

// todo: test
public class NumberUtil {

    public static boolean isPositiveLong(String check) {
        try {
            long result = Long.parseLong(check);
            if (result < 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotPositiveLong(String check) {
        return !isPositiveLong(check);
    }

}
