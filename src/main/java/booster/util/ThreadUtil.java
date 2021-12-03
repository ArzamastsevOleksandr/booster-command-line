package booster.util;

import java.util.concurrent.TimeUnit;

public interface ThreadUtil {

    static void sleepSeconds(int s) {
        try {
            TimeUnit.SECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
