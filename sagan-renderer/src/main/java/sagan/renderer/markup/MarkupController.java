package sagan.renderer.markup;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Render lightweight markup into HTML
 */
@Controller
public class MarkupController {

	private final List<MarkupRenderer> converters;


	public MarkupController(List<MarkupRenderer> converters) {
		this.converters = converters;
	}

	@PostMapping(path = "/documents", produces = "text/html")
	public ResponseEntity<String> renderMarkup(@RequestHeader("Content-Type") MediaType contentType,
			@RequestBody String markup) {
		return converters.stream()
				.filter(converter -> converter.canRender(contentType))
				.findFirst()
				.map(converter -> ResponseEntity.ok(converter.renderToHtml(markup)
						+ "\n<!-- rendered by Sagan Renderer Service -->"))
				.orElse(ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build());
	}

}