package bif3.tolan.swe1.mcg.utils;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class with helper methods for maps
 *
 * @author Christopher Tolan
 */
public class MapUtils {

    /**
     * Checks if all keys are contained in a map
     *
     * @param map  The map to check
     * @param keys The keys that should be in the map
     * @param <K>  Key Object
     * @param <V>  Value Object
     * @return True if all keys are found in the map
     */
    public static <K, V> boolean stackContainsAllKeys(Map<K, V> map, List<K> keys) {
        for (K key : keys) {
            if (map.containsKey(key) == false)
                return false;
        }
        return true;
    }

    /**
     * Gets a random value from a map
     * Using a thread safe random
     *
     * @param map Map from which the value should be extracted
     * @param <K> Key object type
     * @param <V> Value object type
     * @return Random value
     */
    public static <K, V> V getRandomValue(Map<K, V> map) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector<K> keys = new Vector<>(map.keySet());
        K randomKey = keys.get(random.nextInt(keys.size()));
        return map.get(randomKey);
    }
}
