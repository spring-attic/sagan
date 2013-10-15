package sagan.guides;

import sagan.guides.convert.GuideHtml;
import sagan.guides.convert.GuideHtmlConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
        };
        GuideHtml output = converter.read(GuideHtml.class, inputMessage);
        assertThat(output.getHtml(), equalTo(input));
    }
}
