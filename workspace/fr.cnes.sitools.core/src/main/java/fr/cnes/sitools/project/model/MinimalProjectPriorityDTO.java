package fr.cnes.sitools.project.model;

/**
 * Intermediary model class of ProjectPriorityDTO
 * 
 * @author b.fiorito (AKKA Technologies)
 * 
 * */
public class MinimalProjectPriorityDTO {

  /** Object identifier */
  private String id;

  /** Order to display projects in portal */
  private Integer priority;

  /** Categorize project in a portlet with this name */
  private String categoryProject;

  /**
   * Gets the id value
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the priority value
   * @return the priority
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Sets the value of priority
   * @param priority the priority to set
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  /**
   * Gets the categoryProject value
   * @return the categoryProject
   */
  public String getCategoryProject() {
    return categoryProject;
  }

  /**
   * Sets the value of categoryProject
   * @param categoryProject the categoryProject to set
   */
  public void setCategoryProject(String categoryProject) {
    this.categoryProject = categoryProject;
  }
  
}
