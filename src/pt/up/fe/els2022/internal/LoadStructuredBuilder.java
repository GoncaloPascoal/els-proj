package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadStructuredInstruction;

import java.util.List;

public class LoadStructuredBuilder extends LoadBuilder {
    private String key;
    private List<String> columns;

    public LoadStructuredBuilder withKey(String key) {
        this.key = key;
        return this;
    }

    public LoadStructuredBuilder withColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    @Override
    public Instruction create() {
        return new LoadStructuredInstruction(target, filePaths, metadataColumns, key, columns);
    }
}
