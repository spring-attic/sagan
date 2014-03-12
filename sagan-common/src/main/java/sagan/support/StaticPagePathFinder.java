package sagan.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class StaticPagePathFinder {
    private ResourcePatternResolver resourceResolver;

    public static class PagePaths {
        private String filePath;
        private String urlPath;

        public PagePaths(String filePath, String urlPath) {
            this.filePath = filePath;
            this.urlPath = urlPath;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getUrlPath() {
            return urlPath;
        }
    }

    @Autowired
    public StaticPagePathFinder(ResourcePatternResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    public List<PagePaths> findPaths() throws IOException {
        Resource baseResource = resourceResolver.getResource("classpath:/templates/pages");
        String basePath = baseResource.getURL().getPath();
        Resource[] resources = resourceResolver.getResources("classpath:/templates/pages/**/*.html");
        List<PagePaths> paths = new ArrayList<>();
        for (Resource resource : resources) {
            String filePath = relativeFilePath(basePath, resource);
            paths.add(new PagePaths(filePath, buildRequestMapping(filePath)));
        }
        return paths;
    }

    private String relativeFilePath(String basePath, Resource resource) throws IOException {
        return resource.getURL().getPath().substring(basePath.length()).replace(".html", "");
    }

    private String buildRequestMapping(String filePath) {
        String requestMapping = filePath;
        if (requestMapping.endsWith("/index")) {
            requestMapping = requestMapping.replace("/index", "");
            if (requestMapping.equals("")) {
                requestMapping = "/";
            }
        }
        return requestMapping;
    }
}
