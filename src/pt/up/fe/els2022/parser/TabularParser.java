package pt.up.fe.els2022.parser;

import java.io.File;
import java.io.StringReader;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;

import com.google.inject.Inject;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.internal.*;
import pt.up.fe.els2022.internal.text.ColumnIntervalBuilder;
import pt.up.fe.els2022.internal.text.RegexLineDelimiterBuilder;
import pt.up.fe.els2022.model.JoinType;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Program;
import pt.up.fe.els2022.tabular.TabularStandaloneSetup;
import pt.up.fe.els2022.tabular.tabular.*;
import pt.up.fe.specs.util.SpecsIo;
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
        ProgramBuilder builder = new ProgramBuilder();
        BiConsumerClassMap<DslInstruction, ProgramBuilder> instructionParseMap = new BiConsumerClassMap<>();

        instructionParseMap.put(DslLoadStructured.class, this::loadStructured);
        instructionParseMap.put(DslLoadUnstructured.class, this::loadUnstructured);
        instructionParseMap.put(DslJoin.class, this::join);
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

        var ast = (DslModel) result.getRootASTElement();

        for (DslInstruction dslInstruction : ast.getInstructions()) {
            instructionParseMap.accept(dslInstruction, builder);
        }

        return builder.create();
    }

    private static Interval parseInterval(DslInterval dslInterval) {
        OptionalInt end = dslInterval.getEnd();
        return new Interval(dslInterval.getStart(), end == null ? null : end.getVal());
    }

    private static final Map<String, BiConsumer<LoadBuilder<?>, DslLoadParam>> loadParamMap = Map.ofEntries(
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget())),
        Map.entry("files", (b, p) -> b.withFiles(p.getFiles())),
        Map.entry("metadataColumns", (b, p) -> p.getMetadataColumns().forEach(
            e -> b.addMetadataColumn(e.getColumn(), MetadataType.fromId(e.getType()))
        )),
        Map.entry("columnSuffix", (b, p) -> b.withColumnSuffix(p.getColumnSuffix()))
    );

    private static final Map<String, BiConsumer<LoadStructuredBuilder, DslLoadStructuredParam>> loadStructuredParamMap = Map.ofEntries(
        Map.entry("paths", (b, p) -> b.withPaths(p.getPaths())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns()))
    );

    private void loadStructured(DslLoadStructured loadStructured, ProgramBuilder parent) {
        LoadStructuredBuilder builder = parent.loadStructured();

        BiConsumerClassMap<EObject, LoadStructuredBuilder> paramMap = new BiConsumerClassMap<>();
        paramMap.put(DslLoadParam.class, (p, b) -> loadParamMap.get(p.getName()).accept(b, p));
        paramMap.put(DslLoadStructuredParam.class, (p, b) -> loadStructuredParamMap.get(p.getName()).accept(b, p));

        for (EObject param : loadStructured.getParams()) {
            paramMap.accept(param, builder);
        }
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
        Map.entry("columnIntervals", (b, p) -> p.getColumnIntervals().forEach(
            e -> b.addColumnInterval(e.getColumn(), parseInterval(e.getInterval()))
        )),
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

    private void loadUnstructured(DslLoadUnstructured loadUnstructured, ProgramBuilder parent) {
        LoadUnstructuredBuilder builder = parent.loadUnstructured();

        BiConsumerClassMap<EObject, LoadUnstructuredBuilder> paramMap = new BiConsumerClassMap<>();
        paramMap.put(DslLoadParam.class, (p, b) -> loadParamMap.get(p.getName()).accept(b, p));
        paramMap.put(DslLoadUnstructuredParam.class, (p, b) -> loadUnstructuredParamMap.get(p.getName()).accept(b, p));

        for (EObject param : loadUnstructured.getParams()) {
            paramMap.accept(param, builder);
        }
    }

    private static final Map<String, BiConsumer<JoinBuilder, DslJoinParam>> joinParamMap = Map.ofEntries(
        Map.entry("tables", (b, p) -> b.withTables(p.getTables())),
        Map.entry("type", (b, p) -> b.withType(JoinType.fromId(p.getType()))),
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget()))
    );

    private void join(DslJoin join, ProgramBuilder parent) {
        JoinBuilder builder = parent.join();

        for (DslJoinParam param : join.getParams()) {
            joinParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private Map<String, BiConsumer<RenameBuilder, DslRenameParam>> renameParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("mapping", (b, p) -> b.withMapping(
            p.getMapping().stream().collect(
                Collectors.toMap(DslRenameMapping::getFrom, DslRenameMapping::getTo)
            )))
    );

    private void rename(DslRename rename, ProgramBuilder parent) {
        RenameBuilder builder = parent.rename();

        for (DslRenameParam param : rename.getParams()) {
            renameParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private Map<String, BiConsumer<SortBuilder, DslSortParam>> sortParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("column", (b, p) -> b.withColumn(p.getColumn())),
        Map.entry("descending", (b, p) -> b.withDescending(p.isDescending()))
    );

    private void sort(DslSort sort, ProgramBuilder parent) {
        SortBuilder builder = parent.sort();

        for (DslSortParam param : sort.getParams()) {
            sortParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private Map<String, BiConsumer<FunctionBuilder<?>, DslFunctionParam>> functionParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns())),
        Map.entry("excludeColumns", (b, p) -> b.withExcludeColumns(p.getExcludeColumns()))
    );

    private void average(DslAverage average, ProgramBuilder parent) {
        AverageBuilder builder = parent.average();

        for (DslFunctionParam param : average.getParams()) {
            functionParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private void sum(DslSum sum, ProgramBuilder parent) {
        SumBuilder builder = parent.sum();

        for (DslFunctionParam param : sum.getParams()) {
            functionParamMap.get(param.getName()).accept(builder, param);
        }
    }

    private Map<String, BiConsumer<SaveBuilder, DslSaveParam>> saveParamMap = Map.ofEntries(
        Map.entry("source", (b, p) -> b.withSource(p.getSource())),
        Map.entry("file", (b, p) -> b.withFile(p.getFile())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns()))
    );

    private void save(DslSave save, ProgramBuilder parent) {
        SaveBuilder builder = parent.save();

        for (DslSaveParam param : save.getParams()) {
            saveParamMap.get(param.getName()).accept(builder, param);
        }
    }
}
