package cliclient.util;

import org.springframework.stereotype.Component;

// todo: test
@Component
public class NumberUtil {

    public boolean isPositiveLong(String check) {
        try {
            long result = Long.parseLong(check);
            return result > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isNotPositiveLong(String check) {
        return !isPositiveLong(check);
    }

    public boolean isPositiveInteger(String check) {
        try {
            int result = Integer.parseInt(check);
            return result > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isNotPositiveInteger(String check) {
        return !isPositiveInteger(check);
    }

}
