package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.AverageInstruction;
import pt.up.fe.els2022.instructions.Instruction;

public class AverageBuilder extends FunctionBuilder<AverageBuilder> {
    public AverageBuilder(ProgramBuilder parent) {
        super(parent);
    }

    @Override
    protected Instruction createUnsafe() {
        return new AverageInstruction(target, columns);
    }
}
