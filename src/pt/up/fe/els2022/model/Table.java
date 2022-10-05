package pt.up.fe.els2022.model;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.set.UnmodifiableSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Table {
    private final ListOrderedMap<String, List<String>> data;

    public Table() {
        data = new ListOrderedMap<>();
    }

    // TODO: Perhaps extract this to a utility class?
    private static int numRows(Map<String, List<String>> data) {
        return data.values().stream().findFirst().map(List::size).orElse(0);
    }

    public void addRow(Map<String, String> row) {
        Map<String, List<String>> rows = new ListOrderedMap<>();
        row.forEach((k, v) -> rows.put(k, Collections.singletonList(v)));
        addRows(rows);
    }

    public void addRows(Map<String, List<String>> rows) {
        Set<String> missingColumns = new LinkedHashSet<>(data.keySet());
        missingColumns.removeAll(rows.keySet());

        int numExisting = numRows();
        for (Map.Entry<String, List<String>> entry : rows.entrySet()) {
            data.putIfAbsent(entry.getKey(), new ArrayList<>(Collections.nCopies(numExisting, null)));
            data.get(entry.getKey()).addAll(entry.getValue());
        }

        for (String column : missingColumns) {
            data.get(column).add(numRows(rows), null);
        }
    }

    public void addColumn(String name, List<String> values) {
        if (getColumnNames().contains(name)) {
            throw new IllegalArgumentException("Column already exists: " + name);
        }

        int difference = values.size() - numRows();
        int absoluteDifference = Math.abs(difference);
        if (difference > 0) {
            for (List<String> column : data.values()) {
                column.add(absoluteDifference, null);
            }
        }
        else if (difference < 0) {
            values.add(absoluteDifference, null);
        }

        data.put(name, values);
    }

    public void addColumns(Map<String, List<String>> columns) {
        columns.forEach(this::addColumn);
    }

    public void renameColumn(String oldName, String newName) {
        if (data.containsKey(newName)) {
            throw new IllegalArgumentException("Column already exists: " + newName);
        }

        int index = data.indexOf(oldName);
        List<String> column = data.remove(oldName);
        if (column != null) data.put(index, newName, column);
    }

    public List<String> getColumn(String name) {
        return Collections.unmodifiableList(data.get(name));
    }

    public Set<String> getColumnNames() {
        return UnmodifiableSet.unmodifiableSet(data.keySet());
    }

    public Map<String, String> getRow(int index) {
        Map<String, String> row = new ListOrderedMap<>();
        data.forEach((k, v) -> row.put(k, v.get(index)));
        return UnmodifiableMap.unmodifiableMap(row);
    }

    public int numColumns() {
        return data.size();
    }

    public int numRows() {
        return numRows(data);
    }

    public void concatenate(Table other) {
        addRows(other.data);
    }

    public void merge(Table other) {
        addColumns(other.data);
    }
}
