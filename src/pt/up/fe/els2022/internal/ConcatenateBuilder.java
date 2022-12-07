package pt.up.fe.els2022.internal;

import java.util.Arrays;
import java.util.List;

import pt.up.fe.els2022.instructions.ConcatenateInstruction;
import pt.up.fe.els2022.instructions.Instruction;

public class ConcatenateBuilder extends InstructionBuilder {
    private List<String> tables;
    private String target;

    public ConcatenateBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public ConcatenateBuilder withTables(List<String> tables) {
        this.tables = tables;
        return this;
    }

    public ConcatenateBuilder withTables(String... tables) {
        this.tables = Arrays.asList(tables);
        return this;
    }

    public ConcatenateBuilder withTarget(String target) {
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
        return new ConcatenateInstruction(tables, target);
    }
}
