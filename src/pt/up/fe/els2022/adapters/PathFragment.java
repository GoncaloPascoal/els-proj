package pt.up.fe.els2022.adapters;

public class PathFragment {
    private final String key;
    private final boolean directChild;

    public PathFragment(String key, boolean directChild) {
        this.key = key;
        this.directChild = directChild;
    }

    public String getKey() {
        return key;
    }

    public boolean isDirectChild() {
        return directChild;
    }
}
