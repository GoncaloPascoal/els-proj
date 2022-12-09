package pt.up.fe.els2022.internal;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.model.JoinType;
import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Program;
import pt.up.fe.specs.util.SpecsIo;

public class ProgramBuilderTest {
    @Test
    public void internalDsl() {
        ProgramBuilder builder = new ProgramBuilder();

        builder
            .loadStructured()
                .withTarget("t1")
                .withFiles("test/res/checkpoint2/data/vitis-report.xml")
                .withPaths("//Resources")
                .withMetadataColumns(Map.of("Folder", MetadataType.DIRECTORY_PATH))
                .close()
            .loadStructured()
                .withTarget("t2")
                .withFiles("test/res/checkpoint2/data/decision_tree.json")
                .withPaths("/", "/params")
                .close()
            .loadUnstructured()
                .withTarget("t3")
                .withFiles("test/res/checkpoint2/data/gprof.txt")
                .columnInterval()
                    .withLines(new Interval(6))
                    .addColumnInterval("HighestPercentage", new Interval(1, 7))
                    .addColumnInterval("HighestName", new Interval(55))
                    .close()
                .close()
            .join()
                .withTables("t1", "t2", "t3")
                .withType(JoinType.MERGE)
                .close()
            .save()
                .withSource("t1")
                .withFile("out/base.csv");

        Program program = builder.create();
        program.execute();

        assertEquals(
            SpecsIo.getResource("checkpoint2/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }
}
