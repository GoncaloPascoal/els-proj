package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadStructuredInstruction;

import java.util.List;

public class LoadStructuredBuilder extends LoadBuilder<LoadStructuredBuilder> {
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
    protected void validate() {
        super.validate();
        if (key == null) {
            throw new RuntimeException("Missing arguments for loadStructured instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new LoadStructuredInstruction(target, filePaths, metadataColumns, key, columns);
    }
}
