package org.trasler.utils.lang;

import java.util.List;
import java.util.Map;

/**
 *
 * @author simon
 */
public class CollectionUtils {
    public static boolean isEmpty(List list) {
        return (list != null) ? list.isEmpty() : true;
    }

    public static boolean isEmpty(Map map) {
        return (map != null) ? map.isEmpty() : true;
    }
}
