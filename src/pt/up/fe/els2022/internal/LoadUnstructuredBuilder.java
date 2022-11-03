package pt.up.fe.els2022.internal;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadUnstructuredInstruction;
import pt.up.fe.els2022.instructions.text.TextInstruction;

public class LoadUnstructuredBuilder extends LoadBuilder<LoadUnstructuredBuilder> {
    private final List<TextInstruction> textInstructions;

    public LoadUnstructuredBuilder() {
        textInstructions = new ArrayList<>();
    }

    @Override
    protected Instruction createUnsafe() {
        return new LoadUnstructuredInstruction(target, filePaths, metadataColumns, textInstructions);
    }
}
