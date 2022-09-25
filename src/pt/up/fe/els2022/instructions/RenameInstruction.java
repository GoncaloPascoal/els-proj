package pt.up.fe.els2022.instructions;

import java.util.Map;

import pt.up.fe.els2022.model.Table;

public class RenameInstruction implements Instruction {
    private final Table table;
    private final Map<String, String> mapping;

    public RenameInstruction(Table table, Map<String, String> mapping) {
        this.table = table;
        this.mapping = mapping;
    }

    public void execute() {
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            table.renameColumn(entry.getKey(), entry.getValue());
        }
    }
}
