package org.springframework.site.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.site.blog.PaginationInfo;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/search")
public class SearchController {

	private ElasticsearchOperations elasticsearchTemplate;

	@Autowired
	public SearchController(ElasticsearchOperations elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	@RequestMapping(method = {GET, HEAD})
	public String search(@RequestParam(value = "q", defaultValue = "") String query, @RequestParam(defaultValue = "1") int page, Model model) {

		Page<Post> posts;
		if (query.equals("")) {
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
			posts = elasticsearchTemplate.queryForPage(searchQuery, Post.class);
		} else {
			Criteria criteria = new Criteria().or("rawContent");
			for (String token : query.split("\\s+")) {
				criteria.contains(token);
			}
			criteria = criteria.or("title");
			for (String token : query.split("\\s+")) {
				criteria.contains(token);
			}
			CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
			criteriaQuery.setPageable(BlogPostsPageRequest.forSearch(page));
			posts = elasticsearchTemplate.queryForPage(criteriaQuery, Post.class);
		}

		model.addAttribute("results", posts.getContent());
		model.addAttribute("query", query);
		model.addAttribute("paginationInfo", new PaginationInfo(posts));
		return "search/results";
	}

}
