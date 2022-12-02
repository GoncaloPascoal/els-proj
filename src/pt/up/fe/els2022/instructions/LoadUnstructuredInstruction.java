package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.UnstructuredAdapter;
import pt.up.fe.els2022.instructions.text.TextInstruction;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadUnstructuredInstruction extends LoadInstruction {
    private final List<TextInstruction> textInstructions;

    public LoadUnstructuredInstruction(String target, List<String> filePaths,
            Map<String, MetadataType> metadataColumns, String columnSuffix,
            List<TextInstruction> textInstructions) {
        super(target, filePaths, metadataColumns, columnSuffix);

        if (textInstructions.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one text instruction.");
        }

        this.textInstructions = textInstructions;
    }

    protected Adapter createAdapter() throws FileNotFoundException, UnsupportedFileExtensionException {
        return new UnstructuredAdapter(textInstructions);
    }
}
