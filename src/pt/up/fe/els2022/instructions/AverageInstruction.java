package pt.up.fe.els2022.instructions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AverageInstruction extends FunctionInstruction {
    private static final DecimalFormat format = new DecimalFormat(
        "#.#####",
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)
    );

    public AverageInstruction(String target, Set<String> columns, Set<String> excludeColumns) {
        super(target, columns, excludeColumns);
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

        return format.format(total / column.size());
    }
}
