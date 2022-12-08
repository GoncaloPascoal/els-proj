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

import pt.up.fe.els2022.instructions.Instruction;
import pt.up.fe.els2022.internal.*;
import pt.up.fe.els2022.model.MetadataType;
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

    public void parse(File file) {
        parse(SpecsIo.read(file));
    }

    public void parse(String code) {
        IParseResult result = parser.parse(new StringReader(code));
        FunctionClassMap<DslInstruction, Instruction> instructionParseMap = new FunctionClassMap<>();

        instructionParseMap.put(DslLoadStructured.class, this::loadStructured); // X
        instructionParseMap.put(DslLoadUnstructured.class, this::loadUnstructured);
        instructionParseMap.put(DslMerge.class, this::merge); // X
        instructionParseMap.put(DslRename.class, this::rename); // X
        instructionParseMap.put(DslSort.class, this::sort); // X
        instructionParseMap.put(DslAverage.class, this::average); // X
        instructionParseMap.put(DslSum.class, this::sum); // X
        instructionParseMap.put(DslSave.class, this::save); // X

        if (result.hasSyntaxErrors()) {
            System.err.println("Provided script has syntax errors:");

            for (var error : result.getSyntaxErrors()) {
                System.err.println("- Line " + error.getStartLine() + ": " +
                    error.getSyntaxErrorMessage().getMessage());
            }
            return;
        }

        var model = (DslModel) result.getRootASTElement();

        for (DslInstruction dslInstruction : model.getInstructions()) {
            Instruction instruction = instructionParseMap.apply(dslInstruction);
            System.out.println(instruction);
        }
    }

    private static Map<String, BiConsumer<LoadBuilder<?>, DslLoadParam>> loadParamMap = Map.ofEntries(
        Map.entry("target", (b, p) -> b.withTarget(p.getTarget())),
        Map.entry("files", (b, p) -> b.withFiles(p.getFiles())),
        Map.entry("metadataColumns", (b, p) -> b.withMetadataColumns(
            p.getMetadataColumns().stream().collect(
                Collectors.toMap(DslLoadMapping::getColumn, e -> MetadataType.fromId(e.getType()))
            )
        )),
        Map.entry("columnSuffix", (b, p) -> b.withColumnSuffix(p.getColumnSuffix()))
    );

    private static Map<String, BiConsumer<LoadStructuredBuilder, DslLoadStructuredParam>> loadStructuredParamMap = Map.ofEntries(
        Map.entry("paths", (b, p) -> b.withPaths(p.getPaths())),
        Map.entry("columns", (b, p) -> b.withColumns(p.getColumns()))
    );

    private Instruction loadStructured(DslInstruction dslInstruction) {
        DslLoadStructured loadStructured = (DslLoadStructured) dslInstruction;
        LoadStructuredBuilder builder = new LoadStructuredBuilder(null);

        BiConsumerClassMap<EObject, LoadStructuredBuilder> paramMap = new BiConsumerClassMap<>();
        paramMap.put(DslLoadParam.class, (p, b) -> loadParamMap.get(p.getName()).accept(b, p));
        paramMap.put(DslLoadStructuredParam.class, (p, b) -> loadStructuredParamMap.get(p.getName()).accept(b, p));

        for (EObject param : loadStructured.getParams()) {
            paramMap.accept(param, builder);
        }

        return builder.create();
    }

    private static Map<String, BiConsumer<LoadUnstructuredBuilder, DslLoadUnstructuredParam>> loadUnstructuredParamMap = Map.ofEntries(
        Map.entry("textInstructions", (b, p) -> b.close()) // TODO
    );

    private Instruction loadUnstructured(DslInstruction dslInstruction) {
        DslLoadUnstructured loadUnstructured = (DslLoadUnstructured) dslInstruction;
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

    private Instruction merge(DslInstruction dslInstruction) {
        DslMerge merge = (DslMerge) dslInstruction;
        MergeBuilder builder = new MergeBuilder(null);

        for (DslMergeParam param : merge.getParams()) {
            mergeParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    // private Instruction concat(DslInstruction dslInstruction) {
    //     DslConcat concat = (DslConcat) dslInstruction;
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

    private Instruction rename(DslInstruction dslInstruction) {
        DslRename rename = (DslRename) dslInstruction;
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

    private Instruction sort(DslInstruction dslInstruction) {
        DslSort sort = (DslSort) dslInstruction;
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

    private Instruction average(DslInstruction dslInstruction) {
        DslAverage average = (DslAverage) dslInstruction;
        AverageBuilder builder = new AverageBuilder(null);

        for (DslFunctionParam param : average.getParams()) {
            functionParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }

    private Instruction sum(DslInstruction dslInstruction) {
        DslSum sum = (DslSum) dslInstruction;
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

    private Instruction save(DslInstruction dslInstruction) {
        DslSave save = (DslSave) dslInstruction;
        SaveBuilder builder = new SaveBuilder(null);

        for (DslSaveParam param : save.getParams()) {
            saveParamMap.get(param.getName()).accept(builder, param);
        }

        return builder.create();
    }
}
