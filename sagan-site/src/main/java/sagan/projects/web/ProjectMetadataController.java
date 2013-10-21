package sagan.projects.web;

import sagan.projects.Project;
import sagan.projects.service.ProjectMetadataService;

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

@Controller
@RequestMapping("/project_metadata")
public class ProjectMetadataController {

    private ProjectMetadataService service;

    @Autowired
    public ProjectMetadataController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value = "/{projectId}", method = { GET, HEAD })
    public void projectMetadata(@PathVariable("projectId") String projectId, @RequestParam("callback") String callback,
                                HttpServletResponse response) throws IOException {
        response.setContentType("text/javascript; charset=UTF-8");
        PrintWriter out = response.getWriter();

        Project project = service.getProject(projectId);

        ObjectMapper objectMapper = new ObjectMapper();

        out.print(callback + String.format("(%s);", objectMapper.writeValueAsString(project)));
    }

}
