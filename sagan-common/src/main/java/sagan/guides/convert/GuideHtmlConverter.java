package sagan.guides.convert;

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

public class GuideHtmlConverter implements HttpMessageConverter<GuideHtml> {

    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(new MediaType("application", "vnd.github.v3.html+json"));
    }

    @Override
    public GuideHtml read(Class<? extends GuideHtml> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        String bodyString = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
        return new GuideHtml(bodyString);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return GuideHtml.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public void write(GuideHtml html, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        throw new UnsupportedOperationException("can't write");
    }
}
