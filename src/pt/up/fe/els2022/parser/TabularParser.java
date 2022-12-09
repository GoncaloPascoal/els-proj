package pt.up.fe.els2022.parser;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;

import com.google.inject.Inject;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.internal.*;
import pt.up.fe.els2022.internal.text.ColumnIntervalBuilder;
import pt.up.fe.els2022.internal.text.RegexLineDelimiterBuilder;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Program;
import pt.up.fe.els2022.tabular.TabularStandaloneSetup;
import pt.up.fe.els2022.tabular.tabular.*;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.classmap.FunctionClassMap;
import pt.up.fe.specs.util.classmap.BiConsumerClassMap;

public class TabularParser {
    @Inject
    private IParser parser;

    public TabularParser() {
        var injector = new TabularStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
    }

    public Program parse(File file) {
        return parse(SpecsIo.read(file));
    }

    public Program parse(String code) {
        IParseResult result = parser.parse(new StringReader(code));
        FunctionClassMap<DslInstruction, Instruction> instructionParseMap = new FunctionClassMap<>();

        instructionParseMap.put(DslLoadStructured.class, this::loadStructured);
        instructionParseMap.put(DslLoadUnstructured.class, this::loadUnstructured);
        instructionParseMap.put(DslMerge.class, this::merge);
        instructionParseMap.put(DslRename.class, this::rename);
        instructionParseMap.put(DslSort.class, this::sort);
        instructionParseMap.put(DslAverage.class, this::average);
        instructionParseMap.put(DslSum.class, this::sum);
        instructionParseMap.put(DslSave.class, this::save);

        if (result.hasSyntaxErrors()) {
            StringBuilder errBuilder = new StringBuilder();

            errBuilder.append("Provided script has syntax errors:\n");

            for (var error : result.getSyntaxErrors()) {
                errBuilder.append("- Line ").append(error.getStartLine()).append(": ")
                    .append(error.getSyntaxErrorMessage().getMessage());
            }

            throw new IllegalArgumentException(errBuilder.toString());
        }

        var model = (DslModel) result.getRootASTElement();

        List<Instruction> instructions = new ArrayList<>();
        for (DslInstruction dslInstruction : model.getInstructions()) {
            instructions.add(instructionParseMap.apply(dslInstruction));
        }

        return new Program(instructions);
    }

    private static Interval parseInterval(DslInterval dslInterval) {
        OptionalInt end = dslInterval.getEnd();
        return new Interval(dslInterval.getStart(), end == null ? null : end.getVal());
    }

    private static final Map<String, BiConsumer<LoadBuilder<?>, DslLoadParam>> loadParamMap = Map.ofEntries(
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget())),
        Map.entry("files", (b, p) -> b.withFiles(p.getFiles())),
        Map.entry("metadataColumns", (b, p) -> b.withMetadataColumns(
            p.getMetadataColumns().stream().collect(
                Collectors.toMap(DslLoadMapping::getColumn, e -> MetadataType.fromId(e.getType()))
            )
        )),
        Map.entry("columnSuffix", (b, p) -> b.withColumnSuffix(p.getColumnSuffix()))
    );

    private static final Map<String, BiConsumer<LoadStructuredBuilder, DslLoadStructuredParam>> loadStructuredParamMap = Map.ofEntries(
        Map.entry("paths", (b, p) -> b.withPaths(p.getPaths())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns()))
    );

    private Instruction loadStructured(DslLoadStructured loadStructured) {
        LoadStructuredBuilder builder = new LoadStructuredBuilder(null);

        BiConsumerClassMap<EObject, LoadStructuredBuilder> paramMap = new BiConsumerClassMap<>();
        paramMap.put(DslLoadParam.class, (p, b) -> loadParamMap.get(p.getName()).accept(b, p));
        paramMap.put(DslLoadStructuredParam.class, (p, b) -> loadStructuredParamMap.get(p.getName()).accept(b, p));

        for (EObject param : loadStructured.getParams()) {
            paramMap.accept(param, builder);
        }

        return builder.create();
    }

    private final Map<String, BiConsumer<LoadUnstructuredBuilder, DslLoadUnstructuredParam>> loadUnstructuredParamMap = Map.ofEntries(
        Map.entry("textInstructions", this::parseTextInstructions)
    );

    private void parseTextInstructions(LoadUnstructuredBuilder builder, DslLoadUnstructuredParam param) {
        BiConsumerClassMap<DslTextInstruction, LoadUnstructuredBuilder> instructionParseMap = new BiConsumerClassMap<>();

        instructionParseMap.put(DslColumnInterval.class, this::columnInterval);
        instructionParseMap.put(DslRegexLineDelimiter.class, this::regexLineDelimiter);

        for (DslTextInstruction dslTextInstruction : param.getTextInstructions()) {
            instructionParseMap.accept(dslTextInstruction, builder);
        }
    }

    private static final Map<String, BiConsumer<ColumnIntervalBuilder, DslColumnIntervalParam>> columnIntervalParamMap = Map.ofEntries(
        Map.entry("lines", (b, p) -> b.withLines(p.getLines()
            .stream().map(TabularParser::parseInterval).collect(Collectors.toList()))
        ),
        Map.entry("columnIntervals", (b, p) -> b.withColumnIntervals(p.getColumnIntervals()
            .stream().collect(
                Collectors.toMap(DslColumnIntervalMapping::getColumn, e -> parseInterval(e.getInterval()))
            ))),
        Map.entry("stripWhitespace", (b, p) -> b.withStripWhitespace(p.isStripWhitespace())),
        Map.entry("columnarFormat", (b, p) -> b.withColumnarFormat(p.getColumnarFormat()))
    );

    private void columnInterval(DslColumnInterval columnInterval, LoadUnstructuredBuilder parent) {
        var builder = parent.columnInterval();

        for (DslColumnIntervalParam param : columnInterval.getParams()) {
            columnIntervalParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private static final Map<String, BiConsumer<RegexLineDelimiterBuilder, DslRegexLineDelimiterParam>> regexLineDelimiterParamMap = Map.ofEntries(
        Map.entry("linePatterns", (b, p) -> b.withLinePatterns(p.getLinePatterns())),
        Map.entry("delimiter", (b, p) -> b.withDelimiter(p.getDelimiter()))
    );

    private void regexLineDelimiter(DslRegexLineDelimiter regexLineDelimiter, LoadUnstructuredBuilder parent) {
        var builder = parent.regexLineDelimiter();

        for (DslRegexLineDelimiterParam param : regexLineDelimiter.getParams()) {
            regexLineDelimiterParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private Instruction loadUnstructured(DslLoadUnstructured loadUnstructured) {
        LoadUnstructuredBuilder builder = new LoadUnstructuredBuilder(null);

        BiConsumerClassMap<EObject, LoadUnstructuredBuilder> paramMap = new BiConsumerClassMap<>();
        paramMap.put(DslLoadParam.class, (p, b) -> loadParamMap.get(p.getName()).accept(b, p));
        paramMap.put(DslLoadUnstructuredParam.class, (p, b) -> loadUnstructuredParamMap.get(p.getName()).accept(b, p));

        for (EObject param : loadUnstructured.getParams()) {
            paramMap.accept(param, builder);
        }

        return builder.create();
    }

    private static final Map<String, BiConsumer<MergeBuilder, DslMergeParam>> mergeParamMap = Map.ofEntries(
        Map.entry("tables", (b, p) -> b.withTables(p.getTables())),
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget()))
    );

    private Instruction merge(DslMerge merge) {
        MergeBuilder builder = new MergeBuilder(null);

        for (DslMergeParam param : merge.getParams()) {
            mergeParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    // private Instruction concat(DslConcat concat) {
    //     ConcatenateBuilder builder = new ConcatenateBuilder(null);

    //     for (DslJoinParam param : concat.getParams()) {
    //         mergeParamMap.get(param.getName()).accept(builder, param);
    //     }

    //     return builder.create();
    // }

    private Map<String, BiConsumer<RenameBuilder, DslRenameParam>> renameParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("mapping", (b, p) -> b.withMapping(
            p.getMapping().stream().collect(
                Collectors.toMap(DslRenameMapping::getFrom, DslRenameMapping::getTo)
            )))
    );

    private Instruction rename(DslRename rename) {
        RenameBuilder builder = new RenameBuilder(null);

        for (DslRenameParam param : rename.getParams()) {
            renameParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    private Map<String, BiConsumer<SortBuilder, DslSortParam>> sortParamMap = Map.ofEntries(
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget())),
        Map.entry("column", (b, p) -> b.withColumn(p.getColumn())),
        Map.entry("descending", (b, p) -> b.withDescending(p.isDescending()))
    );

    private Instruction sort(DslSort sort) {
        SortBuilder builder = new SortBuilder(null);

        for (DslSortParam param : sort.getParams()) {
            sortParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    private Map<String, BiConsumer<FunctionBuilder<?>, DslFunctionParam>> functionParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns())),
        Map.entry("excludeColumns", (b, p) -> b.withExcludeColumns(p.getExcludeColumns()))
    );

    private Instruction average(DslAverage average) {
        AverageBuilder builder = new AverageBuilder(null);

        for (DslFunctionParam param : average.getParams()) {
            functionParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    private Instruction sum(DslSum sum) {
        SumBuilder builder = new SumBuilder(null);

        for (DslFunctionParam param : sum.getParams()) {
            functionParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    private Map<String, BiConsumer<SaveBuilder, DslSaveParam>> saveParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("file", (b, p) -> b.withFile(p.getFile())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns()))
    );

    private Instruction save(DslSave save) {
        SaveBuilder builder = new SaveBuilder(null);

        for (DslSaveParam param : save.getParams()) {
            saveParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }
}
