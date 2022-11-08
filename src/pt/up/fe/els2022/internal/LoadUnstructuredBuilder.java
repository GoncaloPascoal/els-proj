package pt.up.fe.els2022.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.LoadUnstructuredInstruction;
import pt.up.fe.els2022.internal.text.ColumnIntervalBuilder;
import pt.up.fe.els2022.internal.text.RegexLineDelimiterBuilder;
import pt.up.fe.els2022.internal.text.TextInstructionBuilder;

public class LoadUnstructuredBuilder extends LoadBuilder<LoadUnstructuredBuilder> {
    private final List<TextInstructionBuilder> builders;

    public LoadUnstructuredBuilder(ProgramBuilder parent) {
        super(parent);
        builders = new ArrayList<>();
    }

    public ColumnIntervalBuilder columnInterval() {
        var builder = new ColumnIntervalBuilder(this);
        builders.add(builder);
        return builder;
    }

    public RegexLineDelimiterBuilder regexLineDelimiter() {
        var builder = new RegexLineDelimiterBuilder(this);
        builders.add(builder);
        return builder;
    }

    @Override
    protected Instruction createUnsafe() {
        var textInstructions = builders.stream().map(Builder::create).collect(Collectors.toList());
        return new LoadUnstructuredInstruction(target, filePaths, metadataColumns,textInstructions);
    }
}
