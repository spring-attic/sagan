package sagan.guides;

public interface ContentProvider<T extends DocumentContent> {

    T populate(T document);

}
