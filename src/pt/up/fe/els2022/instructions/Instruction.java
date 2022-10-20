package pt.up.fe.els2022.instructions;

import pt.up.fe.els2022.model.ProgramState;

public interface Instruction {
    void execute(ProgramState state);
}
