package sagan.renderer.guides;

public class GuideImage {

	private String name;

	private String encodedContent;

	public GuideImage() {
	}

	public GuideImage(String name, String encodedContent) {
		this.name = name;
		this.encodedContent = encodedContent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEncodedContent() {
		return encodedContent;
	}

	public void setEncodedContent(String encodedContent) {
		this.encodedContent = encodedContent;
	}


}
