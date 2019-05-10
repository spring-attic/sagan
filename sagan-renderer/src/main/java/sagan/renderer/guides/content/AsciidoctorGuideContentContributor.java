package sagan.renderer.guides.content;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import sagan.renderer.guides.GuideContentResource;
import sagan.renderer.guides.GuideRenderingException;

import org.springframework.stereotype.Component;

/**
 * Render the README.adoc file with Asciidoctor and contribute the
 * guide content and its table of contents.
 */
@Component
public class AsciidoctorGuideContentContributor implements GuideContentContributor {

	private static final String README_FILENAME = "README.adoc";

	private final Asciidoctor asciidoctor;

	public AsciidoctorGuideContentContributor(Asciidoctor asciidoctor) {
		this.asciidoctor = asciidoctor;
	}

	@Override
	public void contribute(GuideContentResource guideContent, File repositoryRoot) {
		try {
			Attributes attributes = new Attributes();
			attributes.setAllowUriRead(true);
			attributes.setSkipFrontMatter(true);
			File readmeAdocFile = new File(
					repositoryRoot.getAbsolutePath() + File.separator + README_FILENAME);
			OptionsBuilder options = OptionsBuilder.options().safe(SafeMode.SAFE)
					.baseDir(repositoryRoot).headerFooter(true).attributes(attributes);
			StringWriter writer = new StringWriter();
			this.asciidoctor.convert(new FileReader(readmeAdocFile), writer, options);
			Document doc = Jsoup.parse(writer.toString());
			guideContent.setContent(doc.select("#content").html()
					+ "\n<!-- rendered by Sagan Renderer Service -->");
			guideContent.setTableOfContents(findTableOfContents(doc));
		} catch (IOException e) {
			throw new GuideRenderingException(guideContent.getName(), e);
		}
	}

	/**
	 * Extract top level table-of-content entries, and discard lower level links
	 *
	 * @param doc the rendered HTML guide
	 * @return HTML of the top tier table of content entries
	 */
	private String findTableOfContents(Document doc) {
		Elements toc = doc.select("div#toc > ul.sectlevel1");
		toc.select("ul.sectlevel2").forEach(subsection -> subsection.remove());
		toc.forEach(part -> part.select("a[href]").stream()
				.filter(anchor -> doc.select(anchor.attr("href")).get(0).parent()
						.classNames().stream()
						.anyMatch(clazz -> clazz.startsWith("reveal")))
				.forEach(href -> href.parent().remove()));
		return toc.toString();
	}
}
