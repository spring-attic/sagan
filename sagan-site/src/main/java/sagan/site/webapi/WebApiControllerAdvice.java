package sagan.site.webapi;

import sagan.site.webapi.release.InvalidReleaseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 */
@ControllerAdvice(basePackageClasses = IndexController.class)
public class WebApiControllerAdvice {

	@ExceptionHandler(InvalidReleaseException.class)
	public ResponseEntity<String> handleInvalidReleases(InvalidReleaseException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
