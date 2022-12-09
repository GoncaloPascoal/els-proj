package pt.up.fe.els2022.instructions;

import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.els2022.model.JoinType;
import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;

public class JoinInstruction implements Instruction {
    private final List<String> tables;
    private final JoinType type;
    private final String target;

    public JoinInstruction(List<String> tables, JoinType type, String target) {
        if (tables.size() < 2) {
            throw new IllegalArgumentException("Must specify at least two tables to merge.");
        }

        this.tables = tables;
        this.type = type;

        if (target == null) target = tables.get(0);
        this.target = target;
    }

    @Override
    public void execute(ProgramState state) {
        Table result = new Table(state.getTable(tables.get(0)));

        for (Table table : tables.stream().skip(1).map(state::getTable).collect(Collectors.toList())) {
            type.apply(result, table);
        }

        state.putTable(target, result);
    }
}
