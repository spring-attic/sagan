package io.spring.site.domain.projects;

import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

class ProjectParser {

    private final Map<String, String> variables;
    private final Map<String, String> defaultUrls;

    ProjectParser(Map<String, Map<String, String>> metadata) {
        variables = metadata.get("variables");
        defaultUrls = metadata.get("defaultUrls");
    }

    Project parse(Map<String, Object> projectData) {
        String id = projectData.get("id").toString();
        variables.put("id", id);
        String name = projectData.get("name").toString();
        String repoUrl = parseRepoUrl(projectData);
        String siteUrl = parseSiteUrl(projectData);
        boolean isAggregator = parseIsAggregator(projectData);
        List<ProjectRelease> documentationList = new ProjectVersionsParser(variables, defaultUrls).parse(projectData);
        variables.remove("id");
        return new Project(id, name, repoUrl, siteUrl, documentationList, isAggregator);
    }

    private String parseRepoUrl(Map<String, Object> projectData) {
        String repoUrl;
        if (projectData.containsKey("repoUrl")) {
            repoUrl = projectData.get("repoUrl").toString();
        } else {
            repoUrl = defaultUrls.get("repoUrl");
        }
        return new UriTemplate(repoUrl).expand(variables).toString();
    }

    private String parseSiteUrl(Map<String, Object> projectData) {
        String siteUrl;
        if (projectData.containsKey("siteUrl")) {
            siteUrl = projectData.get("siteUrl").toString();
            if (siteUrl.equals("NONE")) {
                return "";
            }
        } else {
            siteUrl = defaultUrls.get("siteUrl");
        }
        return new UriTemplate(siteUrl).expand(variables).toString();
    }

    private boolean parseIsAggregator(Map<String, Object> projectData) {
        return Boolean.TRUE.equals(projectData.get("aggregator"));
    }
}
