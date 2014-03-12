package sagan.support.github;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

public class MarkdownHtmlConverter implements HttpMessageConverter<MarkdownHtml> {

    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(new MediaType("application", "vnd.github.v3.html+json"));
    }

    @Override
    public MarkdownHtml read(Class<? extends MarkdownHtml> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        return new MarkdownHtml(StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8")));
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return MarkdownHtml.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public void write(MarkdownHtml markdownHtml, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException("can't write");
    }
}
