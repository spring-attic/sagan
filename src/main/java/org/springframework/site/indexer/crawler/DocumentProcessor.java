package org.springframework.site.indexer.crawler;

import org.jsoup.nodes.Document;

public interface DocumentProcessor {
	void process(Document document);
}
