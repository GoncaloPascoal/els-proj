package pt.up.fe.els2022.instructions.text;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ListOrderedMap;

import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.utilities.LineStream;

public class RegexLineDelimiterInstruction implements TextInstruction {
    private final List<String> linePatterns;
    private final String delimiter;

    public RegexLineDelimiterInstruction(List<String> linePatterns, String delimiter) {
        this.linePatterns = linePatterns;
        this.delimiter = delimiter;
        // TODO: add optional parameter to use result of RegEx match as column name
    }

    @Override
    public void execute(File file, Table table) {
        Map<String, String> row = new ListOrderedMap<>();

        List<String> lines = LineStream.readLines(file);
        List<Pattern> patterns = linePatterns.stream().map(Pattern::compile).collect(Collectors.toList());

        for (Pattern pattern : patterns) {
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);

                if (matcher.lookingAt()) {
                    String[] split = line.split(delimiter, 2);
                    if (split.length == 2) {
                        String name = split[0].strip();
                        String value = split[1].strip();
                        row.put(name, value);
                    }
                }
            }
        }

        table.addRow(row);
    }
}
