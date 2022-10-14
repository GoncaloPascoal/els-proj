package pt.up.fe.els2022.adapters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public class JsonAdapter extends StructuredAdapter {
    public JsonAdapter(Map<String, MetadataType> metadataColumns, String key, List<String> columns) {
        super(metadataColumns, key, columns);
    }

    public JsonElement findByKey(JsonElement current, String key) {
        if (current.isJsonObject()) {
            JsonObject currentObj = current.getAsJsonObject();
            
            JsonElement element = currentObj.get(key);
            if (element != null) return element;

            for (Entry<String, JsonElement> entry : currentObj.entrySet()) {
                element = findByKey(entry.getValue(), key);
                if (element != null) return element;
            }
        }

        return null;
    }

    @Override
    public Table extractTable(List<File> files) {
        Table table = new Table();
        Map<String, List<String>> rows = new ListOrderedMap<>();

        try {
            for (File file : files) {
                String contents = Files.readString(file.toPath());
                JsonElement root = JsonParser.parseString(contents);

                JsonElement element = findByKey(root, key);
                JsonObject obj = element.getAsJsonObject();

                if (columns.isEmpty()) {
                    // Get all key-value pairs (with primitive values) in object
                    for (Entry<String, JsonElement> entry : obj.entrySet()) {
                        String colName = entry.getKey();
                        JsonElement colValue = entry.getValue();

                        if (colValue.isJsonPrimitive()) {
                            rows.putIfAbsent(colName, new ArrayList<>());
                            rows.get(colName).add(colValue.getAsString());
                        }
                    }
                }
                else {
                    // Get specific key-value pairs in a particular order
                    for (String colName : columns) {
                        JsonElement colValue = obj.get(colName);
                        if (colValue == null || !colValue.isJsonPrimitive()) {
                            throw new RuntimeException("Column " + colName + " does not exist or does not correspond to a primitive value.");
                        }
                        rows.putIfAbsent(colName, new ArrayList<>());
                        rows.get(colName).add(colValue.getAsString());
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        table.addRows(rows);
        return table;
    }
}
