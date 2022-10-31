package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadUnstructuredInstruction;

public class LoadUnstructuredBuilder extends LoadBuilder {
    @Override
    public Instruction create() {
        return new LoadUnstructuredInstruction(target, filePaths, metadataColumns);
    }
}
