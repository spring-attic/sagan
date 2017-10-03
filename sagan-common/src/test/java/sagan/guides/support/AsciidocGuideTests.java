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

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Dave Syer
 *
 */
public class AsciidocGuideTests {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testJson() throws Exception {
        AsciidocGuide guide = mapper.readValue(mapper.writeValueAsString(new AsciidocGuide("content", "toc")),
                AsciidocGuide.class);
        assertThat(guide.getContent(), equalTo("content"));
    }

}
