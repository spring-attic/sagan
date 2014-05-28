package sagan.team.support;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exception raised when an unknown or otherwise hidden team member page is requested,
 * e.g. /team/{unknown} or /admin/team/{unknown}.
 *
 * Note that because this class is marked with {@code @ResponseStatus(NOT_FOUND)}, the
 * site-wide 404 page will be displayed when this exception is handled. See
 * {@link sagan.MvcConfig.ErrorConfig} for details, and contrast the approach used here
 * with {@link sagan.MvcConfig#handleException(sagan.support.ResourceNotFoundException)}
 */
@ResponseStatus(NOT_FOUND)
@SuppressWarnings("serial")
class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String username) {
        this("Could not find member profile with username '%s'", username);
    }

    public MemberNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
