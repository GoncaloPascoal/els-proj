package pt.up.fe.els2022.instructions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;

public class SortInstruction implements Instruction {
    private final String target;
    private final String colName;
    private final boolean descending;

    public SortInstruction(String target, String column, Boolean descending) {
        this.target = target;
        this.colName = column;

        if (descending == null) {
            descending = false;
        }
        this.descending = descending;
    }

    public void execute(ProgramState state) {
        Table table = state.getTable(target);
        if (table == null) {
            throw new IllegalArgumentException("Table " + target + " does not exist.");
        }

        List<String> column = table.getColumn(colName);
        if (column == null) {
            throw new IllegalArgumentException("Column " + colName + " does not exist.");
        }

        List<Integer> indices = IntStream.range(0, column.size()).boxed().collect(Collectors.toList());
        indices.sort((a, b) -> {
            int val = column.get(a).compareTo(column.get(b));
            if (descending) val = -val;
            return val;
        });

        for (String colName : table.getColumnNames()) {
            List<String> oldColumn = table.getColumn(colName);
            List<String> newColumn = new ArrayList<>();

            for (int i : indices) {
                newColumn.add(oldColumn.get(i));
            }

            table.replaceColumn(colName, newColumn);
        }
    }
}
