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
package sagan.renderer.asciidoctor;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dave Syer
 *
 */
@RestController
public class AsciidoctorController {

    private final Asciidoctor asciidoctor;

    public AsciidoctorController(Asciidoctor asciidoctor) {
        this.asciidoctor = asciidoctor;
    }

    @PostMapping(path = "/documents", consumes = "text/asciidoc")
    public String render(@RequestBody String markup) {
        Attributes attributes = new Attributes();
        attributes.setAllowUriRead(true);
        attributes.setSkipFrontMatter(true);
        attributes.setAttribute("source-highlighter", "prettify");
        attributes.setAttribute("idprefix", "");
        attributes.setAttribute("idseparator", "-");
        attributes.setAnchors(true);
        OptionsBuilder options = OptionsBuilder.options().safe(SafeMode.SAFE)
                .attributes(attributes);
        return asciidoctor.convert(markup, options)
                + "\n<!-- rendered by AsciidoctorController -->";
    }

}
