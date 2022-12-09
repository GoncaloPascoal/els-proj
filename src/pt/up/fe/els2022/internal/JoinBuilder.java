package pt.up.fe.els2022.internal;

import java.util.Arrays;
import java.util.List;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.JoinInstruction;
import pt.up.fe.els2022.model.JoinType;

public class JoinBuilder extends InstructionBuilder {
    private List<String> tables;
    private JoinType type;
    private String target;

    public JoinBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public JoinBuilder withTables(List<String> tables) {
        this.tables = tables;
        return this;
    }

    public JoinBuilder withTables(String... tables) {
        this.tables = Arrays.asList(tables);
        return this;
    }

    public JoinBuilder withType(JoinType type) {
        this.type = type;
        return this;
    }

    public JoinBuilder withTarget(String target) {
        this.target = target;
        return this;
    }

    @Override
    protected void validate() {
        if (tables == null || type == null) {
            throw new RuntimeException("Missing arguments for join instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new JoinInstruction(tables, type, target);
    }
    
}
