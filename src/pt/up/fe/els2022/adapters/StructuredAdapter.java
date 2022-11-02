package pt.up.fe.els2022.adapters;

import java.util.List;

public abstract class StructuredAdapter extends Adapter {
    protected String path;
    protected List<String> columns;

    public StructuredAdapter(String path, List<String> columns) {
        this.path = path;
        this.columns = columns;
    }
}
