package pt.up.fe.els2022;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.up.fe.specs.util.SpecsIo;

public class Checkpoint2Test {
    @Test
    public void baseUseCase() {
        Main.main(new String[]{"test/res/checkpoint2/base.yaml"});
        assertEquals(
            SpecsIo.getResource("checkpoint2/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }
}
