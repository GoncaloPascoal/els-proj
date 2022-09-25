package pt.up.fe.els2022.utils;

import java.util.Map;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <K, V> Map<K, V> castMap(Map<?, ?> map, Class<K> keyClass, Class<V> valueClass) {
        if (map.keySet().stream().anyMatch(k -> !keyClass.isInstance(k)) ||
                map.values().stream().anyMatch(v -> !valueClass.isInstance(v))) {
            throw new RuntimeException("Cast failed: map contains elements that aren't instances of the specified types.");
        }

        return castMapUnchecked(map, keyClass, valueClass);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castMapUnchecked(Map<?, ?> map, Class<K> keyClass, Class<V> valueClass) {
        return (Map<K, V>) map;
    }
}
