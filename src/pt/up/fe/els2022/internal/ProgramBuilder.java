package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.model.Program;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramBuilder {
    private final List<Builder<Instruction>> builders;

    public ProgramBuilder() {
        builders = new ArrayList<>();
    }

    public LoadStructuredBuilder loadStructured() {
        LoadStructuredBuilder builder = new LoadStructuredBuilder(this);
        builders.add(builder);
        return builder;
    }

    public LoadUnstructuredBuilder loadUnstructured() {
        LoadUnstructuredBuilder builder = new LoadUnstructuredBuilder(this);
        builders.add(builder);
        return builder;
    }

    public RenameBuilder rename() {
        RenameBuilder builder = new RenameBuilder(this);
        builders.add(builder);
        return builder;
    }

    public MergeBuilder merge() {
        MergeBuilder builder = new MergeBuilder(this);
        builders.add(builder);
        return builder;
    }

    public SaveBuilder save() {
        SaveBuilder builder = new SaveBuilder(this);
        builders.add(builder);
        return builder;
    }

    public Program create() {
        return new Program(builders.stream().map(Builder::create).collect(Collectors.toList()));
    }
}
