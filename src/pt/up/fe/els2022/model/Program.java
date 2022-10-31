package pt.up.fe.els2022.model;

import pt.up.fe.els2022.instructions.Instruction;

import java.util.List;

public class Program {
    private final ProgramState state;
    private final List<Instruction> instructions;

    public Program(List<Instruction> instructions) {
        state = new ProgramState();
        this.instructions = instructions;
    }

    public void execute() {
        instructions.forEach(i -> i.execute(state));
    }
}
