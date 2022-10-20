package pt.up.fe.els2022.instructions;

import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;

public class MergeInstruction implements Instruction {
    private final List<String> tables;
    private final String target;

    public MergeInstruction(List<String> tables, String target) {
        if (tables.size() < 2) {
            throw new IllegalArgumentException("Must specify at least two tables to merge.");
        }

        if (target == null) {
            target = tables.get(0);
        }

        this.tables = tables;
        this.target = target;
    }

    @Override
    public void execute(ProgramState state) {
        Table result = new Table(state.getTable(tables.get(0)));

        for (Table table : tables.stream().skip(1).map(state::getTable).collect(Collectors.toList())) {
            result.merge(table);
        }

        state.putTable(target, result);
    }
}
