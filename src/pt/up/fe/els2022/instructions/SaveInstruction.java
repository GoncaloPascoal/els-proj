package pt.up.fe.els2022.instructions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.specs.util.csv.CsvWriter;

public class SaveInstruction implements Instruction {
    private final Table table;
    private final String path;
    private final List<String> columns;

    public SaveInstruction(Table table, String path, List<String> columns) {
        this.table = table;
        this.path = path;
        this.columns = columns;
    }

    public void execute() {
        if (!table.getColumnNames().containsAll(columns)) {
            throw new RuntimeException("Save instruction references columns that do not exist.");
        }

        File file = new File(path);
        if (!FileUtils.getExtension(file.getName()).equals("csv")) {
            throw new RuntimeException("Destination file must be a CSV file.");
        }

        CsvWriter csvWriter = new CsvWriter(columns);
        for (int i = 0; i < table.numRows(); i++) {
            Map<String, String> row = table.getRow(i);
            csvWriter.addLine(columns.stream().map(c -> row.get(c)).collect(Collectors.toList()));
        }
        String csv = csvWriter.buildCsv();

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(csv);
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred when writing the csv file.");
            e.printStackTrace();
        }
    }
}
