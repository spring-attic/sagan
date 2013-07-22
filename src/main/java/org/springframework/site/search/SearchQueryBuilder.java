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
			"  \"query\": {\n" +
					"    \"bool\": {\n" +
					"      \"should\": [\n" +
					"        { \"text\": { \"title\": \"%s\" }},\n" +
					"        { \"text\": { \"rawContent\": \"%s\" }}\n" +
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

	SearchQueryBuilder() {
	}

	Search.Builder forEmptyQuery(Pageable pageable) {
		return new Search.Builder("{" + emptyQuery + "," + buildQueryPagination(pageable) + "}");
	}

	Search.Builder forQuery(String queryTerm, Pageable pageable) {
		return new Search.Builder("{" + buildFullQuery(queryTerm) + "," + buildQueryFilters(new Date()) + "," + buildQueryPagination(pageable) + "}");
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