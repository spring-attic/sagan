package sagan.projects.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sagan.projects.Project;
import sagan.projects.service.ProjectMetadataService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/project_metadata")
public class ProjectMetadataController {

    private ProjectMetadataService service;

    @Autowired
    public ProjectMetadataController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value="/{projectId}", method = { GET, HEAD })
    public void projectMetadata(@PathVariable("projectId") String projectId, @RequestParam("callback") String callback, HttpServletResponse response) throws IOException {
        response.setContentType("text/javascript; charset=UTF-8");
        PrintWriter out = response.getWriter();

        Project project = service.getProject(projectId);

        ObjectMapper objectMapper = new ObjectMapper();

        out.print(callback + String.format("(%s);", objectMapper.writeValueAsString(project)));
    }

}
