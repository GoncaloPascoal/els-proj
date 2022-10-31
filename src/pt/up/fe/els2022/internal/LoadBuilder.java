package pt.up.fe.els2022.internal;

import pt.up.fe.els2022.model.MetadataType;

import java.util.List;
import java.util.Map;

public abstract class LoadBuilder extends InstructionBuilder {
    protected String target;
    protected List<String> filePaths;
    protected Map<String, MetadataType> metadataColumns;

    public LoadBuilder withTarget(String target) {
        this.target = target;
        return this;
    }

    public LoadBuilder withFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
        return this;
    }

    public LoadBuilder withMetadataColumns(Map<String, MetadataType> metadataColumns) {
        this.metadataColumns = metadataColumns;
        return this;
    }
}
