package greencity.security.filters;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private static final Map<String, String> storage = new HashMap<>();

    /**
     * Put value to map.
     * */
    public static void put(String key, String value) {
        storage.put(key, value);
    }

    /**
     * Get value from map.
     * */
    public static String get(String key) {
        return storage.get(key);
    }
}
