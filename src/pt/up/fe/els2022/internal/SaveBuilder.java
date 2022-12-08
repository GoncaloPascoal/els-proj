package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.SaveInstruction;

import java.util.List;

public class SaveBuilder extends InstructionBuilder {
    private String source;
    private String file;
    private List<String> columns;

    public SaveBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public SaveBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public SaveBuilder withFile(String file) {
        this.file = file;
        return this;
    }

    public SaveBuilder withColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    @Override
    protected void validate() {
        if (source == null || file == null) {
            throw new RuntimeException("Missing arguments for save instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new SaveInstruction(source, file, columns);
    }
}
