package org.springframework.site.domain.tools;

import org.simpleframework.xml.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.tools.toolsuite.parser.ToolXmlConverter;
import org.springframework.site.domain.tools.toolsuite.ToolSuite;
import org.springframework.site.domain.tools.toolsuite.xml.ToolSuiteXml;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ToolsService {
	private final ToolXmlConverter toolXmlConverter = new ToolXmlConverter();
	private RestTemplate restTemplate;
	private Serializer serializer;

	@Autowired
	public ToolsService(RestTemplate restTemplate, Serializer serializer) {
		this.restTemplate = restTemplate;
		this.serializer = serializer;
	}

	public ToolSuite getStsDownloads() throws Exception {
		String responseXml = restTemplate.getForObject("http://dist.springsource.com/release/STS/index-new.xml", String.class);
		ToolSuiteXml toolSuiteXml = serializer.read(ToolSuiteXml.class, responseXml);
		return toolXmlConverter.convert(toolSuiteXml, "Spring Tool Suite");
	}

	public ToolSuite getGgtsDownloads() throws Exception {
		String responseXml = restTemplate.getForObject("http://dist.springsource.com/release/STS/index-new.xml", String.class);
		ToolSuiteXml toolSuiteXml = serializer.read(ToolSuiteXml.class, responseXml);
		return toolXmlConverter.convert(toolSuiteXml, "Groovy/Grails Tool Suite");
	}
}
