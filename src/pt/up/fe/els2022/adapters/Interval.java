package pt.up.fe.els2022.adapters;

public class Interval {
    private final int start;
    private final Integer end;

    public Interval(int start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public Interval(int start) {
        this(start, null);
    }

    public int getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }
}
