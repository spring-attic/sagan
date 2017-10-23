package sagan.guides;

/**
 * Document content, loaded from distant datastore and parsed to generate
 * the content and sidebar outputs.
 * This operation can be considered as "expensive" (CPU + latency); consider retrieving
 * only {@link sagan.guides.DocumentMetadata} if possible.
 */
public interface DocumentContent {

    String getContent();

    String getTableOfContents();

}
