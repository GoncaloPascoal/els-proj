package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.JsonAdapter;
import pt.up.fe.els2022.adapters.XmlAdapter;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadStructuredInstruction extends LoadInstruction {
    private final List<String> paths;
    private final List<String> columns;

    public LoadStructuredInstruction(String target, List<String> filePaths, Map<String, MetadataType> metadataColumns,
            List<String> paths, List<String> columns) {
        super(target, filePaths, metadataColumns);

        if (paths.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one path.");
        }

        if (columns == null) {
            columns = Collections.emptyList();
        }

        if (paths.size() >= 2 && !columns.isEmpty()) {
            throw new IllegalArgumentException("Column specification for multiple paths is not supported.");
        }

        this.paths = paths;
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
                return new XmlAdapter(paths, columns);
            case "json":
                return new JsonAdapter(paths, columns);
            default:
                throw new UnsupportedFileExtensionException(extension);
        }
    }
}
