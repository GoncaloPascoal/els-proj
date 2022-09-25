package pt.up.fe.els2022;

import java.io.FileNotFoundException;
import java.util.List;

import pt.up.fe.els2022.instructions.Instruction;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: els2022-g1 path_to_config");
            return;
        }

        // Read config file from args
        ConfigParser configParser = new ConfigParser(args[0]);

        // Convert config file to instructions
        List<Instruction> instructions;
        try {
            instructions = configParser.getInstructions();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Execute instructions
        for (Instruction instruction : instructions) {
            instruction.execute();
        }
    }
}