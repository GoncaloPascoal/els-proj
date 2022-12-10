package pt.up.fe.els2022;

import java.io.File;

import pt.up.fe.els2022.model.Program;
import pt.up.fe.els2022.parser.TabularParser;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: els2022-g1 path_to_config");
            return;
        }

        File file = new File(args[0]);

        if (!file.exists() || !file.canRead()) {
            System.err.println("Invalid file path.");
            return;
        }

        TabularParser parser = new TabularParser();
        Program program = parser.parse(new File(args[0]));
        program.execute();
    }
}