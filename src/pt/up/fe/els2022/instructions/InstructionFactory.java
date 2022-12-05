package pt.up.fe.els2022.instructions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.up.fe.els2022.instructions.text.TextInstruction;
import pt.up.fe.els2022.instructions.text.TextInstructionFactory;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.utils.CollectionUtils;
import pt.up.fe.specs.util.SpecsCollections;

public class InstructionFactory {
    private static class LoadParameters {
        public String target;
        public List<String> files;
        public Map<String, MetadataType> metadataColumns;
        public String columnSuffix;

        public LoadParameters(String target, List<String> files, Map<String, MetadataType> metadataColumns, String columnSuffix) {
            this.target = target;
            this.files = files;
            this.metadataColumns = metadataColumns;
            this.columnSuffix = columnSuffix;
        }
    }

    private static LoadParameters parseLoadParameters(Map<String, Object> args) {
        Object targetObj = args.get("target");
        Object filesObj = args.get("files");
        Object metadataColumnsObj = args.getOrDefault("metadataColumns", Collections.emptyMap());
        Object columnSuffixObj = args.get("columnSuffix");

        if (targetObj == null || filesObj == null) {
            throw new IllegalArgumentException("Missing required arguments for load instruction.");
        }

        if (!(targetObj instanceof String && filesObj instanceof List<?> && metadataColumnsObj instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("Incorrect argument types for load instruction.");
        }

        try {
            String target = (String) targetObj;
            List<String> files = SpecsCollections.cast((List<?>) filesObj, String.class);
            Map<String, MetadataType> metadataColumns = CollectionUtils.castMap(
                            (Map<?, ?>) metadataColumnsObj, String.class, String.class)
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        var mdType = MetadataType.fromId(e.getValue());
                        if (mdType == null)
                            throw new IllegalArgumentException(e.getValue() + " is not a valid type of metadata.");
                        return mdType;
                    }));
            String columnSuffix = columnSuffixObj != null ? (String) columnSuffixObj : null;

            return new LoadParameters(target, files, metadataColumns, columnSuffix);
        }
        catch (RuntimeException ex) {
            throw new IllegalArgumentException("Incorrect argument types for load instruction.");
        }
    }

    public static Instruction createInstruction(String type, Map<String, Object> args) {
        switch (type) {
            case "loadStructured": {
                LoadParameters loadParameters = parseLoadParameters(args);
                Object pathsObj = args.get("paths");
                Object columnsObj = args.getOrDefault("columns", Collections.emptyList());

                if (pathsObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for loadStructured instruction.");
                }

                if (!(pathsObj instanceof List<?> && (columnsObj == null || columnsObj instanceof List<?>))) {
                    throw new IllegalArgumentException("Incorrect argument types for loadStructured instruction.");
                }

                try {
                    List<String> paths = SpecsCollections.cast((List<?>) pathsObj, String.class);
                    List<String> columns = columnsObj == null ? null : SpecsCollections.cast((List<?>) columnsObj, String.class);
                    return new LoadStructuredInstruction(loadParameters.target,
                        loadParameters.files, loadParameters.metadataColumns,
                        loadParameters.columnSuffix, paths, columns);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for loadStructured instruction: " + ex.getMessage());
                }
            }
            case "loadUnstructured": {
                LoadParameters loadParameters = parseLoadParameters(args);
                Object instructionsObj = args.get("instructions");

                if (instructionsObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for loadUnstructured instruction.");
                }

                if (!(instructionsObj instanceof List<?>)) {
                    throw new IllegalArgumentException("Incorrect argument types for loadUnstructured instruction.");
                }

                try {
                    List<Map> genericMap = SpecsCollections.cast((List<?>) instructionsObj, Map.class);
                    List<TextInstruction> instructions = genericMap.stream().map(i -> {
                        Map<String, Map> m = CollectionUtils.castMap(i, String.class, Map.class);
                        var raw = m.entrySet().stream().findFirst().get();
                        return TextInstructionFactory.createInstruction(
                            raw.getKey(),
                            CollectionUtils.castMap(raw.getValue(), String.class, Object.class)
                        );
                    }).collect(Collectors.toList());

                    return new LoadUnstructuredInstruction(loadParameters.target,
                        loadParameters.files, loadParameters.metadataColumns,
                        loadParameters.columnSuffix, instructions);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for loadUnstructured instruction: " + ex.getMessage());
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
                    throw new IllegalArgumentException("Incorrect argument types for rename instruction: " + ex.getMessage());
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
                    throw new IllegalArgumentException("Incorrect argument types for merge instruction: " + ex.getMessage());
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
                    throw new IllegalArgumentException("Incorrect argument types for save instruction: " + ex.getMessage());
                }
            }
            case "sort": {
                Object targetObj = args.get("target");
                Object columnObj = args.get("column");
                Object descendingObj = args.get("descending");

                if (targetObj == null || columnObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for sort instruction.");
                }

                if (!(targetObj instanceof String && columnObj instanceof String && (descendingObj == null || descendingObj instanceof Boolean))) {
                    throw new IllegalArgumentException("Incorrect argument types for sort instruction.");
                }

                try {
                    String target = (String) targetObj;
                    String column = (String) columnObj;
                    Boolean descending = descendingObj == null ? null : (Boolean) descendingObj;

                    return new SortInstruction(target, column, descending);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for sort instruction: " + ex.getMessage());
                }
            }
            default:
                throw new IllegalArgumentException(type + " is not a valid instruction type.");
        }
    }
}
