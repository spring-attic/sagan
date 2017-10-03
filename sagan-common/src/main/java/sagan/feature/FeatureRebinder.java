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

package sagan.feature;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

public class FeatureRebinder implements EnvironmentAware, ApplicationContextAware {

    private final FeatureProperties beans;

    private ConfigurableEnvironment environment = new StandardEnvironment();

    private ApplicationContext applicationContext;

    private List<GenericConverter> genericConverters = Collections.emptyList();

    private List<Converter<?, ?>> converters = Collections.emptyList();

    public FeatureRebinder(FeatureProperties beans) {
        this.beans = beans;
    }

    @Autowired(required = false)
    @ConfigurationPropertiesBinding
    public void setConverters(List<Converter<?, ?>> converters) {
        this.converters = converters;
    }

    @Autowired(required = false)
    @ConfigurationPropertiesBinding
    public void setGenericConverters(List<GenericConverter> converters) {
        this.genericConverters = converters;
    }

    public void rebind(Map<String, Object> source) {
        ConfigurationPropertiesBindingPostProcessor binder = new ConfigurationPropertiesBindingPostProcessor();
        binder.setApplicationContext(applicationContext);
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        binder.setBeanFactory(beanFactory);
        binder.setConverters(converters);
        binder.setGenericConverters(genericConverters);
        MutablePropertySources propertySources = new MutablePropertySources(environment.getPropertySources());
        propertySources.addFirst(new MapPropertySource("request", source));
        binder.setPropertySources(propertySources);
        try {
            binder.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initialize binder", e);
        }
        binder.postProcessBeforeInitialization(beans, "featureProperties");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

}