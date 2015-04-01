package sagan.blog.support;

import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.RootNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sagan.support.markdown.MarkdownToHtmlSerializer;
import sagan.support.markdown.PrettifyVerbatimSerializer;

import java.util.Collections;

/**
 * A {@link MarkdownService} based on the Pegdown library.
 */
@Service
@Qualifier("pegdown")
public class PegdownMarkdownService implements MarkdownService {

    private final PegDownProcessor pegdown;

    public PegdownMarkdownService() {
        pegdown = new PegDownProcessor(Extensions.ALL ^ Extensions.ANCHORLINKS);
    }

    @Override
    public String renderToHtml(String markdownSource) {
        // synchronizing on pegdown instance since neither the processor nor the underlying parser is thread-safe.
        synchronized (pegdown) {
            RootNode astRoot = pegdown.parseMarkdown(markdownSource.toCharArray());
            MarkdownToHtmlSerializer serializer = new MarkdownToHtmlSerializer(new LinkRenderer(),
                    Collections.singletonMap(VerbatimSerializer.DEFAULT, PrettifyVerbatimSerializer.INSTANCE));
            return serializer.toHtml(astRoot);
        }
    }
}
