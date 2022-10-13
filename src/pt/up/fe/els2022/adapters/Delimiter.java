package pt.up.fe.els2022.adapters;

public class Delimiter {
    private final int start, end;
    
    public Delimiter(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
