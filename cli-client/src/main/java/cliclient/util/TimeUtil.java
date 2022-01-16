package cliclient.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class TimeUtil {

    public Timestamp timestampNow() {
        return new Timestamp(System.currentTimeMillis());
    }

}
