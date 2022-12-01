package pt.up.fe.els2022.instructions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;

public abstract class FunctionInstruction implements Instruction {
    private final String target;
    private final Set<String> columns;

    protected FunctionInstruction(String target, Set<String> columns) {
        this.target = target;

        if (columns == null) {
            columns = Collections.emptySet();
        }
        this.columns = columns;
    }

    protected abstract String applyToColumn(List<String> column);

    @Override
    public void execute(ProgramState state) {
        Table table = state.getTable(target);
        Map<String, String> row = new ListOrderedMap<>();

        for (String colName : table.getColumnNames()) {
            if (columns.isEmpty() || columns.contains(colName)) {
                row.put(colName, applyToColumn(table.getColumn(colName)));
            }
        }
    }
}
