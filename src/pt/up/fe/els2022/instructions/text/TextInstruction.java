package pt.up.fe.els2022.instructions.text;

import java.io.File;

import pt.up.fe.els2022.model.Table;

public interface TextInstruction {
    void execute(File file, Table table);
}
