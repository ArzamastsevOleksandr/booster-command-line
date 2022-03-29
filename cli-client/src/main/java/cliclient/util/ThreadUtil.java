package cliclient.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ThreadUtil {

    public void sleepSeconds(int s) {
        try {
            TimeUnit.SECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
