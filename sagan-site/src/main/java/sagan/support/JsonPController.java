package sagan.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RestController;

/**
 * A convenience annotation that is itself annotated with {@link RestController @RestController}.
 * <p>
 * This annotation is targeted by the {@link sagan.projects.support.JsonPControllerAdvice} Controller advice
 * in order to support JSONP calls in endpoints defined in those controllers.

 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface JsonPController{

}
