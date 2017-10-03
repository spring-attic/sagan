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

package sagan.feature.support;

import sagan.feature.FeatureProperties;
import sagan.feature.FeatureRebinder;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class FeatureFilter implements Filter {

    private final FeatureRebinder binder;
    private final FeatureProperties features;

    public FeatureFilter(FeatureRebinder binder, FeatureProperties features) {
        this.binder = binder;
        this.features = features;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String header = ((HttpServletRequest) request).getHeader("X-Enabled");
        Map<String, Object> map = new LinkedHashMap<>();
        if (header != null && !"false".equals(header) || isWhitelisted(((HttpServletRequest) request).getUserPrincipal())) {
            map.put("sagan.feature.enabled", true);
            binder.rebind(map);
        }
        chain.doFilter(request, response);
    }

    private boolean isWhitelisted(Principal user) {
        if (user instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) user;
            return (features.getUsers().contains(token.getCredentials()));
        }
        return false;
    }

    @Override
    public void destroy() {
    }

}