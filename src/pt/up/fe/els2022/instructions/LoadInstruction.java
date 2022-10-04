package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.AdapterConfiguration;
import pt.up.fe.els2022.adapters.AdapterFactory;
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
        AdapterConfiguration configuration = new AdapterConfiguration(key, columns, metadataColumns);
        Adapter adapter;

        for (String filePath : filePaths) {
            try {
                adapter = AdapterFactory.createAdapter(configuration, filePath);
            }
            catch (FileNotFoundException | UnsupportedFileExtensionException e) {
                throw new RuntimeException(e);
            }

            if (!adapter.acceptsConfiguration()) {
                throw new IllegalArgumentException("Missing required arguments for load instruction.");
            }
            Table newTable = adapter.extractTable();
            table.concatenate(newTable);
        }
    }
}
