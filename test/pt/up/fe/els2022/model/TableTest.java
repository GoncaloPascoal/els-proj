package pt.up.fe.els2022.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public final class TableTest {
    @Test
    public void addRowToEmptyTable() {
        Table t = new Table();

        Map<String, String> row = Map.of("c1", "v1", "c2", "v2", "c3", "v3");
        t.addRow(row);

        assertEquals(1, t.numRows());
        assertEquals(3, t.numColumns());
        assertEquals(row, t.getRow(0));
        assertEquals(Set.of("c1", "c2", "c3"), t.getColumnNames());
    }

    @Test
    public void addRowWithDifferentColumns() {
        Table t = new Table();

        t.addRow(Map.of("c1", "a1", "c2", "a2"));
        t.addRow(Map.of("c2", "b2", "c3", "b3"));

        assertEquals(3, t.numColumns());
        assertArrayEquals(new String[]{"a1", "a2", null}, t.getRow(0).values().toArray());
        assertArrayEquals(new String[]{null, "b2", "b3"}, t.getRow(1).values().toArray());
    }

    @Test
    public void concatenateTables() {
        Table t1 = new Table();
        t1.addRow(Map.of("c1", "1", "c2", "2"));

        Table t2 = new Table();
        t2.addRows(Map.of("c1", List.of("3", "4"), "c2", List.of("5", "6")));

        t1.concatenate(t2);

        assertEquals(3, t1.numRows());
        assertArrayEquals(new String[]{"1", "3", "4"}, t1.getColumn("c1").toArray());
        assertArrayEquals(new String[]{"2", "5", "6"}, t1.getColumn("c2").toArray());
    }
}
