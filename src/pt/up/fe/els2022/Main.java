package pt.up.fe.els2022;

import java.io.File;
import java.io.FileNotFoundException;

import pt.up.fe.els2022.model.Program;
import pt.up.fe.els2022.parser.ConfigParser;
import pt.up.fe.els2022.parser.TabularParser;
import pt.up.fe.els2022.utils.FileUtils;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: els2022-g1 path_to_script");
            return;
        }

        File file = new File(args[0]);

        if (!file.exists() || !file.canRead()) {
            System.err.println("Invalid file path.");
            return;
        }

        String extension = FileUtils.getExtension(file);
        Program program;

        switch (extension) {
            case "yaml":
                ConfigParser configParser = new ConfigParser(file);
                try {
                    program = new Program(configParser.getInstructions());
                }
                catch (FileNotFoundException ex) {
                    System.err.println(ex.getMessage());
                    return;
                }
                break;
            case "tb":
            case "tabular":
                TabularParser tabularParser = new TabularParser();
                program = tabularParser.parse(file);
                break;
            default:
                System.err.println("Invalid script extension!");
                return;
        }

        program.execute();
    }
}