package sagan.tools.support;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import sagan.support.Fixtures;
import sagan.tools.Release;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ToolSuiteXmlParsingTests {
    private String responseXml = Fixtures.load("/fixtures/tools/sts_downloads.xml");

    @Test
    public void unmarshal() throws Exception {
        XmlMapper serializer = new XmlMapper();

        ToolSuiteXml toolSuiteXml = serializer.readValue(responseXml, ToolSuiteXml.class);

        assertThat(toolSuiteXml.getReleases(), notNullValue());
        assertThat(toolSuiteXml.getReleases().size(), equalTo(4));
        Release release = toolSuiteXml.getReleases().get(0);
        assertThat(release.getDownloads().size(), equalTo(7));
        assertThat(release.getWhatsnew(), notNullValue());

        assertThat(toolSuiteXml.getOthers(), notNullValue());
        assertThat(toolSuiteXml.getOthers().size(), equalTo(43));
        Release oldRelease = toolSuiteXml.getOthers().get(0);
        assertThat(oldRelease.getDownloads().size(), equalTo(17));
        assertThat(oldRelease.getWhatsnew(), notNullValue());
    }
}
