package sagan.blog.support;

import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.VerbatimSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * A {@link MarkdownService} based on the Pegdown library.
 */
@Service
@Qualifier("pegdown")
public class PegdownMarkdownService implements MarkdownService {

    private PegDownProcessor pegdown;

    public PegdownMarkdownService() {
        pegdown = new PegDownProcessor(Extensions.ALL);
    }

    @Override
    public String renderToHtml(String markdownSource) {
        // synchronizing on pegdown instance since neither the processor nor the underlying parser is thread-safe.
        synchronized (pegdown) {
            return pegdown.markdownToHtml(markdownSource, new LinkRenderer(),
                    Collections.singletonMap(VerbatimSerializer.DEFAULT, PrettifyVerbatimSerializer.INSTANCE));
        }
    }
}
