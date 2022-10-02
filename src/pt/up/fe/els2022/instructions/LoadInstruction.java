package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadInstruction implements Instruction {
    private final Table table;
    private final List<String> filePaths;
    private final String key;
    private final List<String> columns;
    private final Map<String, MetadataType> metadataColumns;

    public LoadInstruction(Table table, List<String> filePaths, String key, List<String> columns,
            Map<String, MetadataType> metadataColumns) {
        this.table = table;
        this.filePaths = filePaths;
        this.key = key;
        this.columns = columns;
        this.metadataColumns = metadataColumns;
    }

    public void execute() {
        Adapter adapter = new Adapter(key, columns, metadataColumns);
        Table newTable;

        for (String filePath : filePaths) {
            try {
                newTable = adapter.extractTable(filePath);
            }
            catch (FileNotFoundException | UnsupportedFileExtensionException e) {
                throw new RuntimeException(e);
            }

            table.concatenate(newTable);
        }
    }
}
