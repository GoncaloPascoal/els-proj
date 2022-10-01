package pt.up.fe.els2022;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.up.fe.specs.util.SpecsIo;

public class Checkpoint1Test {
    @Test
    public void baseUseCase() {
        Main.main(new String[]{"test/res/checkpoint1/base.yaml"});
        assertEquals(
            SpecsIo.getResource("checkpoint1/expected.csv"),
            SpecsIo.getResource("checkpoint1/out/base.csv")
        );
    }
}