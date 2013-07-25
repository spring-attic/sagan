package org.springframework.site.search;

import io.searchbox.core.Search;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public class SearchQueryBuilder {


	private static final String emptyQuery =
			"  \"query\": {\n" +
					"    \"bool\": {\n" +
					"      \"should\": [{\n" +
					"        \"match_all\": {}\n" +
					"      }]\n" +
					"    }\n" +
					"  }";

	private static final String fullQuery =
			" \"query\": {\n" +
					"    \"multi_match\": {\n" +
					"      \"query\": \"%s\",\n" +
					"      \"fields\": [\n" +
					"        \"title^10\",\n" +
					"        \"rawContent\"\n" +
					"      ]\n" +
					"    }\n" +
					"  }";

	private static final String rawQueryFilters =
			"\"filter\": {\n" +
					"      \"range\": {\n" +
					"        \"publishAt\": {\n" +
					"          \"from\": \"\",\n" +
					"          \"to\": \"%s\",\n" +
					"          \"include_lower\": true,\n" +
					"          \"include_upper\": true\n" +
					"        }\n" +
					"      }\n" +
					"    }\n";

	private static final String highlight =
			"\"highlight\": {\n" +
					"    \"order\": \"score\",\n" +
					"    \"require_field_match\": false,\n" +
					"    \"fields\": {\n" +
					"      \"rawContent\": {\n" +
					"        \"fragment_size\": 300,\n" +
					"        \"number_of_fragments\": 1\n" +
					"      }\n" +
					"    }\n" +
					"  }";


	SearchQueryBuilder() {
	}

	Search.Builder forEmptyQuery(Pageable pageable) {
		return new Search.Builder("{" + emptyQuery + ","
				+ buildQueryPagination(pageable) + ","
				+ highlight
				+ "}");
	}

	Search.Builder forQuery(String queryTerm, Pageable pageable) {
		return new Search.Builder("{" + buildFullQuery(queryTerm) + ","
				+ buildQueryFilters(new Date()) + ","
				+ buildQueryPagination(pageable) + ","
				+ highlight
				+ "}");
	}

	private String buildFullQuery(String queryTerm) {
		return String.format(fullQuery, queryTerm, queryTerm);
	}

	private String buildQueryFilters(Date toDate) {
		String formattedDate = ISODateTimeFormat.dateTimeNoMillis().print(toDate.getTime());
		return String.format(rawQueryFilters, formattedDate);
	}

	private String buildQueryPagination(Pageable pageable) {
		return String.format("\"from\":%d,\"size\":%d", pageable.getOffset(), pageable.getPageSize());
	}
}