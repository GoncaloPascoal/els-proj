package pt.up.fe.els2022.internal;

import java.util.List;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadUnstructuredInstruction;
import pt.up.fe.els2022.instructions.text.TextInstruction;

public class LoadUnstructuredBuilder extends LoadBuilder<LoadUnstructuredBuilder> {
    private List<TextInstruction> textInstructions;

    public LoadUnstructuredBuilder withTextInstructions(List<TextInstruction> textInstructions) {
        this.textInstructions = textInstructions;
        return this;
    }

    @Override
    protected void validate() {
        super.validate();
        if (textInstructions == null) {
            throw new RuntimeException("Missing arguments for loadUnstructured instruction.");
        }
    }

    @Override
    protected Instruction createUnsafe() {
        return new LoadUnstructuredInstruction(target, filePaths, metadataColumns, textInstructions);
    }
}
