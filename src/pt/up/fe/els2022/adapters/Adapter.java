package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.List;
import java.util.Optional;

import pt.up.fe.els2022.model.Table;

public abstract class Adapter {
    protected File file;

    protected Adapter(File file) {
        this.file = file;
    }

    public abstract Table extractTable(String path, Optional<List<String>> columns);
}
