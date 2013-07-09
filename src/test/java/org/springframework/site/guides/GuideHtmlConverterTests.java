package org.springframework.site.guides;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.Cookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GuideHtmlConverterTests {
	private GuideHtmlConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new GuideHtmlConverter();
	}

	@Test
	public void readInputStreamAsUTF8() throws Exception {
		final String input = "└── is a unicode char";

		HttpInputMessage inputMessage = new HttpInputMessage() {
			@Override
			public InputStream getBody() throws IOException {
				return new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8")));
			}

			@Override
			public HttpHeaders getHeaders() {
				return null;
			}

			@Override
			public Cookies getCookies() {
				return null;
			}
		};
		GuideHtml output = converter.read(GuideHtml.class, inputMessage);
		assertThat(output.getHtml(), equalTo(input));
	}
}
