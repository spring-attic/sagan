package sagan.tools.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import sagan.support.cache.CachedRestClient;
import sagan.tools.EclipseDownloads;
import sagan.tools.ToolSuiteDownloads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ToolsService {
    private final ToolXmlConverter toolXmlConverter = new ToolXmlConverter();
    private final CachedRestClient restClient;
    private final RestTemplate restTemplate;
    private final XmlMapper serializer;

    @Autowired
    public ToolsService(CachedRestClient restClient, RestTemplate restTemplate, XmlMapper serializer) {
        this.restClient = restClient;
        this.restTemplate = restTemplate;
        this.serializer = serializer;
    }

    public ToolSuiteDownloads getStsGaDownloads() throws Exception {
        return getToolSuiteDownloads("Spring Tool Suite", "STS");
    }

    public ToolSuiteDownloads getStsMilestoneDownloads() throws Exception {
        return getToolSuiteDownloads("Milestone Version - Spring Tool Suite", "STS");
    }

    public Collection<ToolSuiteDownloads> getStsLegacyDownloads() throws Exception {
        List<ToolSuiteDownloads> previousStsDownloads = new ArrayList<>();

        previousStsDownloads.addAll(getLegacyToolSuiteDownloads("Spring Tool Suite", "STS"));
        previousStsDownloads.addAll(getLegacyToolSuiteDownloads("Previous Version", "STS"));
        return previousStsDownloads;
    }

    private ToolSuiteDownloads getToolSuiteDownloads(String toolSuiteName, String shortName) throws Exception {
        String responseXml =
                restClient.get(restTemplate, "http://dist.springsource.com/release/STS/index-new.xml",
                        String.class);
        ToolSuiteXml toolSuiteXml = serializer.readValue(responseXml, ToolSuiteXml.class);
        return toolXmlConverter.convert(toolSuiteXml, toolSuiteName, shortName);
    }

    private Collection<ToolSuiteDownloads> getLegacyToolSuiteDownloads(String toolSuiteName, String shortName) throws Exception {
        String responseXml =
                restClient.get(restTemplate, "http://dist.springsource.com/release/STS/index-new.xml",
                        String.class);
        ToolSuiteXml toolSuiteXml = serializer.readValue(responseXml, ToolSuiteXml.class);
        return toolXmlConverter.convertLegacy(toolSuiteXml, toolSuiteName, shortName);
    }

    public EclipseDownloads getEclipseDownloads() throws Exception {
        String responseXml =
                restClient.get(restTemplate, "http://dist.springsource.com/release/STS/eclipse.xml",
                        String.class);
        EclipseXml eclipseXml = serializer.readValue(responseXml, EclipseXml.class);
        return new EclipseDownloadsXmlConverter().convert(eclipseXml);
    }
}
