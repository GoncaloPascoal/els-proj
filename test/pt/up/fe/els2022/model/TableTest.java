package pt.up.fe.els2022.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.junit.Before;
import org.junit.Test;

import pt.up.fe.els2022.utils.CollectionUtils;

public final class TableTest {
    private Map<String, String> rowOne, rowTwo;
    private Set<String> rowOneColumnNames;
    private Map<String, List<String>> rows;

    @Before
    public void initialize() {
        List<String> names = Arrays.asList("c1", "c2", "c3");
        rowOneColumnNames = ListOrderedSet.listOrderedSet(names);

        rowOne = CollectionUtils.buildMap(names, Arrays.asList("a1", "a2", "a3"));
        rowTwo = CollectionUtils.buildMap(Arrays.asList("c2", "c3", "c4"), Arrays.asList("b2", "b3", "b4"));

        rows = CollectionUtils.buildMap(names, Arrays.asList(
            Arrays.asList("b1", "c1"),
            Arrays.asList("b2", "c2"),
            Arrays.asList("b3", "c3")
        ));
    }

    @Test
    public void addRowToEmptyTable() {
        Table t = new Table();
        t.addRow(rowOne);

        assertEquals(1, t.numRows());
        assertEquals(3, t.numColumns());
        assertEquals(rowOne, t.getRow(0));
        assertEquals(rowOneColumnNames, t.getColumnNames());
    }

    @Test
    public void addRowWithDifferentColumns() {
        Table t = new Table();

        t.addRow(rowOne);
        t.addRow(rowTwo);

        assertEquals(4, t.numColumns());
        assertArrayEquals(new String[]{"a1", "a2", "a3", null}, t.getRow(0).values().toArray());
        assertArrayEquals(new String[]{null, "b2", "b3", "b4"}, t.getRow(1).values().toArray());
    }

    @Test
    public void concatenateTables() {
        Table t1 = new Table();
        t1.addRow(rowOne);

        Table t2 = new Table();
        t2.addRows(rows);

        t1.concatenate(t2);

        assertEquals(3, t1.numRows());
        assertArrayEquals(new String[]{"a1", "b1", "c1"}, t1.getColumn("c1").toArray());
        assertArrayEquals(new String[]{"a2", "b2", "c2"}, t1.getColumn("c2").toArray());
        assertArrayEquals(new String[]{"a3", "b3", "c3"}, t1.getColumn("c3").toArray());
    }
}
