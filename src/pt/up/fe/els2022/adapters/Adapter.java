package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public abstract class Adapter {
    public static Table extractMetadataTable(Map<String, MetadataType> metadataColumns, File file) {
        return extractMetadataTable(metadataColumns, Collections.singletonList(file));
    }

    public static Table extractMetadataTable(Map<String, MetadataType> metadataColumns, List<File> files) {
        Table table = new Table();
        Map<String, List<String>> rows = new LinkedHashMap<>();
        files.forEach(f -> metadataColumns.forEach((k, v) -> {
            rows.putIfAbsent(k, new ArrayList<>());
            rows.get(k).add(v.value(f));
        }));
        table.addRows(rows);
        return table;
    }

    public abstract Table extractTable(List<File> files);

    public Table extractTable(File file) {
        return extractTable(Collections.singletonList(file));
    }
}
