package sagan.feature;

import sagan.feature.config.FeatureAutoConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.request.AbstractRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class FeatureScopeTests {

    @Test
    public void rebindFeatureProperties() {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestConfiguration.class).run();
        FeatureRebinder rebinder = context.getBean(FeatureRebinder.class);
        FeatureProperties features = context.getBean(FeatureProperties.class);
        assertThat(features.isEnabled(), is(false));
        rebinder.rebind(Collections.singletonMap("sagan.feature.enabled", "true"));
        assertThat(features.isEnabled(), is(true));
        context.close();
    }

    @Test
    public void requestScopedTarget() {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestConfiguration.class).run();
        FeatureProperties defaults = context.getBean(FeatureProperties.class);
        RequestContextHolder.setRequestAttributes(new TestRequestAttributes());
        FeatureProperties features = context.getBean(FeatureProperties.class);
        assertThat(features, not(equalTo(defaults)));
        context.close();
    }

    @Test
    public void normalTarget() {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(TestConfiguration.class).run();
        FeatureProperties defaults = context.getBean(FeatureProperties.class);
        FeatureProperties features = context.getBean(FeatureProperties.class);
        assertThat(features, equalTo(defaults));
        context.close();
    }

    private static class TestRequestAttributes extends AbstractRequestAttributes {

        private Object mutex = new Object();

        private Map<String, Object> map = new HashMap<>();

        @Override
        public Object getAttribute(String name, int scope) {
            return map.get(name);
        }

        @Override
        public void setAttribute(String name, Object value, int scope) {
            map.put(name, value);
        }

        @Override
        public void removeAttribute(String name, int scope) {
            map.remove(name);
        }

        @Override
        public String[] getAttributeNames(int scope) {
            return map.keySet().toArray(new String[0]);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback, int scope) {
        }

        @Override
        public Object resolveReference(String key) {
            return map;
        }

        @Override
        public String getSessionId() {
            return "XXXX";
        }

        @Override
        public Object getSessionMutex() {
            return mutex;
        }

        @Override
        protected void updateAccessedSessionAttributes() {
        }

    }

    @Configuration
    @Import(FeatureAutoConfiguration.class)
    protected static class TestConfiguration {
        @Bean
        // Eclipse cannot run the tests in sagan-site without this
        @ConditionalOnMissingClass("org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration")
        public EmbeddedServletContainerFactory containerFactory() {
            return null;
        }
    }
}
