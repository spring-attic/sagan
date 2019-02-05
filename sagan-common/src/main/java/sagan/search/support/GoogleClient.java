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
package sagan.search.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author Dave Syer
 *
 */
@Component
public class GoogleClient {

    @Value("${search.engine}")
    private String engine;

    @Value("${search.key}")
    private String apiKey;

    private RestTemplate rest;

    private Gson gson;

    @Autowired
    public GoogleClient(RestTemplateBuilder builder, Gson gson) {
        this.gson = gson;
        this.rest = builder.build();
    }

    public GoogleResult execute(String term, int page) {
        if (term == null) {
            term = "";
        }
        int start = page * 10 + 1;
        return new GoogleResult(gson.fromJson(rest.getForObject(
                "https://content.googleapis.com/customsearch/v1?cx={engine}&q={term}&key={key}&start={start}",
                String.class, engine, term, apiKey, start), JsonObject.class));
    }

}
