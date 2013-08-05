package org.springframework.site.web.search;

import org.springframework.site.search.SearchEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchEntryBuilder {

	private String title = "A random title";
	private String rawContent = "A random post content";
	private String summary = "A random post summary";
	private Date publishAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	private String path = "/some/path";
	private List<String> facetPaths = new ArrayList<>();
	private boolean current = true;
	private String type;

	private SearchEntryBuilder() {
	}

	public static SearchEntryBuilder entry() {
		return new SearchEntryBuilder();
	}

	public SearchEntryBuilder title(String title) {
		this.title = title;
		return this;
	}

	public SearchEntryBuilder rawContent(String rawContent) {
		this.rawContent = rawContent;
		return this;
	}

	public SearchEntryBuilder summary(String summary) {
		this.summary = summary;
		return this;
	}

	public SearchEntryBuilder publishAt(Date date) {
		this.publishAt = date;
		return this;
	}

	public SearchEntryBuilder publishAt(String dateString) throws ParseException {
		this.publishAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
		return this;
	}

	public SearchEntryBuilder path(String path) {
		this.path = path;
		return this;
	}

	public SearchEntryBuilder facetPath(String facetPath) {
		this.facetPaths.add(facetPath);
		return this;
	}

	public SearchEntryBuilder notCurrent() {
		this.current = false;
		return this;
	}

	public SearchEntry build() {
		SearchEntry searchResult = new SearchEntry();
		searchResult.setTitle(title);
		searchResult.setRawContent(rawContent);
		searchResult.setSummary(summary);
		searchResult.setPublishAt(publishAt);
		searchResult.setPath(path);
		searchResult.setCurrent(current);
		searchResult.setFacetPaths(facetPaths);
		if (type != null) {
			searchResult.setType(type);
		}
		return searchResult;
	}

	public SearchEntryBuilder type(String type) {
		this.type = type;
		return this;
	}
}
