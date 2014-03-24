package sagan.team.support;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("serial")
@ResponseStatus(NOT_FOUND)
class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
