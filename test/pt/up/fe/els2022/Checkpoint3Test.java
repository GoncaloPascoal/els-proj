package pt.up.fe.els2022;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.up.fe.specs.util.SpecsIo;

public class Checkpoint3Test {
    @Test
    public void baseUseCase() {
        Main.main(new String[]{"test/res/checkpoint3/base.yaml"});
        assertEquals(
            SpecsIo.getResource("checkpoint3/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }
}
