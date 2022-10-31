package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadUnstructuredInstruction;

public class LoadUnstructuredBuilder extends LoadBuilder {
    @Override
    protected Instruction createUnsafe() {
        return new LoadUnstructuredInstruction(target, filePaths, metadataColumns);
    }
}
