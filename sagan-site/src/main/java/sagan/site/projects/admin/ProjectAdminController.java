package sagan.site.projects.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import sagan.site.projects.Project;
import sagan.site.projects.ProjectGeneration;
import sagan.site.projects.ProjectGroup;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.projects.ProjectSample;
import sagan.site.projects.Release;
import sagan.site.projects.SupportStatus;
import sagan.site.projects.Version;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that handles administrative actions for Spring project metadata, e.g. adding
 * new releases, updating documentation urls, etc. Per rules in
 * {@code sagan.SecurityConfig}, authentication is required for all requests. See
 * {@code ProjectsController} for public, read-only operations.
 */
@Controller
@RequestMapping("/admin/projects")
class ProjectAdminController {

	private final ProjectMetadataService service;

	private final ModelMapper modelMapper;

	public ProjectAdminController(ProjectMetadataService service, ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;
		modelMapper.addConverter(new AbstractConverter<String, Project>() {
			protected Project convert(String projectId) {
				return projectId == null ? null : service.fetchFullProject(projectId);
			}
		});
		modelMapper.addConverter(new AbstractConverter<LocalDate, String>() {
			@Override
			protected String convert(LocalDate source) {
				return source != null ? source.format(DateTimeFormatter.ISO_DATE) : null;
			}
		});
		modelMapper.addConverter(new AbstractConverter<String, LocalDate>() {
			@Override
			protected LocalDate convert(String source) {
				return StringUtils.hasText(source) ? LocalDate.parse(source) : null;
			}
		});
		modelMapper.addConverter(new AbstractConverter<String, ProjectGroup>() {
			protected ProjectGroup convert(String groupName) {
				return StringUtils.hasText(groupName) ? service.findGroup(groupName) : null;
			}
		});
		modelMapper.addConverter(new AbstractConverter<ProjectGroup, String>() {
			protected String convert(ProjectGroup group) {
				return group == null ? null : group.getName();
			}
		});
		modelMapper.createTypeMap(ProjectFormReleases.FormRelease.class, Release.class).addMappings(mapper -> {
			mapper.skip(Release::setId);
		});
		modelMapper.createTypeMap(ProjectFormGenerations.FormGeneration.class, ProjectGeneration.class).addMappings(mapper -> {
			mapper.skip(ProjectGeneration::setId);
		});
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("projects", this.service.fetchAllProjects());
		return "admin/project/index";
	}

	@GetMapping("/new")
	public String newProject(Model model) {
		ProjectFormMetadata project = new ProjectFormMetadata("spring-new", "New Spring Project");
		model.addAttribute("project", project);
		model.addAttribute("statusList", SupportStatus.values());
		return "admin/project/new";
	}

	@PostMapping("/new")
	public String createProject(@Valid ProjectFormMetadata projectMetadata, Model model) {
		Project project = new Project(projectMetadata.getId(), projectMetadata.getName());
		this.modelMapper.map(projectMetadata, project);
		this.service.save(project);
		return "redirect:" + project.getId();
	}

	@GetMapping("/{id}")
	public String editMetadata(@PathVariable String id, Model model) {
		Project project = this.service.fetchFullProject(id);
		model.addAttribute("project", this.modelMapper.map(project, ProjectFormMetadata.class));
		model.addAttribute("statusList", SupportStatus.values());
		return "admin/project/edit-metadata";
	}

	@PostMapping("/{id}")
	public String saveMetadata(@Valid ProjectFormMetadata projectMetadata, Model model) {
		Project project = this.service.fetchFullProject(projectMetadata.getId());
		this.modelMapper.map(projectMetadata, project);
		this.service.save(project);
		return "redirect:" + project.getId();
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable String id, Model model) {
		this.service.delete(id);
		return "redirect:/admin/projects";
	}

	@GetMapping("/{id}/info")
	public String editInfo(@PathVariable String id, Model model) {
		Project project = this.service.fetchFullProject(id);
		model.addAttribute("project", this.modelMapper.map(project, ProjectFormInfo.class));
		model.addAttribute("groups", this.service.getAllGroups());
		return "admin/project/edit-info";
	}

	@PostMapping("/{id}/info")
	public String saveInfo(@Valid ProjectFormInfo projectInfo, Model model) {
		Project project = this.service.fetchFullProject(projectInfo.getId());
		this.modelMapper.map(projectInfo, project);
		this.service.save(project);
		return "redirect:info";
	}

	@GetMapping("/{id}/releases")
	public String editReleases(@PathVariable String id, Model model) {
		Project project = this.service.fetchFullProject(id);
		ProjectFormReleases form = this.modelMapper.map(project, ProjectFormReleases.class);
		form.getNewRelease().setApiDocUrl("https://docs.spring.io/" + id + "/docs/{version}/api/");
		form.getNewRelease().setRefDocUrl("https://docs.spring.io/" + id + "/docs/{version}/reference/html/");
		model.addAttribute("project", form);
		return "admin/project/edit-releases";
	}

	@PostMapping("/{id}/releases")
	public String saveReleases(@Valid ProjectFormReleases formReleases, Model model) {
		Project project = this.service.fetchFullProject(formReleases.getId());
		formReleases.getReleases().forEach(formRelease -> {
			Version version = Version.of(formRelease.getVersion());
			if (formRelease.isToDelete()) {
				project.removeRelease(version);
			}
			else {
				project.findRelease(version).ifPresent(release -> {
					this.modelMapper.map(formRelease, release);
				});
			}
		});
		ProjectFormReleases.FormRelease newFormRelease = formReleases.getNewRelease();
		if (StringUtils.hasText(newFormRelease.getVersion())) {
			Release newRelease = project.addRelease(Version.of(newFormRelease.getVersion()));
			this.modelMapper.map(newFormRelease, newRelease);
		}
		this.service.save(project);
		return "redirect:releases";
	}

	@GetMapping("/{id}/support")
	public String editSupport(@PathVariable String id, Model model) {
		Project project = this.service.fetchFullProject(id);
		model.addAttribute("project", this.modelMapper.map(project, ProjectFormGenerations.class));
		return "admin/project/edit-support";
	}

	@PostMapping("/{id}/support")
	public String saveSupport(@Valid ProjectFormGenerations formGenerations, Model model) {
		Project project = this.service.fetchFullProject(formGenerations.getId());
		formGenerations.getGenerations().forEach(formGeneration -> {
			if (formGeneration.isToDelete()) {
				project.removeGeneration(formGeneration.getName());
			}
			else {
				project.findGeneration(formGeneration.getName()).ifPresent(generation -> {
					this.modelMapper.map(formGeneration, generation);
				});
			}
		});
		ProjectFormGenerations.FormGeneration newFormGeneration = formGenerations.getNewGeneration();
		if (StringUtils.hasText(newFormGeneration.getName())) {
			ProjectGeneration newGen = project.addGeneration(newFormGeneration.getName(), LocalDate.parse(newFormGeneration.getInitialReleaseDate(), DateTimeFormatter.ISO_DATE));
			this.modelMapper.map(newFormGeneration, newGen);
		}
		this.service.save(project);
		return "redirect:support";
	}

	@GetMapping("/{id}/samples")
	public String editSamples(@PathVariable String id, Model model) {
		Project project = this.service.fetchFullProject(id);
		model.addAttribute("project", this.modelMapper.map(project, ProjectFormSamples.class));
		return "admin/project/edit-samples";
	}

	@PostMapping("/{id}/samples")
	public String saveSamples(@Valid ProjectFormSamples formSamples, Model model) {
		Project project = this.service.fetchFullProject(formSamples.getId());

		formSamples.getSamples().forEach(formSample -> {
			if (formSample.isToDelete()) {
				project.getSamples().stream()
						.filter(sample -> sample.getId().equals(formSample.getId()))
						.findFirst().ifPresent(project::removeSample);
			}
			else {
				project.getSamples().stream().filter(sample -> sample.getId().equals(formSample.getId()))
						.findFirst().ifPresent(sample -> {
					sample.setTitle(formSample.getTitle());
					sample.setUrl(formSample.getUrl());
					sample.setDescription(formSample.getDescription());
					sample.setSortOrder(formSample.getSortOrder());
				});
			}
		});
		ProjectFormSamples.FormSample newFormSample = formSamples.getNewSample();
		if (StringUtils.hasText(newFormSample.getTitle())) {
			ProjectSample newSample = project.addSample(newFormSample.getTitle(), newFormSample.getUrl());
			this.modelMapper.map(newFormSample, newSample);
		}
		this.service.save(project);
		return "redirect:samples";
	}

}
