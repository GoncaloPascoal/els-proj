package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadStructuredInstruction;

import java.util.Arrays;
import java.util.List;

public class LoadStructuredBuilder extends LoadBuilder<LoadStructuredBuilder> {
    private List<String> paths;
    private List<String> columns;

    public LoadStructuredBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public LoadStructuredBuilder withPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public LoadStructuredBuilder withPaths(String... paths) {
        this.paths = Arrays.asList(paths);
        return this;
    }

    public LoadStructuredBuilder withColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public LoadStructuredBuilder withColumns(String... columns) {
        this.columns = Arrays.asList(columns);
        return this;
    }

    @Override
    protected void validate() {
        super.validate();
        if (paths == null) {
            throw new RuntimeException("Missing arguments for loadStructured instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new LoadStructuredInstruction(target, filePaths, metadataColumns,
            columnSuffix, paths, columns);
    }
}
