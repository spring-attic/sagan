package org.springframework.search;

import io.searchbox.core.Search;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public class SearchQueryBuilder {

	private static final String matchAllQuery =
			"  \"query\": {\n" +
					"    \"bool\": {\n" +
					"      \"must\": [{\n" +
					"        \"match_all\": {}\n" +
					"      }]\n" +
					"    }\n" +
					"  }";

	private static final String rawQueryFilters =
			"\"filter\": {\n" +
					"  \"and\": {\n" +
					"    \"filters\": [{\n" +
					"      \"range\": {\n" +
					"        \"publishAt\": {\n" +
					"          \"from\": \"\",\n" +
					"          \"to\": \"%s\",\n" +
					"          \"include_lower\": true,\n" +
					"          \"include_upper\": true\n" +
					"        }\n" +
					"      }\n" +
					"    }, {\n" +
					"      \"or\": {\n" +
					"        \"filters\": [{\n" +
					"          \"query\": {\n" +
					"            \"match\": {\n" +
					"              \"title\": {\n" +
					"                \"query\": \"%s\",\n" +
					"                \"type\": \"phrase\"\n" +
					"              }\n" +
					"            }\n" +
					"          }\n" +
					"        }, {\n" +
					"          \"query\": {\n" +
					"            \"match\": {\n" +
					"              \"rawContent\": {\n" +
					"                \"query\": \"%s\",\n" +
					"                \"type\": \"phrase\"\n" +
					"              }\n" +
					"            }\n" +
					"          }\n" +
					"        }]\n" +
					"      }\n" +
					"    }]\n" +
					"  }\n" +
					"}\n";

	SearchQueryBuilder() {
	}

	Search forEmptyQuery(Pageable pageable) {
		return new Search("{" + matchAllQuery + "," + buildQueryPagination(pageable) + "}");
	}

	Search forQuery(String query, Pageable pageable) {
		return new Search("{" + matchAllQuery + "," + buildQueryFilters(new Date(), query) + "," + buildQueryPagination(pageable) + "}");
	}

	private String buildQueryFilters(Date toDate, String query) {
		String formattedDate = ISODateTimeFormat.dateTimeNoMillis().print(toDate.getTime());
		return String.format(rawQueryFilters, formattedDate, query, query);
	}

	private String buildQueryPagination(Pageable pageable) {
		return String.format("\"from\":%d,\"size\":%d", pageable.getOffset(), pageable.getPageSize());
	}
}