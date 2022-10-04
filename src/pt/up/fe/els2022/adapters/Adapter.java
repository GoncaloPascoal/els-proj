package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public abstract class Adapter {
    protected File file;

    protected Adapter(File file) {
        this.file = file;
    }

    public Table extractTable(String key, List<String> columns, Map<String, MetadataType> metadataColumns) {
        Table table = new Table();
        Map<String, String> row = new LinkedHashMap<>();
        metadataColumns.forEach((k, v) -> row.put(k, v.value(file)));
        table.addRow(row);
        return table;
    }
}
