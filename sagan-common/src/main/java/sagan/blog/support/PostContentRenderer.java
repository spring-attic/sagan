package sagan.blog.support;

import sagan.blog.PostFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PostContentRenderer {

    private Map<PostFormat, MarkdownService> renderers = new HashMap<>();

    @Autowired
    public PostContentRenderer(@Qualifier("pegdown") MarkdownService markdownService,
                               @Qualifier("asciidoctor") MarkdownService asciidoctor) {
        renderers.put(PostFormat.ASCIIDOC, asciidoctor);
        renderers.put(PostFormat.MARKDOWN, markdownService);
    }

    public String render(String content, PostFormat format) {
        MarkdownService renderer = renderers.get(format);
        if (renderer == null) {
            throw new IllegalArgumentException("Unsupported post format: " + format);
        }
        String html = renderer.renderToHtml(content);
        if (format == PostFormat.MARKDOWN) {
            html = renderCallouts(decode(html));
        }
        return html;
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
            matcher.appendReplacement(sb,
                    String.format("<div class=\"callout\">\n" + "<div class=\"callout-title\">%s</div>\n" + "%s\n"
                            + "</div>", matcher.group(1), matcher.group(2)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
