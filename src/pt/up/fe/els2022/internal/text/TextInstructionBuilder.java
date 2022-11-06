package pt.up.fe.els2022.internal.text;

import pt.up.fe.els2022.instructions.text.TextInstruction;
import pt.up.fe.els2022.internal.InstructionBuilder;
import pt.up.fe.els2022.internal.LoadUnstructuredBuilder;

public abstract class TextInstructionBuilder extends InstructionBuilder<TextInstruction> {
    private final LoadUnstructuredBuilder parent;

    public TextInstructionBuilder(LoadUnstructuredBuilder parent) {
        this.parent = parent;
    }

    public LoadUnstructuredBuilder close() {
        return parent;
    }
}
