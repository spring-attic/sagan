package sagan.renderer.guides;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

/**
 * Spring guide content holder.
 */
public class GuideContentResource extends ResourceSupport {

	private String name;

	private String tableOfContents;

	private String content;

	private String pushToPwsMetadata;

	private List<GuideImage> images;

	GuideContentResource(String name, String content, String tableOfContents) {
		this.name = name;
		this.content = content;
		this.tableOfContents = tableOfContents;
	}

	GuideContentResource() { }

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

	public String getPushToPwsMetadata() {
		return this.pushToPwsMetadata;
	}

	public void setPushToPwsMetadata(String pushToPwsMetadata) {
		this.pushToPwsMetadata = pushToPwsMetadata;
	}

	public List<GuideImage> getImages() {
		return this.images;
	}

	public void setImages(List<GuideImage> images) {
		this.images = images;
	}

}