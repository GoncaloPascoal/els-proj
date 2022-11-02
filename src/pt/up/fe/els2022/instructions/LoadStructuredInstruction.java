package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.JsonAdapter;
import pt.up.fe.els2022.adapters.XmlAdapter;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadStructuredInstruction extends LoadInstruction {
    private final String path;
    private final List<String> columns;

    public LoadStructuredInstruction(String target, List<String> filePaths, Map<String, MetadataType> metadataColumns,
            String path, List<String> columns) {
        super(target, filePaths, metadataColumns);
        this.path = path;
        this.columns = columns;
    }

    protected Adapter createAdapter() throws FileNotFoundException, UnsupportedFileExtensionException {
        String extension = FileUtils.getExtension(files.stream().findFirst().get());
 
        if (files.stream().anyMatch(f -> !FileUtils.getExtension(f).equals(extension))) {
            throw new IllegalArgumentException("Source files must have the same extension.");
        }

        if (files.stream().anyMatch(f -> !(f.exists() && f.canRead()))) {
            throw new FileNotFoundException();
        }

        switch (extension) {
            case "xml":
                return new XmlAdapter(path, columns);
            case "json":
                return new JsonAdapter(path, columns);
            default:
                throw new UnsupportedFileExtensionException(extension);
        }
    }
}
