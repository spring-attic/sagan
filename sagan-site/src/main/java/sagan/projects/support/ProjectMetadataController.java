package sagan.projects.support;

import org.springframework.util.StringUtils;
import sagan.projects.Project;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles ajax requests for project metadata, typically from the
 * individual Spring project pages managed via GitHub's "GH Pages" infrastructure at
 * http://projects.spring.io. See https://github.com/spring-projects/gh-pages#readme for
 * more information.
 */
@Controller
@RequestMapping("/project_metadata")
class ProjectMetadataController {

    private final ProjectMetadataService service;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProjectMetadataController(ProjectMetadataService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(value = "/{projectId}", method = { GET, HEAD })
    public void projectMetadata(@PathVariable("projectId") String projectId,
                                @RequestParam(value="callback", required=false) String callback,
                                HttpServletResponse response) throws IOException {
        response.setContentType("text/javascript; charset=UTF-8");
        PrintWriter out = response.getWriter();

        Project project = service.getProject(projectId);

        if(callback == null || !StringUtils.hasText(callback)) {
            out.print(this.objectMapper.writeValueAsString(project));
        } else {
            out.print(callback + String.format("(%s);", this.objectMapper.writeValueAsString(project)));
        }
    }

}
