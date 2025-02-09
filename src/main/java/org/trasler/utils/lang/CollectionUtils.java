package org.trasler.utils.lang;

import java.util.List;
import java.util.Map;

/**
 *
 * @author simon
 */
public class CollectionUtils {
    /**
     * Detect if a list is non-null or empty.
     *
     * @param list The list to check
     * @return True if the list is non-null or empty
     */
    public static boolean isEmpty(List list) {
        return (list != null) ? list.isEmpty() : true;
    }

    /**
     * Detect if a map is non-null or empty.
     *
     * @param map The list to check
     * @return True if the map is non-null or empty
     */
    public static boolean isEmpty(Map map) {
        return (map != null) ? map.isEmpty() : true;
    }
}
