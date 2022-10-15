package pt.up.fe.els2022.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public class TxtAdapter extends Adapter {
    private final Delimiter lines;
    private final Map<String, Delimiter> columnDelimiters;
    private final boolean stripWhitespace;

    protected TxtAdapter(Map<String, MetadataType> metadataColumns, Delimiter lines, Map<String, Delimiter> columnDelimiters, boolean stripWhitespace) {
        super(metadataColumns);
        this.lines = lines;
        this.columnDelimiters = columnDelimiters;
        this.stripWhitespace = stripWhitespace;
    }

    @Override
    public Table extractTable(List<File> files) {
        Table table = new Table();
        Map<String, List<String>> rows = new ListOrderedMap<>();
        
        BufferedReader bufferReader;
        for (File file : files) {
            try {
                bufferReader = new BufferedReader(new FileReader(file));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            for (int line = 0; line < lines.getEnd(); line++) {
                String fileLine;

                // Read Line from File
                try {
                    fileLine = bufferReader.readLine();
                }
                catch(IOException e) {
                    try { bufferReader.close(); } catch (IOException ignored) {}
                    throw new RuntimeException(e);
                }

                // Check if the file is over
                if (fileLine == null) {
                    try { bufferReader.close(); } catch (IOException ignored) {}
                    throw new RuntimeException("Source file didn't contain enough lines.");
                }
                
                // Lines are only useful when inside the interval [Start, End[
                if (line < lines.getStart()) {
                    continue;
                }

                // Build the Table
                for (Map.Entry<String, Delimiter> column : columnDelimiters.entrySet()) {
                    String columnName = column.getKey();
                    Delimiter columnDelimiter = column.getValue();
                    String columnValue = fileLine.substring(columnDelimiter.getStart(), columnDelimiter.getEnd());
                    if (stripWhitespace) {
                        columnValue = columnValue.strip();
                    }
                    rows.putIfAbsent(columnName, new ArrayList<>());
                    rows.get(columnName).add(columnValue);
                }
            }
            try {
                bufferReader.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        table.addRows(rows);
        return table;
    }
}
