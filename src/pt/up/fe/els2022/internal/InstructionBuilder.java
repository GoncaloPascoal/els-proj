package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;

public abstract class InstructionBuilder {
    protected abstract void validate();

    protected abstract Instruction createUnsafe();

    public Instruction create() {
        validate();
        return createUnsafe();
    }
}
