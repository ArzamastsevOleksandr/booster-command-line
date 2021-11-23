package booster.util;

import java.util.function.Supplier;

public class ObjectUtil {

    public static <T, X extends Throwable> T requireNonNullOrElseThrow(T obj, Supplier<X> supplier) throws X {
        if (obj == null) {
            throw supplier.get();
        }
        return obj;
    }

    public static <T> T requireNonNullOrElseThrowIAE(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
        return obj;
    }

}
