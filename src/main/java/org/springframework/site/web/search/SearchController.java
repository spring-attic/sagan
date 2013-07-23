package org.springframework.site.web.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.search.SearchResult;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/search")
public class SearchController {

	private final SearchService searchService;

	@Autowired
	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@RequestMapping(method = {GET, HEAD})
	public String search(@RequestParam(value = "q", defaultValue = "") String query, @RequestParam(defaultValue = "1") int page, Model model) {
		Pageable pageable = PageableFactory.forSearch(page);
		Page<SearchResult> entries = searchService.search(query, pageable);
		model.addAttribute("results", entries.getContent());
		model.addAttribute("query", query);
		model.addAttribute("paginationInfo", new PaginationInfo(entries));
		return "search/results";
	}

}
