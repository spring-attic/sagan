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

package sagan.renderer.markdown;

import java.util.Collections;

import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.RootNode;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dave Syer
 *
 */
@RestController
public class MarkdownController {

    private final PegDownProcessor pegdown;

    public MarkdownController() {
        pegdown = new PegDownProcessor(Extensions.ALL ^ Extensions.ANCHORLINKS);
    }

    @PostMapping("/documents")
    public String render(@RequestBody String markup) {
        // synchronizing on pegdown instance since neither the processor nor the
        // underlying parser is thread-safe.
        synchronized (pegdown) {
            RootNode astRoot = pegdown.parseMarkdown(markup.toCharArray());
            MarkdownToHtmlSerializer serializer = new MarkdownToHtmlSerializer(
                    new LinkRenderer(),
                    Collections.singletonMap(VerbatimSerializer.DEFAULT,
                            PrettifyVerbatimSerializer.INSTANCE));
            return serializer.toHtml(astRoot)
                    + "\n<!-- rendered by MarkdownController -->";
        }
    }

}
