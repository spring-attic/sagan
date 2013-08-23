package org.springframework.site.web.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchFacet;
import org.springframework.site.search.SearchResult;
import org.springframework.site.search.SearchResults;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/search")
public class SearchController {

	private final SearchService searchService;

	@Autowired
	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@RequestMapping(method = {GET, HEAD, POST})
	public String search(SearchForm searchForm, @RequestParam(defaultValue = "1") int page, Model model) {
		Pageable pageable = PageableFactory.forSearch(page);
		SearchResults searchResults = searchService.search(searchForm.getQ(), pageable, searchForm.getFilters());
		Page<SearchResult> entries = searchResults.getPage();
		model.addAttribute("results", entries.getContent());
		model.addAttribute("paginationInfo", new PaginationInfo(entries));
		SearchFacet root = new SpringFacetsBuilder(searchResults.getFacets()).build();
		model.addAttribute("rootFacet", root);
		model.addAttribute("searchForm", searchForm);
		return "search/results";
	}
}
