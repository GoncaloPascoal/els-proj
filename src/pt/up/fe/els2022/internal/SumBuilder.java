package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.SumInstruction;

public class SumBuilder extends FunctionBuilder<SumBuilder> {
    public SumBuilder(ProgramBuilder parent) {
        super(parent);
    }

    @Override
    protected Instruction createUnsafe() {
        return new SumInstruction(target, columns);
    }
}
