package pt.up.fe.els2022.instructions;

import java.util.List;
import java.util.Set;

public class SumInstruction extends FunctionInstruction {
    public SumInstruction(String source, Set<String> columns, Set<String> excludeColumns, String target) {
        super(source, columns, excludeColumns, target);
    }

    @Override
    protected String applyToColumn(List<String> column) {
        double total = 0.0;

        for (String strVal : column) {
            try {
                total += Double.parseDouble(strVal);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }

        return String.valueOf(total);
    }
}
