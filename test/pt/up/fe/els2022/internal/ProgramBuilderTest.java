package pt.up.fe.els2022.internal;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import pt.up.fe.els2022.model.Program;
import pt.up.fe.specs.util.SpecsIo;

public class ProgramBuilderTest {
    @Test
    public void internalDsl() {
        ProgramBuilder builder = new ProgramBuilder();

        builder.loadStructured()
            .withColumns(List.of("LUT", "FF", "DSP48E", "BRAM_18K"))
            .withPath("Resources")
            .withFilePaths(List.of("test/res/checkpoint1/data/vitis-report_*.xml"))
            .withTarget("t");

        builder.rename()
            .withSource("t")
            .withMapping(Map.of("BRAM_18K", "BRAMs", "DSP48E", "DSPs", "LUT", "LUTs", "FF", "FFs"));

        builder.save()
            .withSource("t")
            .withPath("out/base.csv");

        Program program = builder.create();
        program.execute();

        assertEquals(
            SpecsIo.getResource("checkpoint1/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }
}
