package integration.indexer;

import integration.IndexerIntegrationTestBase;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.domain.blog.PostRepository;
import io.spring.site.indexer.PublishedBlogPostsIndexer;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import java.util.Calendar;
import java.util.Date;

import static integration.indexer.PublishedBlogPostIndexerTests.TestConfiguration;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = { TestConfiguration.class })
public class PublishedBlogPostIndexerTests extends IndexerIntegrationTestBase{

    @Configuration
    public static class TestConfiguration {
        @Bean
        @Primary
        public SearchService stubSearchService(){
            return new SearchService(null, null) {
                @Override
                public void saveToIndex(SearchEntry entry) {
                    indexedEntry = entry;
                }
            };
        }
    }

    private static SearchEntry indexedEntry;

    @Autowired
    private PublishedBlogPostsIndexer blogPostIndexer;

    @Autowired
    private PostRepository postRepository;

    private Post post;
    private Date publishedDate;

    @Before
    public void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 3, 20);
        publishedDate = calendar.getTime();

        post = PostBuilder.post()
                .title("Spring is here")
                .rawContent("#Welcome spring!")
                .renderedContent("<h1>Welcome spring!</h1>")
                .renderedSummary("<h1>Welcome<h1>")
                .category(PostCategory.RELEASES)
                .publishAt(publishedDate)
                .author("jdoe", "John Doe").build();

        postRepository.save(post);

        blogPostIndexer.indexItem(post);
    }

    @Test
    public void usesAPostIdForAnIndexableItemId() throws Exception {
        assertThat(blogPostIndexer.getId(post), equalTo(post.getId()+""));
    }

    @Test
    public void hasACounterName() throws Exception {
        assertThat(blogPostIndexer.counterName(), equalTo("blog_posts"));
    }

    @Test
    public void usesPublishedPostsAsIndexableItems() throws Exception {
        assertThat(blogPostIndexer.indexableItems(), contains(post));
    }

    @Test
    public void indexedEntriesHaveATitle() throws Exception {
        assertThat(this.indexedEntry.getTitle(), equalTo("Spring is here"));
    }

    @Test
    public void indexedEntriesRawContentDoesNotIncludeMarkdownOrHtml() throws Exception {
        assertThat(this.indexedEntry.getRawContent(), equalTo("Welcome spring!"));
    }

    @Test
    public void indexedEntriesFacetPathsContainTheCategory() throws Exception {
        assertThat(this.indexedEntry.getFacetPaths(), containsInAnyOrder("Blog", "Blog/Releases"));
    }

    @Test
    public void indexedEntriesSummaryDoesNotIncludeMarkdownOrHtml() throws Exception {
        assertThat(this.indexedEntry.getSummary(), equalTo("Welcome"));
    }

    @Test
    public void indexedEntriesHaveASubtitle() throws Exception {
        assertThat(this.indexedEntry.getSubTitle(), equalTo("Blog Post"));
    }

    @Test
    public void indexedEntriesHaveThePostPath() throws Exception {
        assertThat(this.indexedEntry.getPath(), equalTo("/blog/" + post.getPublicSlug()));
    }

    @Test
    public void indexedEntriesHaveThePublishedAtDate() throws Exception {
        assertThat(this.indexedEntry.getPublishAt(), equalTo(publishedDate));
    }

}
