package org.springframework.site.search;

public interface CrawlerService {
	void crawl(String url, int levels);
}
