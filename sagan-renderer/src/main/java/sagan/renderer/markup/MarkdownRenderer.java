package sagan.renderer.markup;

import java.util.Collections;

import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.RootNode;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Convert markdown into HTML using the Pegdown library
 */
@Component
class MarkdownRenderer implements MarkupRenderer {

	private final PegDownProcessor pegdown;

	public MarkdownRenderer() {
		this.pegdown = new PegDownProcessor(Extensions.ALL ^ Extensions.ANCHORLINKS);
	}

	@Override
	public boolean canRender(MediaType mediaType) {
		return MediaType.TEXT_MARKDOWN.isCompatibleWith(mediaType);
	}

	@Override
	public String renderToHtml(String markup) {
		// synchronizing on pegdown instance since neither the processor nor the
		// underlying parser is thread-safe.
		synchronized (this.pegdown) {
			RootNode astRoot = this.pegdown.parseMarkdown(markup.toCharArray());
			MarkdownToHtmlSerializer serializer = new MarkdownToHtmlSerializer(
					new LinkRenderer(),
					Collections.singletonMap(VerbatimSerializer.DEFAULT,
							PrettifyVerbatimSerializer.INSTANCE));
			return serializer.toHtml(astRoot);
		}
	}
}
