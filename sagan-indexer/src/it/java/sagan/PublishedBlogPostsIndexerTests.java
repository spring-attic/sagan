package sagan;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.blog.support.PostRepository;
import sagan.blog.support.PublishedBlogPostsIndexer;
import sagan.search.SearchEntry;
import sagan.search.support.SearchService;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = { PublishedBlogPostsIndexerTests.TestConfig.class })
public class PublishedBlogPostsIndexerTests extends AbstractIndexerIntegrationTests {

    /*
     * @Configuration annotation intentionally omitted so as not to interfere with
     * @ComponentScan on the src/main side of this package.
     */
    public static class TestConfig {
        @Bean
        @Primary
        public SearchService stubSearchService() {
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
    private LocalDateTime publishedDate;

    @Before
    public void setUp() throws Exception {
        publishedDate = LocalDateTime.of(2012, Month.APRIL, 20, 0, 0);

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
        assertThat(blogPostIndexer.getId(post), equalTo(post.getId() + ""));
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
        assertThat(indexedEntry.getTitle(), equalTo("Spring is here"));
    }

    @Test
    public void indexedEntriesRawContentDoesNotIncludeMarkdownOrHtml() throws Exception {
        assertThat(indexedEntry.getRawContent(), equalTo("Welcome spring!"));
    }

    @Test
    public void indexedEntriesFacetPathsContainTheCategory() throws Exception {
        assertThat(indexedEntry.getFacetPaths(), containsInAnyOrder("Blog", "Blog/Releases"));
    }

    @Test
    public void indexedEntriesSummaryDoesNotIncludeMarkdownOrHtml() throws Exception {
        assertThat(indexedEntry.getSummary(), equalTo("Welcome"));
    }

    @Test
    public void indexedEntriesHaveASubtitle() throws Exception {
        assertThat(indexedEntry.getSubTitle(), equalTo("Blog Post"));
    }

    @Test
    public void indexedEntriesHaveThePostPath() throws Exception {
        assertThat(indexedEntry.getPath(), equalTo("/blog/" + post.getPublicSlug()));
    }

    @Test
    public void indexedEntriesHaveThePublishedAtDate() throws Exception {
        assertThat(indexedEntry.getPublishAt(), equalTo(publishedDate));
    }

}
