package pt.up.fe.els2022.model;

import java.util.HashMap;
import java.util.Map;

public class ProgramState {
    private final Map<String, Table> tables;

    public ProgramState() {
        tables = new HashMap<>();
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public Table getOrCreateTable(String name) {
        tables.putIfAbsent(name, new Table());
        return tables.get(name);
    }

    public void putTable(String name, Table table) {
        tables.put(name, table);
    }
}
