package sagan.projects.support;

import sagan.support.JsonPController;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice(annotations = JsonPController.class)
public class JsonPControllerAdvice extends AbstractJsonpResponseBodyAdvice {

	public JsonPControllerAdvice() {
		super("callback");
	}
}
