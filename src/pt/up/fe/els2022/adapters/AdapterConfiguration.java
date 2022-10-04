package pt.up.fe.els2022.adapters;

import pt.up.fe.els2022.model.MetadataType;

import java.util.List;
import java.util.Map;

public class AdapterConfiguration {
    private String key;
    private List<String> columns;
    private Map<String, MetadataType> metadataColumns;

    public AdapterConfiguration(String key, List<String> columns,
                                Map<String, MetadataType> metadataColumns) {
        this.key = key;
        this.columns = columns;
        this.metadataColumns = metadataColumns;
    }

    public String getKey() {
        return key;
    }

    public List<String> getColumns() {
        return columns;
    }

    public Map<String, MetadataType> getMetadataColumns() {
        return metadataColumns;
    }
}
