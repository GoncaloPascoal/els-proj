package pt.up.fe.els2022.instructions;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.AdapterFactory;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadInstruction implements Instruction {
    private final Table table;
    private final List<String> filePaths;
    private final String path;
    private final Optional<List<String>> columns;

    public LoadInstruction(Table table, List<String> filePaths, String path, Optional<List<String>> columns) {
        if (columns.isPresent() && columns.get().isEmpty()) {
            columns = Optional.empty();
        }

        this.table = table;
        this.filePaths = filePaths;
        this.path = path;
        this.columns = columns;
    }

    public void execute() {
        Adapter adapter;

        for (String filePath : filePaths) {
            try {
                adapter = AdapterFactory.createAdapter(filePath);
            }
            catch (FileNotFoundException | UnsupportedFileExtensionException e) {
                throw new RuntimeException(e);
            }

            Table newTable = adapter.extractTable(path, columns);
            table.concatenate(newTable);
        }
    }
}
