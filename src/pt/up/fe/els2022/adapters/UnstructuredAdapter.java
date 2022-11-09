package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.List;

import pt.up.fe.els2022.instructions.text.TextInstruction;
import pt.up.fe.els2022.model.Table;

public class UnstructuredAdapter extends Adapter {
    private final List<TextInstruction> textInstructions;

    public UnstructuredAdapter(List<TextInstruction> textInstructions) {
        this.textInstructions = textInstructions;
    }

    @Override
    public Table extractTable(List<File> files) {
        Table table = new Table();
        files.forEach(f -> textInstructions.forEach(i -> i.execute(f, table)));
        return table;
    }
}
