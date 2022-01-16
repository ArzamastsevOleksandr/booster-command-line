package cliclient.util;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {

    public boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

}
