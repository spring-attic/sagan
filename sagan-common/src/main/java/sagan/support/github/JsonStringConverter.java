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

public class JsonStringConverter implements HttpMessageConverter<String> {

    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(new MediaType("application", "json"));
    }

    @Override
    public String read(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        return StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return String.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public void write(String string, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        throw new UnsupportedOperationException("can't write");
    }
}
