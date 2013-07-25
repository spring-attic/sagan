package org.springframework.site.domain.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.services.MarkdownService;
import org.springframework.stereotype.Service;

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
		return renderCallouts(html);
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
