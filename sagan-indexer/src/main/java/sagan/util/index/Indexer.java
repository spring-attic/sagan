package sagan.util.index;

public interface Indexer<T> {
    public Iterable<T> indexableItems();

    public void indexItem(T indexable);

    public String counterName();

    public String getId(T indexable);
}
