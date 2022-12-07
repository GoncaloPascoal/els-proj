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
    private final String source;
    private final Set<String> columns, excludeColumns;
    private final String target;

    protected FunctionInstruction(String source, Set<String> columns, Set<String> excludeColumns, String target) {
        this.source = source;
        this.columns = columns;

        if (excludeColumns == null) excludeColumns = Collections.emptySet();
        this.excludeColumns = excludeColumns;

        if (target == null) target = source;
        this.target = target;
    }

    protected abstract String applyToColumn(List<String> column);

    @Override
    public void execute(ProgramState state) {
        Table sourceTable = state.getTable(source);
        Map<String, String> row = new ListOrderedMap<>();

        final Set<String> includeColumns = new HashSet<>(columns == null ? sourceTable.getColumnNames() : columns);
        includeColumns.removeAll(excludeColumns);

        for (String colName : sourceTable.getColumnNames()) {
            if (includeColumns.contains(colName)) {
                row.put(colName, applyToColumn(sourceTable.getColumn(colName)));
            }
        }

        Table targetTable = state.getOrCreateTable(target);
        targetTable.addRow(row);
    }
}
