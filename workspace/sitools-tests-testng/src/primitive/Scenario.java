package primitive;

public class Scenario {

	private String className;

	@Override
	public String toString() {
		return "className: " + className;
	}

	/**
	 * Get Class Name to run
	 * 
	 * @return class name
	 */
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
