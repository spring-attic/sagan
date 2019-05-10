package sagan.renderer.markup;

import org.springframework.http.MediaType;

/**
 * Convert lightweight markup format into HTML
 */
public interface MarkupRenderer {

	String renderToHtml(String markup);

	boolean canRender(MediaType mediaType);
}
