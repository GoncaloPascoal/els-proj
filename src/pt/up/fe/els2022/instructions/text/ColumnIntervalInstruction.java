package pt.up.fe.els2022.instructions.text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.utilities.LineStream;

public class ColumnIntervalInstruction implements TextInstruction {
    private final SortedSet<Integer> lines;
    private final Map<String, Interval> columnIntervals;
    private final boolean stripWhitespace;
    private final String columnarFormat;

    public ColumnIntervalInstruction(List<Interval> lines,
            Map<String, Interval> columnIntervals, Boolean stripWhitespace,
            String columnarFormat) {
        this.lines = new TreeSet<>();
        lines.forEach(interval -> {
            int start = interval.getStart();
            Integer end = interval.getEnd();
            if (end == null) end = start;

            IntStream.rangeClosed(start, end).forEach(this.lines::add);
        });

        this.columnIntervals = columnIntervals;

        if (stripWhitespace == null) stripWhitespace = true;
        this.stripWhitespace = stripWhitespace;

        this.columnarFormat = columnarFormat;
    }

    @Override
    public void execute(File file, Table table) {
        Map<String, List<String>> rows = new ListOrderedMap<>();

        List<String> txtLines = LineStream.readLines(file);

        int relativeLine = 1;
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
                        txtLine.substring(interval.getStart() - 1, interval.getEnd() - 1) :
                        txtLine.substring(interval.getStart() - 1);

                if (stripWhitespace) {
                    value = value.strip();
                }

                String colName = name;

                if (columnarFormat != null) {
                    // Multiple file lines for the same table row
                    colName = columnarFormat
                        .replace("%n", name)
                        .replace("%a", String.valueOf(line))
                        .replace("%r", String.valueOf(relativeLine));
                }

                rows.putIfAbsent(colName, new ArrayList<>());
                rows.get(colName).add(value);
            }

            ++relativeLine;
        }

        table.addRows(rows);
    }
}
