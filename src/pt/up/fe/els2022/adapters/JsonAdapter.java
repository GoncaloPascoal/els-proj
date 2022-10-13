package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public class JsonAdapter extends StructuredAdapter {
    public JsonAdapter(Map<String, MetadataType> metadataColumns, String key, List<String> columns) {
        super(metadataColumns, key, columns);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Table extractTable(List<File> files) {
        // TODO Auto-generated method stub
        return null;
    }
}
