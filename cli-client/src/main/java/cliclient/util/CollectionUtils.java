package cliclient.util;

import java.util.ArrayList;
import java.util.List;

public interface CollectionUtils {

    static <T> List<T> sublist(List<T> list, int start) {
        return new ArrayList<>(list.subList(start, list.size()));
    }

}
