package fr.cnes.sitools.client.model;

/**
 * DTO to store build and version information
 * 
 * 
 * @author m.gond
 */
public class VersionBuildDateDTO {
  /** The version */
  private String version;
  /** The build date */
  private String buildDate;

  /**
   * Gets the version value
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the value of version
   * 
   * @param version
   *          the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the buildDate value
   * 
   * @return the buildDate
   */
  public String getBuildDate() {
    return buildDate;
  }

  /**
   * Sets the value of buildDate
   * 
   * @param buildDate
   *          the buildDate to set
   */
  public void setBuildDate(String buildDate) {
    this.buildDate = buildDate;
  }

}
