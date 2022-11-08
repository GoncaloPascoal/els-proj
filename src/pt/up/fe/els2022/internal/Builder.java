package pt.up.fe.els2022.internal;

public abstract class Builder<T> {
    protected abstract void validate();

    protected abstract T createUnsafe();

    public T create() {
        validate();
        return createUnsafe();
    }
}
