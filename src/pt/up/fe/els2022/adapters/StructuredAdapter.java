package pt.up.fe.els2022.adapters;

import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;

public abstract class StructuredAdapter extends Adapter {
    protected String key;
    protected List<String> columns;

    public StructuredAdapter(Map<String, MetadataType> metadataColumns, String key, List<String> columns) {
        super(metadataColumns);
        this.key = key;
        this.columns = columns;
    }

    @Override
    public boolean acceptsConfiguration() {
        return super.acceptsConfiguration() && this.key != null && this.columns != null;
    }
}
