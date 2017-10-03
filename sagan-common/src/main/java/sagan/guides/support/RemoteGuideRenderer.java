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
package sagan.guides.support;

import sagan.guides.DocumentContent;

import org.springframework.web.client.RestTemplate;

/**
 * @author Dave Syer
 *
 */
public class RemoteGuideRenderer implements GuideRenderer {

    private final RestTemplate template;
    private String uri;

    public RemoteGuideRenderer(RestTemplate builder, String uri) {
        this.template = builder;
        this.uri = uri;
    }

    @Override
    public DocumentContent render(String path) {
        return this.template.getForObject(this.uri + "/guides/" + path, AsciidocGuide.class);
    }

}
