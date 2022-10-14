package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public class TxtAdapter extends Adapter {
    private final Map<String, Delimiter> columnDelimiters;
    private final boolean stripWhitespace;

    protected TxtAdapter(Map<String, MetadataType> metadataColumns, Map<String, Delimiter> columnDelimiters, boolean stripWhitespace) {
        super(metadataColumns);
        this.columnDelimiters = columnDelimiters;
        this.stripWhitespace = stripWhitespace;
    }

    @Override
    public Table extractTable(List<File> files) {
        // TODO Auto-generated method stub
        return null;
    }
}
