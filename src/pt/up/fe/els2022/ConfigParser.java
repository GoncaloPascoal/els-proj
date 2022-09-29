package pt.up.fe.els2022;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.InstructionFactory;

public class ConfigParser {
    private File file;

    public ConfigParser(File file) {
        this.file = file;
    }

    public ConfigParser(String path) {
        this(new File(path));
    }

    public List<Instruction> getInstructions() throws FileNotFoundException {
        List<Instruction> instructions = new ArrayList<>();

        InputStream inputStream = new FileInputStream(file);
        Yaml yaml = new Yaml();
        List<Map<String, Map<String, Object>>> rawInstructions = yaml.load(inputStream);

        for (var rawInstruction : rawInstructions) {
            for (var entry : rawInstruction.entrySet()) {
                String type = entry.getKey();
                Map<String, Object> args = entry.getValue();
                instructions.add(InstructionFactory.createInstruction(type, args));
            }
        }

        return instructions;
    }
}
