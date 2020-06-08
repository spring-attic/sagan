package sagan.site.projects.admin;

import java.util.ArrayList;
import java.util.List;

import sagan.site.projects.ReleaseStatus;
import sagan.site.projects.Repository;

public class ProjectFormReleases {

	private String id;

	private List<FormRelease> releases = new ArrayList<>();

	private FormRelease newRelease;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FormRelease> getReleases() {
		return releases;
	}

	public void setReleases(List<FormRelease> releases) {
		this.releases = releases;
	}

	public FormRelease getNewRelease() {
		return newRelease;
	}

	public void setNewRelease(FormRelease newRelease) {
		this.newRelease = newRelease;
	}

	public static class FormRelease {

		private Long id;

		private String version;

		private ReleaseStatus releaseStatus;

		private boolean toDelete;

		private Repository repository;

		private String refDocUrl;

		private String apiDocUrl;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public ReleaseStatus getReleaseStatus() {
			return releaseStatus;
		}

		public void setReleaseStatus(ReleaseStatus releaseStatus) {
			this.releaseStatus = releaseStatus;
		}

		public boolean isToDelete() {
			return toDelete;
		}

		public void setToDelete(boolean toDelete) {
			this.toDelete = toDelete;
		}

		public Repository getRepository() {
			return repository;
		}

		public void setRepository(Repository repository) {
			this.repository = repository;
		}

		public String getRefDocUrl() {
			return refDocUrl;
		}

		public void setRefDocUrl(String refDocUrl) {
			this.refDocUrl = refDocUrl;
		}

		public String getApiDocUrl() {
			return apiDocUrl;
		}

		public void setApiDocUrl(String apiDocUrl) {
			this.apiDocUrl = apiDocUrl;
		}
	}

}
