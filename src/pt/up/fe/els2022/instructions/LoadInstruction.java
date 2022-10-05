package pt.up.fe.els2022.instructions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.XmlAdapter;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadInstruction implements Instruction {
    private final Table table;
    private final List<File> files;
    private final String key;
    private final List<String> columns;
    private final Map<String, MetadataType> metadataColumns;

    public LoadInstruction(Table table, List<String> filePaths, String key, List<String> columns,
            Map<String, MetadataType> metadataColumns) {
        this.table = table;
        files = filePaths.stream().map(p -> new File(p)).collect(Collectors.toList());
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one source file.");
        }
        this.key = key;
        this.columns = columns;
        this.metadataColumns = metadataColumns;
    }

    private Adapter createAdapter() throws FileNotFoundException, UnsupportedFileExtensionException {
        String extension = FileUtils.getExtension(files.stream().findFirst().get());
 
        if (files.stream().anyMatch(f -> !FileUtils.getExtension(f).equals(extension))) {
            throw new IllegalArgumentException("Source files must have the same extension.");
        }

        if (files.stream().anyMatch(f -> !(f.exists() && f.canRead()))) {
            throw new FileNotFoundException();
        }

        switch (extension) {
            case "xml":
                return new XmlAdapter(key, columns, metadataColumns);
            default:
                throw new UnsupportedFileExtensionException(extension);
        }
    }

    public void execute() {
        Table newTable;
        Adapter adapter;

        try {
            adapter = createAdapter();
        }
        catch (FileNotFoundException | UnsupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }

        newTable = adapter.extractMetadataTable(files);
        newTable.merge(adapter.extractTable(files));

        table.concatenate(newTable);
    }
}
