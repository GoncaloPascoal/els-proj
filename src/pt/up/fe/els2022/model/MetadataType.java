package pt.up.fe.els2022.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum MetadataType {
    FILE_NAME("fileName", f -> f.getName()),
    FILE_PATH("filePath", f -> f.getPath()),
    ABSOLUTE_FILE_PATH("absoluteFilePath", f -> f.getAbsolutePath());

    private final String id;
    private final Function<File, String> function;

    private MetadataType(String id, Function<File, String> function) {
        this.id = id;
        this.function = function;
    }

    private static Map<String, MetadataType> idMap = new HashMap<>();
    static {
        for (var type : MetadataType.values()) {
            idMap.put(type.id, type);
        }
    }

    public static MetadataType fromId(String id) {
        return idMap.get(id);
    }

    public String value(File file) {
        return function.apply(file);
    }
}
