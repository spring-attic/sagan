package sagan.blog.support;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("asciidoctor")
public class AsciidoctorMarkdownService implements MarkdownService {

    private final Asciidoctor asciidoctor;

    public AsciidoctorMarkdownService(Asciidoctor asciidoctor) {
        this.asciidoctor = asciidoctor;
    }

    @Override
    public String renderToHtml(String markdownSource) {
        Attributes attributes = new Attributes();
        attributes.setAllowUriRead(true);
        attributes.setSkipFrontMatter(true);
        attributes.setAttribute("source-highlighter", "prettify");
        attributes.setAttribute("idprefix", "");
        attributes.setAttribute("idseparator", "-");
        attributes.setAnchors(true);
        OptionsBuilder options = OptionsBuilder.options()
                .safe(SafeMode.SAFE)
                .attributes(attributes);
        return asciidoctor.convert(markdownSource, options);
    }

}
