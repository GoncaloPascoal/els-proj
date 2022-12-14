package pt.up.fe.els2022;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.up.fe.specs.util.SpecsIo;

public class Checkpoint1Test {
    @Test
    public void tabularBase() {
        Main.main(new String[]{"test/res/checkpoint1/base.tb"});
        assertEquals(
            SpecsIo.getResource("checkpoint1/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }

    @Test
    public void yamlBase() {
        Main.main(new String[]{"test/res/checkpoint1/base.yaml"});
        assertEquals(
            SpecsIo.getResource("checkpoint1/expected.csv"),
            SpecsIo.read("out/base.csv")
        );
    }
}
