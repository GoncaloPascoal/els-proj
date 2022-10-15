package pt.up.fe.els2022.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <K, V> Map<K, V> castMap(Map<?, ?> map, Class<K> keyClass, Class<V> valueClass) {
        if (map.keySet().stream().anyMatch(k -> !keyClass.isInstance(k)) ||
                map.values().stream().anyMatch(v -> !valueClass.isInstance(v))) {
            throw new RuntimeException("Cast failed: map contains elements that aren't instances of the specified types.");
        }

        return castMapUnchecked(map);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castMapUnchecked(Map<?, ?> map) {
        return (Map<K, V>) map;
    }

    public static <K, V> Map<K, V> buildMap(List<K> keys, List<V> values) {
        Map<K, V> map = new ListOrderedMap<>();
        for (int i = 0; i < keys.size(); ++i) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }
}
