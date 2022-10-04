package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import pt.up.fe.els2022.model.Table;

public abstract class Adapter {
    protected AdapterConfiguration configuration;
    protected File file;

    protected Adapter(AdapterConfiguration configuration, File file) {
        this.configuration = configuration;
        this.file = file;
    }

    public boolean acceptsConfiguration() {
        return configuration != null && configuration.getMetadataColumns() != null;
    }

    public Table extractTable() {
        Table table = new Table();
        Map<String, String> row = new LinkedHashMap<>();
        configuration.getMetadataColumns().forEach((k, v) -> row.put(k, v.value(file)));
        table.addRow(row);
        return table;
    }
}
