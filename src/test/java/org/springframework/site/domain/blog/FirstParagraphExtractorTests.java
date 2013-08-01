package org.springframework.site.domain.blog;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FirstParagraphExtractorTests 	{

	private FirstParagraphExtractor extractor = new FirstParagraphExtractor();

	@Test
	public void extractsTheFirstParagraph() throws Exception {
		assertThat(extractor.extract("xx\n\nxxx", 20), is("xx"));
	}

	@Test
	public void extractsUpToTheMaximumLength() throws Exception {
		assertThat(extractor.extract("ThisIsAReallyLongSentence", 13), is("ThisIsAReally"));
	}

	@Test
	public void theLastWordIsNotCutoffMidway() throws Exception {
		assertThat(extractor.extract("Some content\n\nThe rest", 7), is("Some"));
	}

}
