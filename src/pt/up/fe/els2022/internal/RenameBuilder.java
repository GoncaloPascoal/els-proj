package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.RenameInstruction;

import java.util.Map;

public class RenameBuilder extends InstructionBuilder {
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
    public Instruction create() {
        return new RenameInstruction(source, mapping);
    }
}
