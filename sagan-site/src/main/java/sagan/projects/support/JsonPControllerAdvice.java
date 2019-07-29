package sagan.projects.support;

import sagan.support.JsonPController;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice(annotations = JsonPController.class)
public class JsonPControllerAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonPControllerAdvice() {
        super("callback");
    }
}

class AbstractJsonpResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * Pattern for validating jsonp callback parameter values.
     */
    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");

    private final Log logger = LogFactory.getLog(getClass());

    private final String[] jsonpQueryParamNames;

    protected AbstractJsonpResponseBodyAdvice(String... queryParamNames) {
        Assert.isTrue(!ObjectUtils.isEmpty(queryParamNames), "At least one query param name is required");
        this.jsonpQueryParamNames = queryParamNames;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    @Nullable
    public final Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
                                        MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType,
                                        ServerHttpRequest request, ServerHttpResponse response) {

        if (body == null) {
            return null;
        }
        MappingJacksonValue container = getOrCreateContainer(body);
        String prefix = beforeBodyWriteInternal(container, contentType, returnType, request, response);
        if (prefix!=null) {
            @SuppressWarnings("unchecked")
            HttpMessageConverter<Object> converter = (HttpMessageConverter<Object>) BeanUtils.instantiateClass(converterType);
            try {
                prefix = "/**/" + prefix + "(";
                response.getBody().write(prefix.getBytes());
                converter.write(container.getValue(), contentType, response);
                response.getBody().write(");".getBytes());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            return null;
        }
        return container;
    }

    protected String beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
                                             MethodParameter returnType, ServerHttpRequest request,
                                             ServerHttpResponse response) {

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        for (String name : this.jsonpQueryParamNames) {
            String value = servletRequest.getParameter(name);
            if (value != null) {
                if (!isValidJsonpQueryParam(value)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring invalid jsonp parameter value: " + value);
                    }
                    continue;
                }
                MediaType contentTypeToUse = getContentType(contentType, request, response);
                response.getHeaders().setContentType(contentTypeToUse);
                return value;
            }
        }
        return null;
    }

    /**
     * Validate the jsonp query parameter value. The default implementation returns true
     * if it consists of digits, letters, or "_" and ".". Invalid parameter values are
     * ignored.
     * 
     * @param value the query param value, never {@code null}
     * @since 4.1.8
     */
    protected boolean isValidJsonpQueryParam(String value) {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }

    /**
     * Return the content type to set the response to. This implementation always returns
     * "application/javascript".
     * 
     * @param contentType the content type selected through content negotiation
     * @param request the current request
     * @param response the current response
     * @return the content type to set the response to
     */
    protected MediaType getContentType(MediaType contentType, ServerHttpRequest request, ServerHttpResponse response) {
        return new MediaType("application", "javascript");
    }

    /**
     * Wrap the body in a {@link MappingJacksonValue} value container (for providing
     * additional serialization instructions) or simply cast it if already wrapped.
     */
    protected MappingJacksonValue getOrCreateContainer(Object body) {
        return (body instanceof MappingJacksonValue ? (MappingJacksonValue) body : new MappingJacksonValue(body));
    }

}