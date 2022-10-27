package pt.up.fe.els2022.adapters;

import java.util.List;

public abstract class StructuredAdapter extends Adapter {
    protected String key;
    protected List<String> columns;

    public StructuredAdapter(String key, List<String> columns) {
        this.key = key;
        this.columns = columns;
    }
}
