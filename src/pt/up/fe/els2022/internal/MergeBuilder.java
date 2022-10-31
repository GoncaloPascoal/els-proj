package pt.up.fe.els2022.internal;

import java.util.List;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.MergeInstruction;

public class MergeBuilder extends InstructionBuilder {
    private List<String> tables;
    private String target;

    public MergeBuilder withTables(List<String> tables) {
        this.tables = tables;
        return this;
    }

    public MergeBuilder withTarget(String target) {
        this.target = target;
        return this;
    }

    @Override
    protected void validate() {
        if (tables == null) {
            throw new RuntimeException("Missing arguments for merge instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new MergeInstruction(tables, target);
    }
}
