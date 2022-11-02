package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadStructuredInstruction;

import java.util.List;

public class LoadStructuredBuilder extends LoadBuilder<LoadStructuredBuilder> {
    private String path;
    private List<String> columns;

    public LoadStructuredBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public LoadStructuredBuilder withColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    @Override
    protected void validate() {
        super.validate();
        if (path == null) {
            throw new RuntimeException("Missing arguments for loadStructured instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new LoadStructuredInstruction(target, filePaths, metadataColumns, path, columns);
    }
}
