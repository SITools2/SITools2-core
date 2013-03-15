package fr.cnes.sitools.project.modules.model;

import java.util.List;

/**
 * Class to represent the list of Project Module to send to the client
 * 
 * 
 * @author m.gond
 */
public class ListComponents {
  /**
   * List of ProjectModule
   */
  private List<ProjectModuleModel> components = null;

  /**
   * Gets the components value
   * 
   * @return the components
   */
  public List<ProjectModuleModel> getComponents() {
    return components;
  }

  /**
   * Sets the value of components
   * 
   * @param components
   *          the components to set
   */
  public void setComponents(List<ProjectModuleModel> components) {
    this.components = components;
  }

}
