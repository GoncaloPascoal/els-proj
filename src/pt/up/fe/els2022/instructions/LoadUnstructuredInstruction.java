package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.TxtAdapter;
import pt.up.fe.els2022.instructions.text.TextInstruction;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadUnstructuredInstruction extends LoadInstruction {
    private final List<TextInstruction> textInstructions;

    public LoadUnstructuredInstruction(String target, List<String> filePaths,
            Map<String, MetadataType> metadataColumns, List<TextInstruction> textInstructions) {
        super(target, filePaths, metadataColumns);
        this.textInstructions = textInstructions;
    }

    protected Adapter createAdapter() throws FileNotFoundException, UnsupportedFileExtensionException {
        return new TxtAdapter(textInstructions);
    }
}
