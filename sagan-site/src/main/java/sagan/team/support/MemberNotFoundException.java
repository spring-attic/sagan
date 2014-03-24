package sagan.team.support;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("serial")
@ResponseStatus(NOT_FOUND)
class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String username) {
        this("Could not find member profile with username '%s'", username);
    }

    public MemberNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
