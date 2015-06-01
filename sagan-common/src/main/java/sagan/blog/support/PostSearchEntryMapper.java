package sagan.blog.support;

import org.jsoup.Jsoup;
import sagan.blog.Post;
import sagan.search.SearchEntryMapper;
import sagan.search.types.BlogPost;

class PostSearchEntryMapper implements SearchEntryMapper<Post> {

    @Override
    public BlogPost map(Post post) {
        BlogPost entry = new BlogPost();
        entry.setTitle(post.getTitle());
        entry.setSubTitle("Blog Post");

        String summary = Jsoup.parse(post.getRenderedSummary()).text();
        String content = Jsoup.parse(post.getRenderedContent()).text();

        entry.setSummary(summary);
        entry.setRawContent(content);
        entry.addFacetPaths("Blog", "Blog/" + post.getCategory().getDisplayName());
        entry.setPath("/blog/" + post.getPublicSlug());
        entry.setPublishAt(post.getPublishAt());
        entry.setAuthor(post.getAuthor().getFullName());
        return entry;
    }

}
