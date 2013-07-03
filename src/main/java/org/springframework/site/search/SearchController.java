package org.springframework.site.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.FilterBuilders.numericRangeFilter;
import static org.elasticsearch.index.query.FilterBuilders.orFilter;
import static org.elasticsearch.index.query.FilterBuilders.queryFilter;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/search")
public class SearchController {

	private final ElasticsearchOperations elasticsearchTemplate;

	@Autowired
	public SearchController(ElasticsearchOperations elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	@RequestMapping(method = {GET, HEAD})
	public String search(@RequestParam(value = "q", defaultValue = "") String query, @RequestParam(defaultValue = "1") int page, Model model) {

		Page<SearchEntry> entries;
		if (query.equals("")) {
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
			entries = elasticsearchTemplate.queryForPage(searchQuery, SearchEntry.class);
		} else {
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
					.withFilter(
							andFilter(
									numericRangeFilter("publishAt").lte(new Date().getTime()),
									orFilter(
											queryFilter(matchPhraseQuery("title", query)),
											queryFilter(matchPhraseQuery("rawContent", query))
									)
							)
					).build();
			searchQuery.setPageable(PageableFactory.forSearch(page));
			entries = elasticsearchTemplate.queryForPage(searchQuery, SearchEntry.class);
		}

		model.addAttribute("results", entries.getContent());
		model.addAttribute("query", query);
		model.addAttribute("paginationInfo", new PaginationInfo(entries));
		return "search/results";
	}

}
