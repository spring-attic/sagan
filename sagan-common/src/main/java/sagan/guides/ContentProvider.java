package sagan.guides;

public interface ContentProvider<T extends Document> {

    void populate(T document);

}
