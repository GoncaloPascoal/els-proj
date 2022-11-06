package pt.up.fe.els2022.internal.text;

import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.instructions.text.ColumnIntervalInstruction;
import pt.up.fe.els2022.instructions.text.TextInstruction;

public class ColumnIntervalBuilder extends TextInstructionBuilder {
    private List<Interval> lines;
    private Map<String, Interval> columnIntervals;
    private boolean stripWhitespace = true;
    
    public ColumnIntervalBuilder withLines(List<Interval> lines) {
        this.lines = lines;
        return this;
    }

    public ColumnIntervalBuilder withColumnIntervals(Map<String, Interval> columnIntervals) {
        this.columnIntervals = columnIntervals;
        return this;
    }
    
    public ColumnIntervalBuilder withStripWhitespace(boolean stripWhitespace) {
        this.stripWhitespace = stripWhitespace;
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
        return new ColumnIntervalInstruction(lines, columnIntervals, stripWhitespace);
    }
}
