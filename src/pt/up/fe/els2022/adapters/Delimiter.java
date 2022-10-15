package pt.up.fe.els2022.adapters;

public class Delimiter {
    private final int start;
    private final Integer end;
    
    public Delimiter(int start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }
}
