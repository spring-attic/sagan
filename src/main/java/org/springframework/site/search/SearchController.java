package org.springframework.site.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.site.blog.PaginationInfo;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.site.blog.web.PostViewFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/search")
public class SearchController {

	private ElasticsearchOperations elasticsearchTemplate;
	private final PostViewFactory postViewFactory;

	@Autowired
	public SearchController(ElasticsearchOperations elasticsearchTemplate, PostViewFactory postViewFactory) {
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.postViewFactory = postViewFactory;
	}

	@RequestMapping(method = {GET, HEAD})
	public String search(@RequestParam(value = "q", defaultValue = "") String query, @RequestParam(defaultValue = "1") int page, Model model) {

		Page<Post> posts;
		if (query.equals("")) {
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
			posts = elasticsearchTemplate.queryForPage(searchQuery, Post.class);
		} else {
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
					.withFilter(
							andFilter(
									termFilter("draft", Boolean.FALSE),
									numericRangeFilter("publishAt").lte(new Date().getTime()),
									orFilter(
											queryFilter(matchPhraseQuery("title", query)),
											queryFilter(matchPhraseQuery("rawContent", query))
									)
							)
					).build();
			searchQuery.setPageable(BlogPostsPageRequest.forSearch(page));
			posts = elasticsearchTemplate.queryForPage(searchQuery, Post.class);
		}

		model.addAttribute("results", postViewFactory.createPostViewList(posts.getContent()));
		model.addAttribute("query", query);
		model.addAttribute("paginationInfo", new PaginationInfo(posts));
		return "search/results";
	}

}
