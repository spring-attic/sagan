package sagan.site.projects.admin;

import java.util.List;

public class ProjectFormSamples {

	private String id;

	private List<FormSample> samples;

	private FormSample newSample;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FormSample> getSamples() {
		return samples;
	}

	public void setSamples(List<FormSample> samples) {
		this.samples = samples;
	}

	public FormSample getNewSample() {
		return newSample;
	}

	public void setNewSample(FormSample newSample) {
		this.newSample = newSample;
	}

	public static class FormSample {

		private Long id;

		private boolean toDelete;

		private String title;

		private String description;

		private String url;

		private int sortOrder;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public boolean isToDelete() {
			return toDelete;
		}

		public void setToDelete(boolean toDelete) {
			this.toDelete = toDelete;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(int sortOrder) {
			this.sortOrder = sortOrder;
		}
	}
}
