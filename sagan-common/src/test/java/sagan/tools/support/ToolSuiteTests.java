package sagan.tools.support;

import sagan.tools.Architecture;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;
import sagan.tools.UpdateSiteArchive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolSuiteTests {

    @Test
    public void testPreferredDownloadLinks() throws Exception {
        ToolSuiteDownloads toolSuite = buildToolSuite();

        Set<DownloadLink> links = new HashSet<>();
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-win32.zip",
                "zip", "323MB", "windows", "32"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-win32-x86_64.zip",
                "zip", "323MB", "windows", "64"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-macosx-cocoa-x86_64.dmg",
                "dmg", "323MB", "mac", "64"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-linux-gtk.tar.gz",
                "tar.gz", "323MB", "linux", "32"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-linux-gtk-x86_64.tar.gz",
                "tar.gz", "323MB", "linux", "64"));

        assertThat(toolSuite.getPreferredDownloadLinks(), equalTo(links));
    }

    private ToolSuiteDownloads buildToolSuite() {
        Map<String, ToolSuitePlatform> platforms = new HashMap<>();

        List<EclipseVersion> macEclipseVersions = new ArrayList<>();

        List<Architecture> v36MacArchitectures = new ArrayList<>();
        List<DownloadLink> v36Mac64Links = new ArrayList<>();
        String os = "mac";
        String architecture = "64";
        v36Mac64Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-macosx-cocoa-x86_64.dmg",
                "dmg", "323MB", os, architecture));
        v36MacArchitectures.add(new Architecture("Mac OS X (Cocoa, 64bit)", v36Mac64Links));

        macEclipseVersions.add(new EclipseVersion("4.4", v36MacArchitectures));

        List<Architecture> v35MacArchitectures = new ArrayList<>();
        List<DownloadLink> v35Mac64Links = new ArrayList<>();
        architecture = "64";
        v35Mac64Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa-x86_64.tar.gz",
                "tar.gz", "323MB", os, architecture));
        v35Mac64Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa-x86_64-installer.dmg",
                "dmg", "323MB", os, architecture));
        v35MacArchitectures.add(new Architecture("Mac OS X (Cocoa, 64bit)", v35Mac64Links));

        List<DownloadLink> v35Mac32Links = new ArrayList<>();
        architecture = "32";
        v35Mac32Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa.tar.gz",
                "tar.gz", "323MB", os, architecture));
        v35Mac32Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa-installer.dmg",
                "dmg", "323MB", os, architecture));
        v35MacArchitectures.add(new Architecture("Mac OS X (Cocoa)", v35Mac32Links));
        macEclipseVersions.add(new EclipseVersion("4.3.2", v35MacArchitectures));

        platforms.put("mac", new ToolSuitePlatform("Mac", macEclipseVersions));

        os = "windows";
        List<Architecture> winArchitectures = new ArrayList<>();
        List<DownloadLink> win32Links = new ArrayList<>();
        architecture = "32";
        win32Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-win32.zip",
                "zip", "323MB", os, architecture));
        winArchitectures.add(new Architecture("Windows", win32Links));

        List<DownloadLink> win64Links = new ArrayList<>();
        architecture = "64";
        win64Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-win32-x86_64.zip",
                "zip", "323MB", os, architecture));
        winArchitectures.add(new Architecture("Windows (64bit)", win64Links));
        platforms.put("windows", new ToolSuitePlatform("Windows", Collections.singletonList(new EclipseVersion("4.4",
                winArchitectures))));

        os = "linux";
        List<Architecture> linuxArchitectures = new ArrayList<>();
        List<DownloadLink> linux32Links = new ArrayList<>();
        architecture = "32";
        linux32Links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-linux-gtk.tar.gz",
                "tar.gz", "323MB", os, architecture));
        linux32Links.add(new DownloadLink("http://example.com/linux.sh", "sh", "323MB", os, architecture));
        linuxArchitectures.add(new Architecture("Linux (GTK)", linux32Links));

        List<DownloadLink> linux64Links = new ArrayList<>();
        architecture = "64";
        linux64Links
                .add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-linux-gtk-x86_64.tar.gz",
                        "tar.gz", "323MB", os, architecture));
        linuxArchitectures.add(new Architecture("Linux (GTK, 64bit)", linux64Links));
        platforms.put("linux", new ToolSuitePlatform("Linux", Collections.singletonList(new EclipseVersion("4.3",
                linuxArchitectures))));

        List<UpdateSiteArchive> archives = Collections.emptyList();
        return new ToolSuiteDownloads("ShortName", "3.6.0.RELEASE",
                "http://static.springsource.org/sts/nan/latest/NewAndNoteworthy.html", platforms, archives);
    }
}
