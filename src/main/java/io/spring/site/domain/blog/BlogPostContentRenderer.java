package io.spring.site.domain.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spring.site.domain.services.MarkdownService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BlogPostContentRenderer {

    private MarkdownService markdownService;

    @Autowired
    public BlogPostContentRenderer(MarkdownService markdownService) {
        this.markdownService = markdownService;
    }

    public String render(String content) {
        String html = markdownService.renderToHtml(content);
        return renderCallouts(decode(html));
    }

    private String decode(String html) {
        Matcher matcher = Pattern.compile("<pre>!(.*)</pre>").matcher(html);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).replace('{', '<').replace('}', '>'));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String renderCallouts(String html) {
        Pattern calloutPattern = Pattern.compile("\\[callout title=([^\\]]+)\\]([^\\[]*)\\[\\/callout\\]");
        Matcher matcher = calloutPattern.matcher(html);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, String.format("<div class=\"callout\">\n" +
                    "<div class=\"callout-title\">%s</div>\n" +
                    "%s\n" +
                    "</div>", matcher.group(1), matcher.group(2)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
