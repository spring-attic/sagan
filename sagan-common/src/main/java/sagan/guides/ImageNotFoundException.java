package sagan.guides;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("serial")
@ResponseStatus(NOT_FOUND)
public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }

}

