package pt.up.fe.els2022.internal.text;

import pt.up.fe.els2022.instructions.text.TextInstruction;

public abstract class TextInstructionBuilder {
    protected abstract void validate();

    protected abstract TextInstruction createUnsafe();

    public TextInstruction create() {
        validate();
        return createUnsafe();
    }
}
