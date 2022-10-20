package pt.up.fe.els2022.instructions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.utils.CollectionUtils;
import pt.up.fe.specs.util.SpecsCollections;

public class InstructionFactory {
    public static Instruction createInstruction(String type, Map<String, Object> args) {
        switch (type) {
            case "load": { // TODO: refactor this
                Object targetObj = args.get("target");
                Object filesObj = args.get("files");
                Object keyObj = args.get("key");
                Object columnsObj = args.getOrDefault("columns", Collections.emptyList());
                Object metadataColumnsObj = args.getOrDefault("metadataColumns", Collections.emptyMap());

                if (targetObj == null || filesObj == null || keyObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for load instruction.");
                }

                if (!(targetObj instanceof String && filesObj instanceof List<?> && keyObj instanceof String &&
                        columnsObj instanceof List<?> && metadataColumnsObj instanceof Map<?, ?>)) {
                    throw new IllegalArgumentException("Incorrect argument types for load instruction.");
                }

                try {
                    String target = (String) targetObj;
                    List<String> files = SpecsCollections.cast((List<?>) filesObj, String.class);
                    String key = (String) keyObj;
                    List<String> columns = SpecsCollections.cast((List<?>) columnsObj, String.class);
                    Map<String, MetadataType> metadataColumns = CollectionUtils.castMap(
                        (Map<?, ?>) metadataColumnsObj, String.class, String.class)
                        .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
                            var mdType = MetadataType.fromId(e.getValue());
                            if (mdType == null)
                                throw new IllegalArgumentException(e.getValue() + " is not a valid type of metadata.");
                            return mdType;
                        }));

                    return new LoadInstruction(target, files, key, columns, metadataColumns);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for load instruction.");
                }
            }
            case "rename": {
                Object sourceObj = args.get("source");
                Object mappingObj = args.get("mapping");

                if (sourceObj == null || mappingObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for rename instruction.");
                }

                if (!(sourceObj instanceof String && mappingObj instanceof Map<?, ?>)) {
                    throw new IllegalArgumentException("Incorrect argument types for rename instruction.");
                }

                try {
                    String source = (String) sourceObj;
                    Map<String, String> mapping = CollectionUtils.castMap((Map<?, ?>) mappingObj, String.class, String.class);

                    return new RenameInstruction(source, mapping);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for rename instruction.");
                }
            }
            case "merge": {
                Object tablesObj = args.get("tables");
                Object targetObj = args.get("target");

                if (tablesObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for merge instruction.");
                }

                if (!(tablesObj instanceof List<?> && (targetObj == null || targetObj instanceof String))) {
                    throw new IllegalArgumentException("Incorrect argument types for merge instruction.");
                }

                try {
                    List<String> tables = SpecsCollections.cast((List<?>) tablesObj, String.class);
                    String target = targetObj == null ? null : (String) targetObj;

                    return new MergeInstruction(tables, target);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for merge instruction.");
                }
            }
            case "save": {
                Object sourceObj = args.get("source");
                Object fileObj = args.get("file");
                Object columnsObj = args.getOrDefault("columns", Collections.emptyList());

                if (sourceObj == null || fileObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for save instruction.");
                }

                if (!(sourceObj instanceof String && fileObj instanceof String && columnsObj instanceof List<?>)) {
                    throw new IllegalArgumentException("Incorrect argument types for save instruction.");
                }

                try {
                    String source = (String) sourceObj;
                    String file = (String) fileObj;
                    List<String> columns = SpecsCollections.cast((List<?>) columnsObj, String.class);

                    return new SaveInstruction(source, file, columns);
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
