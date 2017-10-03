/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sagan.feature.config;

import sagan.feature.FeatureProperties;
import sagan.feature.FeatureRebinder;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;

/**
 * @author Dave Syer
 *
 */
@Configuration
@EnableConfigurationProperties(FeatureProperties.class)
public class FeatureAutoConfiguration {

    @Bean
    public FeatureRebinder featureRebinder(FeatureProperties featureBeans) {
        return new FeatureRebinder(featureBeans);
    }

    @Component
    static class FeatureScope implements Scope, BeanFactoryPostProcessor {

        private Scope delegate = new RequestScope();

        private Map<String, Object> objects = new HashMap<>();

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
            beanFactory.registerScope("feature", this);
            for (String name : beanFactory.getBeanNamesForType(FeatureProperties.class)) {
                beanFactory.getBeanDefinition(name).setScope("feature");
            }
        }

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            if (RequestContextHolder.getRequestAttributes() == null) {
                return this.objects.computeIfAbsent(name, id -> objectFactory.getObject());
            }
            return this.delegate.get(name, objectFactory);
        }

        @Override
        public Object remove(String name) {
            if (RequestContextHolder.getRequestAttributes() == null) {
                return this.objects.remove(name);
            }
            return this.delegate.remove(name);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            if (RequestContextHolder.getRequestAttributes() == null) {
                return;
            }
            this.delegate.registerDestructionCallback(name, callback);
        }

        @Override
        public Object resolveContextualObject(String key) {
            if (RequestContextHolder.getRequestAttributes() == null) {
                return null;
            }
            return this.delegate.resolveContextualObject(key);
        }

        @Override
        public String getConversationId() {
            if (RequestContextHolder.getRequestAttributes() == null) {
                return null;
            }
            return this.delegate.getConversationId();
        }

    }
}
