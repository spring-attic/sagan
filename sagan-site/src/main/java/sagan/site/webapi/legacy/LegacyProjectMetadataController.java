package sagan.site.webapi.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sagan.site.projects.Project;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.projects.Release;
import sagan.site.projects.ReleaseStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Legacy API Endpoint for consumers of the previous (unofficial) API.
 * Many Spring Initializr instances rely on this endpoint for fetching the Spring Boot versions.
 */
@RestController
public class LegacyProjectMetadataController {

	private final ProjectMetadataService projectMetadataService;

	private final ObjectMapper objectMapper;

	public LegacyProjectMetadataController(ProjectMetadataService projectMetadataService, ObjectMapper objectMapper) {
		this.projectMetadataService = projectMetadataService;
		this.objectMapper = objectMapper;
	}

	@GetMapping(path = "/project_metadata/{projectName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ObjectNode> legacyProjectMetadata(@PathVariable String projectName) {
		Project project = this.projectMetadataService.fetchFullProject(projectName);
		if (project == null) {
			return ResponseEntity.notFound().build();
		}
		ObjectNode body = this.objectMapper.createObjectNode();
		body.put("id", project.getId());
		body.put("name", project.getName());
		ArrayNode releases = body.putArray("projectReleases");
		for (Release release : project.getReleases()) {
			ObjectNode relNode = objectMapper.createObjectNode();
			relNode.put("version", release.getVersion().toString());
			relNode.put("versionDisplayName", release.getVersion().toString());
			relNode.put("current", release.isCurrent());
			relNode.put("releaseStatus", release.getReleaseStatus().toString());
			relNode.put("snapshot", release.getReleaseStatus().equals(ReleaseStatus.SNAPSHOT));
			releases.add(relNode);
		}
		return ResponseEntity.ok(body);
	}
}
