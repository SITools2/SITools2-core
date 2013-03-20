package primitive;

public class Element {
	private String xpath;
	private String id;
	
	public Element(String id) {
		super();
		this.id = id;
		this.xpath = SEL.locById(id);
	}

	public Element(String id, String xpath) {
		super();
		if (this.id != null) {
			new Element(id);
		}
		else {
			this.xpath = xpath;
		}
	}

	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}

	/**
	 * @param xpath the xpath to set
	 */
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
}
