package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.SortInstruction;

public class SortBuilder extends InstructionBuilder {
    private String source;
    private String column;
    private Boolean descending;

    public SortBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public SortBuilder withSource(String source) {
        this.source = source;
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
        if (source == null || column == null) {
            throw new RuntimeException("Missing arguments for sort instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new SortInstruction(source, column, descending);
    }
}
