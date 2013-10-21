package sagan.blog;

import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchEntryMapper;

import org.jsoup.Jsoup;

public class PostSearchEntryMapper implements SearchEntryMapper<Post> {

    @Override
    public SearchEntry map(Post post) {
        SearchEntry entry = new SearchEntry();
        entry.setTitle(post.getTitle());
        entry.setSubTitle("Blog Post");

        String summary = Jsoup.parse(post.getRenderedSummary()).text();
        String content = Jsoup.parse(post.getRenderedContent()).text();

        entry.setSummary(summary);
        entry.setRawContent(content);
        entry.addFacetPaths("Blog", "Blog/" + post.getCategory().getDisplayName());
        entry.setPath("/blog/" + post.getPublicSlug());
        entry.setPublishAt(post.getPublishAt());
        return entry;
    }

}
