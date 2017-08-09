package sagan.blog.support;

import sagan.blog.PostFormat;

import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.Asciidoctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!original")
public class FormatAwarePostContentRenderer extends PostContentRenderer {

    private Map<PostFormat, MarkdownService> renderers = new HashMap<>();

    @Autowired
    public FormatAwarePostContentRenderer(@Qualifier("pegdown") MarkdownService markdownService,
                                          Asciidoctor asciidoctor) {
        super(markdownService);
        renderers.put(PostFormat.ASCIIDOC, new AsciidoctorMarkdownService(asciidoctor));
    }

    public String render(String content, PostFormat format) {
        MarkdownService renderer = renderers.get(format);
        if (renderer == null) {
            return super.render(content);
        }
        return renderer.renderToHtml(content);
    }

}
