package pt.up.fe.els2022;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParser;
import org.yaml.snakeyaml.Yaml;

import com.google.inject.Inject;

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.instructions.InstructionFactory;
import pt.up.fe.els2022.tabular.TabularStandaloneSetup;
import pt.up.fe.els2022.tabular.tabular.Merge;
import pt.up.fe.specs.util.classmap.ClassMap;
import pt.up.fe.specs.util.classmap.FunctionClassMap;

public class ConfigParser {
    private final File file;

    @Inject
    private IParser parser;

    public ConfigParser(File file) {
        this.file = file;
        var injector = new TabularStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
    }

    public ConfigParser(String path) {
        this(new File(path));
    }

    public List<Instruction> getInstructions() throws FileNotFoundException {
        var result = parser.parse(new FileReader(file));

        FunctionClassMap<EObject, Instruction> map = new FunctionClassMap<>();

        List<Instruction> instructions = new ArrayList<>();

        // InputStream inputStream = new FileInputStream(file);
        // Yaml yaml = new Yaml();
        // List<Map<String, Map<String, Object>>> rawInstructions = yaml.load(inputStream);

        // for (var rawInstruction : rawInstructions) {
        //     for (var entry : rawInstruction.entrySet()) {
        //         String type = entry.getKey();
        //         Map<String, Object> args = entry.getValue();
        //         instructions.add(InstructionFactory.createInstruction(type, args));
        //     }
        // }

        return instructions;
    }

    public Integer merge(Merge merge) {
        return 0;
    }
}
