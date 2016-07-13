package sagan.blog.support;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

public class AsciidoctorMarkdownService implements MarkdownService {

    Asciidoctor asciidoctor = Asciidoctor.Factory.create();

    @Override
    public String renderToHtml(String markdownSource) {
        Attributes attributes = new Attributes();
        attributes.setAllowUriRead(true);
        attributes.setSkipFrontMatter(true);
        attributes.setIcons("font");
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
