package sagan.guides.support;

import sagan.guides.Document;

import java.util.List;

interface DocRepository<T extends Document> {

    /**
     * Return the document with the given name.
     *
     * @param name the unique document identifier
     */
    T find(String name);

    /**
     * Return all documents of type {@code <T>}
     */
    List<T> findAll();

}
