package pt.up.fe.els2022.instructions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.ListFileVisitor;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public abstract class LoadInstruction implements Instruction {
    protected final String target;
    protected final List<File> files;
    protected final Map<String, MetadataType> metadataColumns;
    protected final String columnSuffix;

    public LoadInstruction(String target, List<String> filePaths, Map<String, MetadataType> metadataColumns, String columnSuffix) {
        this.target = target;

        files = new ArrayList<>();
        try {
            for (String path : filePaths) {
                Pattern p = Pattern.compile(path);
                Files.walkFileTree(Paths.get(""), new ListFileVisitor(files, p));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one source file.");
        }

        if (metadataColumns == null) {
            metadataColumns = Collections.emptyMap();
        }
        this.metadataColumns = metadataColumns;

        this.columnSuffix = columnSuffix;
    }

    protected abstract Adapter createAdapter() throws FileNotFoundException, UnsupportedFileExtensionException;

    public void execute(ProgramState state) {
        Table newTable = Adapter.extractMetadataTable(metadataColumns, files);

        Adapter adapter;

        try {
            adapter = createAdapter();
        } catch (FileNotFoundException | UnsupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }

        Table dataTable = adapter.extractTable(files);

        if (columnSuffix != null) {
            for (String colName : Set.copyOf(dataTable.getColumnNames())) {
                dataTable.renameColumn(colName, colName + columnSuffix);
            }
        }

        newTable.merge(dataTable);
        state.getOrCreateTable(target).concatenate(newTable);
    }
}