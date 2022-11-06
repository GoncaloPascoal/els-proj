package pt.up.fe.els2022.internal.text;

import java.util.List;

import pt.up.fe.els2022.instructions.text.RegexLineDelimiterInstruction;
import pt.up.fe.els2022.instructions.text.TextInstruction;

public class RegexLineDelimiterBuilder extends TextInstructionBuilder {
    private List<String> linePatterns;
    private String delimiter;

    public RegexLineDelimiterBuilder withLinePatterns(List<String> linePatterns) {
        this.linePatterns = linePatterns;
        return this;
    }

    public RegexLineDelimiterBuilder withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    protected void validate() {
        if (linePatterns == null || delimiter == null) {
            throw new RuntimeException("Missing arguments for columnInterval instruction.");
        }
    }

    @Override
    protected TextInstruction createUnsafe() {
        return new RegexLineDelimiterInstruction(linePatterns, delimiter);
    }
}
