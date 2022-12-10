package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.model.MetadataType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;

public abstract class LoadBuilder<T extends LoadBuilder<T>> extends InstructionBuilder {
    protected String target;
    protected List<String> files;
    protected Map<String, MetadataType> metadataColumns;
    protected String columnSuffix;

    public LoadBuilder(ProgramBuilder parent) {
        super(parent);
        this.metadataColumns = new ListOrderedMap<>();
    }

    public T withTarget(String target) {
        this.target = target;
        return thisSubclass();
    }

    public T withFiles(List<String> files) {
        this.files = files;
        return thisSubclass();
    }

    public T withFiles(String... files) {
        this.files = Arrays.asList(files);
        return thisSubclass();
    }

    public T addMetadataColumn(String column, MetadataType type) {
        this.metadataColumns.put(column, type); 
        return thisSubclass();
    }

    public T withMetadataColumns(Map<String, MetadataType> metadataColumns) {
        this.metadataColumns = metadataColumns;
        return thisSubclass();
    }

    public T withColumnSuffix(String columnSuffix) {
        this.columnSuffix = columnSuffix;
        return thisSubclass();
    }

    @SuppressWarnings("unchecked")
    private T thisSubclass() {
        return (T) this;
    }

    @Override
    protected void validate() {
        if (target == null || files == null) {
            throw new RuntimeException("Missing arguments for load instruction.");
        }
    }
}
