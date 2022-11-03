package pt.up.fe.els2022.instructions.text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.utilities.LineStream;

public class ColumnIntervalInstruction implements TextInstruction {
    private final SortedSet<Integer> lines;
    private final Map<String, Interval> columnIntervals;
    private final boolean stripWhitespace;

    public ColumnIntervalInstruction(SortedSet<Integer> lines,
            Map<String, Interval> columnIntervals, Boolean stripWhitespace) {
        this.lines = lines;
        this.columnIntervals = columnIntervals;

        if (stripWhitespace == null) stripWhitespace = true;
        this.stripWhitespace = stripWhitespace;
    }

    @Override
    public void execute(File file, Table table) {
        Map<String, List<String>> rows = new ListOrderedMap<>();

        List<String> txtLines = LineStream.readLines(file);

        for (int line : lines) {
            if (txtLines.size() <= line) {
                throw new RuntimeException("Source file doesn't contain enough lines.");
            }

            // Build the table
            String txtLine = txtLines.get(line - 1);
            for (Map.Entry<String, Interval> column : columnIntervals.entrySet()) {
                String name = column.getKey();
                Interval interval = column.getValue();
                String value = interval.getEnd() != null ?
                        txtLine.substring(interval.getStart(), interval.getEnd()) :
                        txtLine.substring(interval.getStart());

                if (stripWhitespace) {
                    value = value.strip();
                }

                rows.putIfAbsent(name, new ArrayList<>());
                rows.get(name).add(value);
            }
        }

        table.addRows(rows);
    }
}
