/*
 * Copyright 2016 the original author or authors.
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

package sagan.projects.support;

import java.util.List;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 * Projection interface for SVG templates.
 *
 * @author Mark Paluch
 */
public interface BadgeSvg {

    @XBRead("/svg/g/text")
    List<GraphicElement> getGraphicElements();

    @XBRead("/svg/g/path")
    List<Path> getPaths();

    @XBRead("/svg/mask/rect")
    Mask getMask();

    @XBWrite("/svg/@width")
    void setWidth(int width);

    interface GraphicElement {
        @XBRead("@x")
        Integer getXPosition();

        @XBRead("@y")
        Integer getYPosition();

        @XBWrite("@x")
        void setXPosition(int x);

        @XBWrite("@y")
        void setYPosition(int y);

        @XBRead(".")
        String getText();

        @XBWrite(".")
        void setText(String text);

    }

    interface Path {

        @XBWrite("@d")
        void setDraw(String d);

    }

    interface Mask {

        @XBRead("@width")
        Integer getWidth();

        @XBWrite("@width")
        void setWidth(int width);

    }
}
