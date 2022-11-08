package pt.up.fe.els2022.adapters;

import java.util.List;

public abstract class StructuredAdapter extends Adapter {
    protected List<String> paths;
    protected List<String> columns;

    public StructuredAdapter(List<String> paths, List<String> columns) {
        this.paths = paths;
        this.columns = columns;
    }
}
