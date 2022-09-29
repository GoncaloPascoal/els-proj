package pt.up.fe.els2022.instructions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.CollectionUtils;
import pt.up.fe.specs.util.SpecsCollections;

public class InstructionFactory {
    // TODO: Refactor this
    private static Table table = new Table();

    public static Instruction createInstruction(String type, Map<String, Object> args) {
        switch (type) {
            case "load": {
                Object filesObj = args.get("files");
                Object keyObj = args.get("key");
                Object columnsObj = args.getOrDefault("columns", Collections.emptyList());

                if (filesObj == null || keyObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for load instruction.");
                }

                if (!(filesObj instanceof List<?> && keyObj instanceof String && columnsObj instanceof List<?>)) {
                    throw new IllegalArgumentException("Incorrect argument types for load instruction.");
                }

                try {
                    List<String> files = SpecsCollections.cast((List<?>) filesObj, String.class);
                    String key = (String) keyObj;
                    List<String> columns = SpecsCollections.cast((List<?>) columnsObj, String.class);

                    return new LoadInstruction(table, files, key, columns);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for load instruction.");
                }
            }
            case "rename": {
                Object mappingObj = args.get("mapping");

                if (mappingObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for rename instruction.");
                }

                try {
                    Map<String, String> mapping = CollectionUtils.castMap((Map<?, ?>) mappingObj, String.class, String.class);

                    return new RenameInstruction(table, mapping);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for rename instruction.");
                }
            }
            case "save": {
                Object fileObj = args.get("file");
                Object columnsObj = args.getOrDefault("columns", Collections.emptyList());

                if (fileObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for save instruction.");
                }

                if (!(fileObj instanceof String && columnsObj instanceof List<?>)) {
                    throw new IllegalArgumentException("Incorrect argument types for save instruction.");
                }

                try {
                    String file = (String) fileObj;
                    List<String> columns = SpecsCollections.cast((List<?>) columnsObj, String.class);

                    return new SaveInstruction(table, file, columns);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for save instruction.");
                }
            }
            default:
                throw new IllegalArgumentException(type + " is not a valid instruction type.");
        }
    }
}
