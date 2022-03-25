package cliclient.util;

import java.util.concurrent.TimeUnit;

@Deprecated
// todo: a separate service
public interface ThreadUtil {

    static void sleepSeconds(int s) {
        try {
            TimeUnit.SECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
