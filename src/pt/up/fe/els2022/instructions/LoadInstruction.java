package pt.up.fe.els2022.instructions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Adapter;
import pt.up.fe.els2022.adapters.JsonAdapter;
import pt.up.fe.els2022.adapters.TxtAdapter;
import pt.up.fe.els2022.adapters.XmlAdapter;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.ProgramState;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.els2022.utils.ListFileVisitor;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public class LoadInstruction implements Instruction {
    private final String target;
    private final List<File> files;
    private final String key;
    private final List<String> columns;
    private final Map<String, MetadataType> metadataColumns;

    public LoadInstruction(String target, List<String> filePaths, String key, List<String> columns,
            Map<String, MetadataType> metadataColumns) {
        this.target = target;

        files = new ArrayList<>();
        try {
            for (String path : filePaths) {
                final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + path);
                Files.walkFileTree(Paths.get(""), new ListFileVisitor(files, matcher));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                return new XmlAdapter(metadataColumns, key, columns);
            case "json":
                return new JsonAdapter(metadataColumns, key, columns);
            case "txt":
                return new TxtAdapter(metadataColumns, null, null, true);
            default:
                throw new UnsupportedFileExtensionException(extension);
        }
    }

    public void execute(ProgramState state) {
        Table newTable;
        Adapter adapter;

        try {
            adapter = createAdapter();
        }
        catch (FileNotFoundException | UnsupportedFileExtensionException e) {
            throw new RuntimeException(e);
        }

        if (!adapter.acceptsConfiguration()) {
            throw new IllegalArgumentException("Missing required arguments for load instruction.");
        }

        newTable = adapter.extractMetadataTable(files);
        newTable.merge(adapter.extractTable(files));

        state.getOrCreateTable(target).concatenate(newTable);
    }
}
