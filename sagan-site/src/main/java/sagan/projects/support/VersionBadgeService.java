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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;
import sagan.projects.support.BadgeSvg.GraphicElement;
import sagan.projects.support.BadgeSvg.Path;

import com.google.common.io.Resources;

/**
 * Service to generate SVG badges.
 *
 * @author Mark Paluch
 */
@Service
public class VersionBadgeService {

    private final URL PRERELEASE_TEMPLATE = Resources.getResource("badge/milestone.svg");
    private final URL GENERAL_AVAILABILITY_TEMPLATE = Resources.getResource("badge/release.svg");
    private final URL SNAPSHOT_TEMPLATE = Resources.getResource("badge/snapshot.svg");
    private final URL VERDANA_FONT = Resources.getResource("badge/Verdana.ttf");
    private final BufferedImage DUMMY = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    private Graphics graphics;
    private XBProjector xbProjector;

    @PostConstruct
    public void postConstruct() throws Exception {

        Font font;
        try (InputStream is = VERDANA_FONT.openStream()) {
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(0, 11);
        }

        graphics = DUMMY.getGraphics();
        graphics.setFont(font);

        DefaultXMLFactoriesConfig myConfig = new DefaultXMLFactoriesConfig();
        myConfig.setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);

        xbProjector = new XBProjector(myConfig, Flags.TO_STRING_RENDERS_XML);
    }

    @PreDestroy
    public void preDestroy() {
        graphics.dispose();
    }

    /**
     * Create a version badge for a given {@link Project} and {@link ProjectRelease}. The badge uses SVG and is returned as byte
     * array.
     *
     * @param project must not be {@literal null}.
     * @param projectRelease must not be {@literal null}.
     * @return
     * @throws IOException
     */
    public byte[] createSvgBadge(Project project, ProjectRelease projectRelease) throws IOException {

        Assert.notNull(project, "Project must not be null!");
        Assert.notNull(projectRelease, "ProjectRelease must not be null!");

        URL template = getTemplate(projectRelease.getReleaseStatus());

        BadgeSvg svgDocument = xbProjector.io().url(template.toString()).read(BadgeSvg.class);

        List<Path> paths = svgDocument.getPaths();

        String label = project.getName();
        String version = projectRelease.getVersion();

        return createSvgBadge(svgDocument, paths, label, version);
    }

    private byte[] createSvgBadge(BadgeSvg svgDocument, List<Path> paths, String label, String version) {

        List<GraphicElement> graphicElements = svgDocument.getGraphicElements();
        graphicElements.get(0).setText(label);
        graphicElements.get(1).setText(label);

        graphicElements.get(2).setText(version);
        graphicElements.get(3).setText(version);

        int labelMarginLeft = 6;
        int labelMarginRight = 4;

        int versionMarginLeft = 4;
        int versionMarginRight = 6;

        int labelTextWidth;
        int versionTextWidth;

        synchronized (graphics) {
            FontMetrics fontMetrics = graphics.getFontMetrics();
            labelTextWidth = fontMetrics.stringWidth(label);
            versionTextWidth = fontMetrics.stringWidth(version);
        }

        int labelWidth = labelTextWidth + labelMarginLeft + labelMarginRight;
        int versionWidth = versionTextWidth + versionMarginLeft + versionMarginRight;

        svgDocument.setWidth(labelWidth + versionWidth);
        svgDocument.getMask().setWidth(labelWidth + versionWidth);

        graphicElements.get(0).setXPosition(labelMarginLeft + (labelTextWidth / 2));
        graphicElements.get(1).setXPosition(labelMarginLeft + (labelTextWidth / 2));

        graphicElements.get(2).setXPosition(labelWidth + versionMarginLeft + (versionTextWidth / 2));
        graphicElements.get(3).setXPosition(labelWidth + versionMarginLeft + (versionTextWidth / 2));

        paths.get(0).setDraw(String.format("M0 0h%dv20H0z", labelWidth));
        paths.get(1).setDraw(String.format("M%d 0h%dv20H%dz", labelWidth, versionWidth, labelWidth));
        paths.get(2).setDraw(String.format("M0 0h%dv20H0z", labelWidth + versionWidth));

        return svgDocument.toString().getBytes();
    }

    private URL getTemplate(ReleaseStatus releaseStatus) {

        switch (releaseStatus) {
            case GENERAL_AVAILABILITY:
                return GENERAL_AVAILABILITY_TEMPLATE;
            case PRERELEASE:
                return PRERELEASE_TEMPLATE;
            default:
                return SNAPSHOT_TEMPLATE;
        }
    }
}
