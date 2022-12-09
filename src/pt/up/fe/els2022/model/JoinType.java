package pt.up.fe.els2022.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum JoinType {
    MERGE("merge", Table::merge),
    CONCATENATE("concatenate", Table::concatenate);

    private final String id;
    private final BiConsumer<Table, Table> consumer;

    JoinType(String id, BiConsumer<Table, Table> consumer) {
        this.id = id;
        this.consumer = consumer;
    }

    private static final Map<String, JoinType> idMap = new HashMap<>();
    static {
        for (var type : JoinType.values()) {
            idMap.put(type.id, type);
        }
    }

    public static JoinType fromId(String id) {
        return idMap.get(id);
    }

    public void apply(Table table, Table other) {
        consumer.accept(table, other);
    }
}
