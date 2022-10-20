package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.utilities.LineStream;

public class TxtAdapter extends Adapter {
    private final SortedSet<Integer> lines;
    private final Map<String, Delimiter> columnDelimiters;
    private final boolean stripWhitespace;

    public TxtAdapter(Map<String, MetadataType> metadataColumns, SortedSet<Integer> lines, Map<String, Delimiter> columnDelimiters, boolean stripWhitespace) {
        super(metadataColumns);
        this.lines = lines;
        this.columnDelimiters = columnDelimiters;
        this.stripWhitespace = stripWhitespace;
    }

    @Override
    public Table extractTable(List<File> files) {
        Table table = new Table();
        Map<String, List<String>> rows = new ListOrderedMap<>();

        List<String> txtLines;
        for (File file : files) {
            txtLines = LineStream.readLines(file);

            for (int line : lines) {
                if (txtLines.size() <= line) {
                    throw new RuntimeException("Source file doesn't contain enough lines.");
                }
                
                // Build the table
                String fileLine = txtLines.get(line);
                for (Map.Entry<String, Delimiter> column : columnDelimiters.entrySet()) {
                    String columnName = column.getKey();
                    Delimiter columnDelimiter = column.getValue();
                    String columnValue = columnDelimiter.getEnd() != null ?
                            fileLine.substring(columnDelimiter.getStart(), columnDelimiter.getEnd()) :
                            fileLine.substring(columnDelimiter.getStart());
                    if (stripWhitespace) {
                        columnValue = columnValue.strip();
                    }
                    rows.putIfAbsent(columnName, new ArrayList<>());
                    rows.get(columnName).add(columnValue);
                }
            }
        }

        table.addRows(rows);
        return table;
    }
}
