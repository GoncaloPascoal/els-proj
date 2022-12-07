package pt.up.fe.els2022.instructions;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;

public abstract class FunctionInstruction implements Instruction {
    private final String target;
    private final Set<String> columns, excludeColumns;

    protected FunctionInstruction(String target, Set<String> columns, Set<String> excludeColumns) {
        this.target = target;
        this.columns = columns;

        if (excludeColumns == null) {
            excludeColumns = Collections.emptySet();
        }
        this.excludeColumns = excludeColumns;
    }

    protected abstract String applyToColumn(List<String> column);

    @Override
    public void execute(ProgramState state) {
        Table table = state.getTable(target);
        Map<String, String> row = new ListOrderedMap<>();

        final Set<String> includeColumns = new HashSet<>(columns == null ? table.getColumnNames() : columns);
        includeColumns.removeAll(excludeColumns);

        for (String colName : table.getColumnNames()) {
            if (includeColumns.contains(colName)) {
                row.put(colName, applyToColumn(table.getColumn(colName)));
            }
        }

        table.addRow(row);
    }
}
