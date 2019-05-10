package sagan.site.blog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sagan.blog.PostFormat;
import sagan.site.renderer.SaganRendererClient;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PostContentRenderer {

	private final SaganRendererClient client;

	public PostContentRenderer(SaganRendererClient client) {
		this.client = client;
	}

	public String render(String content, PostFormat format) {
		if(StringUtils.isEmpty(content)) {
			return "";
		}
		switch (format) {
			case MARKDOWN:
				String renderedMarkdown = this.client.renderMarkdown(content);
				return renderCallouts(decode(renderedMarkdown));

			case ASCIIDOC:
				return this.client.renderAsciidoc(content);

			default:
				throw new IllegalArgumentException("Unsupported post format: " + format);
		}
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
