package bif3.tolan.swe1.mcg.utils;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class with helper methods for maps
 *
 * @author Christopher Tolan
 */
public class MapUtils {
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
        Object[] keys = map.keySet().toArray();
        K randomKey = (K) keys[random.nextInt(keys.length)];
        return map.get(randomKey);
    }
}
