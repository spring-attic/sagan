package sagan.renderer.guides;

import org.springframework.hateoas.RepresentationModel;

/**
 * Spring guide content holder.
 */
public class GuideContentModel extends RepresentationModel {

	private String name;

	private String tableOfContents;

	private String content;

	GuideContentModel(String name, String content, String tableOfContents) {
		this.name = name;
		this.content = content;
		this.tableOfContents = tableOfContents;
	}

	GuideContentModel() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableOfContents() {
		return this.tableOfContents;
	}

	public void setTableOfContents(String tableOfContents) {
		this.tableOfContents = tableOfContents;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}