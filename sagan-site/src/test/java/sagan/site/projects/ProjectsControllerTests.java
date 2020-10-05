package sagan.site.projects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sagan.site.TestSecurityConfig;
import sagan.site.guides.GettingStartedGuides;
import sagan.site.guides.GuideHeader;
import sagan.site.guides.Topicals;
import sagan.site.guides.Tutorials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectsController.class)
@TestPropertySource(properties = "spring.profiles.active=standalone")
@Import(TestSecurityConfig.class)
public class ProjectsControllerTests {
	@MockBean
	private ProjectMetadataService projectMetadataService;

	@MockBean
	private GettingStartedGuides projectGuidesRepo;

	@MockBean
	private Tutorials projectTutorialRepo;

	@MockBean
	private Topicals projectTopicalRepo;

	private Release currentRelease;

	private Release anotherRelease;

	private Release snapshotRelease;

	private SortedSet<Release> releases;

	private List<ProjectGroup> projectGroups;

	private Project springBoot;

	private Project springData;

	private Project springDataJpa;

	@Autowired
	private MockMvc mvc;

	@BeforeEach
	public void setUp() throws Exception {
		ProjectGroup pg1 = new ProjectGroup("microservices", "Microservices");
		ProjectGroup pg2 = new ProjectGroup("cloud", "Cloud");
		this.projectGroups = Arrays.asList(pg1, pg2);

		this.springBoot = new Project("spring-boot", "Spring Boot");
		this.springBoot.getDisplay().setFeatured(true);
		this.springBoot.setGroups(new HashSet<>(Arrays.asList(pg1)));
		this.springBoot.setStackOverflowTags("spring-boot");
		this.currentRelease = new Release(Version.of("2.1.0.RELEASE"), true);
		this.anotherRelease = new Release(Version.of("2.0.0.RELEASE"));
		this.snapshotRelease = new Release(Version.of("2.2.0.SNAPSHOT"));
		this.releases = new TreeSet<>();
		this.releases.addAll(Arrays.asList(this.currentRelease, this.anotherRelease, this.snapshotRelease));
		this.springBoot.setReleases(this.releases);

		this.springData = new Project("spring-data", "Spring Data");
		this.springData.setGroups(new HashSet<>(Arrays.asList(pg2)));
		this.springData.setStackOverflowTags("spring-data,spring-data-commons");
		this.springDataJpa = new Project("spring-data-jpa", "Spring Data JPA");
		this.springDataJpa.setParentProject(this.springData);
		this.springData.setSubProjects(Arrays.asList(this.springDataJpa));

		GuideHeader[] guides = new GuideHeader[] {};
		given(this.projectGuidesRepo.findByProject(any())).willReturn(guides);
		given(this.projectTopicalRepo.findByProject(any())).willReturn(guides);
		given(this.projectTutorialRepo.findByProject(any())).willReturn(guides);

		given(projectMetadataService.fetchAllProjects()).willReturn(Arrays.asList(this.springBoot, this.springData));
		given(projectMetadataService.fetchFullProject("spring-boot")).willReturn(this.springBoot);
		given(projectMetadataService.fetchFullProject("spring-data")).willReturn(this.springData);
		given(projectMetadataService.fetchActiveProjectsTree()).willReturn(Arrays.asList(this.springBoot, this.springData));
		given(projectMetadataService.getAllGroups()).willReturn(this.projectGroups);
		given(projectMetadataService.fetchTopLevelProjectsWithGroups()).willReturn(Arrays.asList(this.springBoot, this.springData));
	}

	@Test
	public void showProjectModelHasProjectData() throws Exception {
		this.mvc.perform(get("/projects/spring-boot"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("project", this.springBoot))
				.andExpect(model().attribute("projects", Matchers.contains(this.springBoot, this.springData)))
				.andExpect(model().attribute("projectStackOverflow", "https://stackoverflow.com/questions/tagged/spring-boot"));
	}

	@Test
	public void showProjectHasStackOverflowLink() throws Exception {
		this.mvc.perform(get("/projects/spring-data"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("project", this.springData))
				.andExpect(model().attribute("projects", Matchers.contains(this.springBoot, this.springData)))
				.andExpect(model().attribute("projectStackOverflow", "https://stackoverflow.com/questions/tagged/spring-data+or+spring-data-commons"));
	}

	@Test
	public void showProjectHasReleases() throws Exception {
		this.mvc.perform(get("/projects/spring-boot"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("currentRelease", Optional.of(this.currentRelease)))
				.andExpect(model().attribute("otherReleases", Matchers.hasItems(this.anotherRelease, this.snapshotRelease)));
	}

	@Test
	public void showProjectWithoutReleases() throws Exception {
		Project projectWithoutReleases = new Project("spring-norelease", "spring-norelease");
		when(projectMetadataService.fetchFullProject("spring-norelease")).thenReturn(projectWithoutReleases);

		this.mvc.perform(get("/projects/spring-norelease"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("currentRelease", Optional.empty()))
				.andExpect(model().attribute("otherReleases", Matchers.empty()));
	}

	@Test
	public void listProjectsProvidesProjectMetadata() throws Exception {
		this.mvc.perform(get("/projects"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("groups", this.projectGroups))
				.andExpect(model().attribute("featured", Matchers.contains(this.springBoot)))
		;
	}

}