package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.RenameInstruction;

import java.util.Map;

public class RenameBuilder extends InstructionBuilder<Instruction> {
    private String source;
    private Map<String, String> mapping;

    public RenameBuilder withSource(String source) {
        this.source = source;
        return this;
    }

    public RenameBuilder withMapping(Map<String, String> mapping) {
        this.mapping = mapping;
        return this;
    }

    @Override
    protected void validate() {
        if (source == null || mapping == null) {
            throw new RuntimeException("Missing arguments for rename instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new RenameInstruction(source, mapping);
    }
}
