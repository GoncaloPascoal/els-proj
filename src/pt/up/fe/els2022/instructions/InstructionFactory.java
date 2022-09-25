package pt.up.fe.els2022.instructions;

import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.CollectionUtils;
import pt.up.fe.specs.util.SpecsCollections;

public class InstructionFactory {
    // TODO: Refactor this
    private static Table table = new Table();

    public static Instruction createInstruction(String instructionType, Map<String, Object> instructionArgs) {
        // TODO: Perhaps throw exception instead of returning a null instruction?
        switch (instructionType) {
            case "load": {
                Object filesObj = instructionArgs.get("file");
                Object tagObj = instructionArgs.get("tag");
                Object columnsObj = instructionArgs.get("columns");

                if (filesObj == null || tagObj == null || columnsObj == null) {
                    System.err.println("Missing arguments for load instruction.");
                    return null;
                }

                if (!(filesObj instanceof List<?> && tagObj instanceof String && columnsObj instanceof List<?>)) {
                    System.err.println("Incorrect argument types for load instruction.");
                    return null;
                }

                try {
                    List<String> files = SpecsCollections.cast((List<?>) filesObj, String.class);
                    String tag = (String) tagObj;
                    List<String> columns = SpecsCollections.cast((List<?>) columnsObj, String.class);
                    return new LoadInstruction(table, files, tag, columns);
                }
                catch (RuntimeException ex) {
                    System.err.println("Incorrect argument types for load instruction.");
                    return null;
                }
            }
            case "rename": {
                Object mappingObj = instructionArgs.get("mapping");

                if (mappingObj == null) {
                    System.err.println("Missing arguments for rename instruction.");
                    return null;
                }

                try {
                    Map<String, String> mapping = CollectionUtils.castMap((Map<?, ?>) mappingObj, String.class, String.class);
                    return new RenameInstruction(table, mapping);
                }
                catch (RuntimeException ex) {
                    System.err.println("Incorrect argument types for rename instruction.");
                    return null;
                }
            }
            case "save": {
                Object fileObj = instructionArgs.get("file");
                Object columnsObj = instructionArgs.get("columns");

                if (fileObj == null || columnsObj == null) {
                        System.err.println("Missing arguments for save instruction.");
                    return null;
                }

                if (!(fileObj instanceof String && columnsObj instanceof List<?>)) {
                    System.err.println("Incorrect argument types for save instruction.");
                    return null;
                }

                try {
                    String file = (String) fileObj;
                    List<String> columns = SpecsCollections.cast((List<?>) columnsObj, String.class);
                    return new SaveInstruction(table, file, columns);
                }
                catch (RuntimeException ex) {
                    System.err.println("Incorrect argument types for save instruction.");
                    return null;
                }
            }
            default: {
                return null;
            }
        }
    }
}
