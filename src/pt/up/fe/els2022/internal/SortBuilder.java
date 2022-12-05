package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.SortInstruction;

public class SortBuilder extends InstructionBuilder {
    private String target;
    private String column;
    private Boolean descending;

    public SortBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public SortBuilder withTarget(String target) {
        this.target = target;
        return this;
    }

    public SortBuilder withColumn(String column) {
        this.column = column;
        return this;
    }

    public SortBuilder withDescending(Boolean descending) {
        this.descending = descending;
        return this;
    }

    @Override
    protected void validate() {
        if (target == null || column == null) {
            throw new RuntimeException("Missing arguments for sort instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new SortInstruction(target, column, descending);
    }
}
