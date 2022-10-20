package pt.up.fe.els2022.instructions;

import java.util.Map;

import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;

public class RenameInstruction implements Instruction {
    private final String source;
    private final Map<String, String> mapping;

    public RenameInstruction(String source, Map<String, String> mapping) {
        this.source = source;
        this.mapping = mapping;
    }

    public void execute(ProgramState state) {
        Table table = state.getTable(source);
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            table.renameColumn(entry.getKey(), entry.getValue());
        }
    }
}
