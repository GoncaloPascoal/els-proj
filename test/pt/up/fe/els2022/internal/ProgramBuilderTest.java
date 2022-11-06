package pt.up.fe.els2022.internal;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Program;
import pt.up.fe.specs.util.SpecsIo;

public class ProgramBuilderTest {
    @Test
    public void internalDsl() {
        ProgramBuilder builder = new ProgramBuilder();

        builder.loadStructured()
            .withTarget("t1")
            .withFilePaths(List.of("test/res/checkpoint2/data/vitis-report.xml"))
            .withPath("Resources")
            .withMetadataColumns(Map.of("Folder", MetadataType.DIRECTORY));

        builder.loadStructured()
            .withTarget("t2")
            .withFilePaths(List.of("test/res/checkpoint2/data/decision_tree.json"))
            .withPath("/");

        builder.loadUnstructured()
            .withTarget("t3")
            .withFilePaths(List.of("test/res/checkpoint2/data/gprof.txt"))
            .columnInterval()
                .withLines(List.of(new Interval(6)))
                .withColumnIntervals(Map.of(
                    "HighestPercentage", new Interval(1, 7),
                    "HighestName", new Interval(55))
                );

        builder.merge()
            .withTables(List.of("t1", "t2", "t3", "t4"));

        builder.save()
            .withSource("t1")
            .withPath("out/base.csv");

        Program program = builder.create();
        program.execute();

        assertEquals(
            SpecsIo.getResource("checkpoint2/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }
}
