package sagan.guides.support;

import sagan.guides.DocumentContent;
import sagan.guides.DocumentMetadata;

import java.util.List;

interface DocRepository<T extends DocumentContent, M extends DocumentMetadata> {

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

    /**
     * Return metadata of type {@code <M>} for all documents of type {@code <T>}
     */
    List<M> findAllMetadata();

}
