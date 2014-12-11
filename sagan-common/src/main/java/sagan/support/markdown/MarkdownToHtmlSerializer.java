package sagan.support.markdown;

import org.pegdown.LinkRenderer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.HeaderNode;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * This custom Serializer customizes the way titles are written in HTML.
 * It generates anchor links automatically for every hX title like:
 * {@code <h2 class="title"><a href="#this-is-a-title" class="anchor" name="this-is-a-title"></a>This is a title</h2>}
 * @see sagan.blog.support.PegdownMarkdownService
 */
public class MarkdownToHtmlSerializer extends ToHtmlSerializer {

    public MarkdownToHtmlSerializer(final LinkRenderer linkRenderer, final Map<String, VerbatimSerializer> verbatimSerializers) {
        super(linkRenderer, verbatimSerializers);
    }

    @Override
    public void visit(HeaderNode node) {
        String tag = "h" + node.getLevel();
        String title = printChildrenToString(node);
        LinkRenderer.Rendering anchorLink = createAnchorLink(title);
        printer.print('<').print(tag).print('>');
        printLink(anchorLink);
        visitChildren(node);
        printer.print('<').print('/').print(tag).print('>');
    }

    private LinkRenderer.Rendering createAnchorLink(String title) {
        String cleanedTitle = title.toLowerCase().replace("\n", " ").replaceAll("[^a-z\\d\\s]", " ");
        String slug = StringUtils.arrayToDelimitedString(StringUtils.tokenizeToStringArray(cleanedTitle, " "), "-");
        return new LinkRenderer.Rendering("#"+slug, "").withAttribute("class","anchor").withAttribute("name", slug);
    }

}
