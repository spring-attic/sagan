package sagan.app.site;

import sagan.projects.service.ProjectMetadataService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UrlPathHelper;

class ViewRenderingHelper {

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private HttpServletRequest request;

    @Autowired
    private ProjectMetadataService projectMetadataService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String navClass(String active, String current) {
        if (active.equals(current)) {
            return "navbar-link active";
        } else {
            return "navbar-link";
        }
    }

    public String blogClass(String active, String current) {
        if (active.equals(current)) {
            return "blog-category active";
        } else {
            return "blog-category";
        }
    }

    public String path() {
        return urlPathHelper.getPathWithinApplication(request);
    }
}
