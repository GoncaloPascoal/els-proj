package pt.up.fe.els2022.internal.text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.instructions.text.ColumnIntervalInstruction;
import pt.up.fe.els2022.instructions.text.TextInstruction;
import pt.up.fe.els2022.internal.LoadUnstructuredBuilder;

public class ColumnIntervalBuilder extends TextInstructionBuilder {
    private List<Interval> lines;
    private Map<String, Interval> columnIntervals;
    private Boolean stripWhitespace;
    private String columnarFormat;

    public ColumnIntervalBuilder(LoadUnstructuredBuilder parent) {
        super(parent);
        this.columnIntervals = new ListOrderedMap<>();
    }

    public ColumnIntervalBuilder withLines(List<Interval> lines) {
        this.lines = lines;
        return this;
    }

    public ColumnIntervalBuilder withLines(Interval... lines) {
        this.lines = Arrays.asList(lines);
        return this;
    }

    public ColumnIntervalBuilder addColumnInterval(String column, Interval interval) {
        columnIntervals.put(column, interval);
        return this;
    }

    public ColumnIntervalBuilder withColumnIntervals(Map<String, Interval> columnIntervals) {
        this.columnIntervals = columnIntervals;
        return this;
    }

    public ColumnIntervalBuilder withStripWhitespace(Boolean stripWhitespace) {
        this.stripWhitespace = stripWhitespace;
        return this;
    }

    public ColumnIntervalBuilder withColumnarFormat(String columnarFormat) {
        this.columnarFormat = columnarFormat;
        return this;
    }

    @Override
    protected void validate() {
        if (lines == null || columnIntervals == null) {
            throw new RuntimeException("Missing arguments for columnInterval instruction.");
        }
    }

    @Override
    protected TextInstruction createUnsafe() {
        return new ColumnIntervalInstruction(lines, columnIntervals, stripWhitespace, columnarFormat);
    }
}
