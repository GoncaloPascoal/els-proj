package pt.up.fe.els2022.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class FunctionBuilder<T extends FunctionBuilder<T>> extends InstructionBuilder {
    protected String target;
    protected Set<String> columns, excludeColumns;

    public FunctionBuilder(ProgramBuilder parent) {
        super(parent);
    }

    public T withTarget(String target) {
        this.target = target;
        return thisSubclass();
    }

    public T withColumns(Set<String> columns) {
        this.columns = columns;
        return thisSubclass();
    }

    public T withColumns(List<String> columns) {
        this.columns = new HashSet<>(columns);
        return thisSubclass();
    }

    public T withColumns(String... columns) {
        this.columns = Set.of(columns);
        return thisSubclass();
    }

    public T withExcludeColumns(List<String> excludeColumns) {
        this.excludeColumns = new HashSet<>(excludeColumns);
        return thisSubclass();
    }

    public T withExcludeColumns(String... excludeColumns) {
        this.excludeColumns = Set.of(excludeColumns);
        return thisSubclass();
    }

    @SuppressWarnings("unchecked")
    private T thisSubclass() {
        return (T) this;
    }

    @Override
    protected void validate() {
        if (target == null) {
            throw new RuntimeException("Missing arguments for function instruction.");
        }
    }
}
