package sagan.guides;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("serial")
@ResponseStatus(NOT_FOUND)
public class UnderstandingGuideNotFoundException extends RuntimeException{
    public UnderstandingGuideNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
