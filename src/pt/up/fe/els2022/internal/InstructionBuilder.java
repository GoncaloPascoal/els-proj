package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;

public abstract class InstructionBuilder extends Builder<Instruction> {
    private final ProgramBuilder parent;

    public InstructionBuilder(ProgramBuilder parent) {
        this.parent = parent;
    }

    public ProgramBuilder close() {
        return parent;
    }
}
