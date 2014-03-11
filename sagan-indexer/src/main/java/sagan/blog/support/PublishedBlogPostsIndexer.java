package sagan.blog.support;

import sagan.blog.Post;
import sagan.search.SearchEntry;
import sagan.search.service.SearchService;
import sagan.Indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishedBlogPostsIndexer implements Indexer<Post> {

    private final SearchService searchService;
    private final BlogService blogService;
    private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

    @Autowired
    public PublishedBlogPostsIndexer(SearchService searchService, BlogService blogService) {
        this.searchService = searchService;
        this.blogService = blogService;
    }

    @Override
    public Iterable<Post> indexableItems() {
        return blogService.getAllPublishedPosts();
    }

    @Override
    public void indexItem(Post indexable) {
        SearchEntry searchEntry = mapper.map(indexable);
        searchService.saveToIndex(searchEntry);
    }

    @Override
    public String counterName() {
        return "blog_posts";
    }

    @Override
    public String getId(Post indexable) {
        return String.valueOf(indexable.getId());
    }
}
