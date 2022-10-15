package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.CollectionUtils;

public class GprofAdapter extends TxtAdapter {
    private static final Delimiter LINES = new Delimiter(5, 21);
    private static final Map<String, Delimiter> COLUMN_DELIMETERS = CollectionUtils.buildMap(
        Arrays.asList(
            "% time", "cumulative seconds", "self seconds",
            "calls", "self s/calls", "total s/calls", "name"
        ),
        Arrays.asList(
            new Delimiter(0, 7), new Delimiter(7, 17), new Delimiter(17, 26),
            new Delimiter(26, 35), new Delimiter(35, 44), new Delimiter(44, 53),
            new Delimiter(53, null)
        )
    );

    public GprofAdapter(Map<String, MetadataType> metadataColumns) {
        super(metadataColumns, LINES, COLUMN_DELIMETERS, true);
    }

    @Override
    public Table extractTable(List<File> files) {
        Table completeTable = super.extractTable(files);
        // TODO Auto-generated method stub
        return completeTable;
    }
}
