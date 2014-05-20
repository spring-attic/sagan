package sagan.blog.support;

import sagan.blog.Post;
import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;
import sagan.support.DateConverter;

import org.jsoup.Jsoup;

class PostSearchEntryMapper implements SearchEntryMapper<Post> {

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
        entry.setPublishAt(DateConverter.toDate(post.getPublishAt()));
        return entry;
    }

}
