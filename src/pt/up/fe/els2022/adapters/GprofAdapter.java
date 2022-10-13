package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;

public class GprofAdapter extends Adapter {

    public GprofAdapter(String key, List<String> columns, Map<String, MetadataType> metadataColumns) {
        super(key, columns, metadataColumns);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Table extractTable(List<File> files) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
