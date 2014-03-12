package sagan.support.github;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

public class DownloadConverter implements HttpMessageConverter<byte[]> {

    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(new MediaType("application", "vnd.github.v3.raw"));
    }

    @Override
    public byte[] read(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        return StreamUtils.copyToByteArray(inputMessage.getBody());
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return byte[].class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public void write(byte[] download, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        throw new UnsupportedOperationException("can't write");
    }
}
