package fr.cnes.sitools.project.model;

import java.util.List;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.util.Property;

/**
 * The configuration of a projectModule in a Project. 
 *
 * @author d.arpin
 */
public class ProjectModule extends Resource {
  
  /** uuid   */
  private static final long serialVersionUID = -3850388533685437006L;

  /** priority */
  private Integer priority; // global ordering of all modules...

  /** CategoryModule */
  private String categoryModule; 
  
  /** divIdToDisplay */
  private String divIdToDisplay; 

  /** the list of roles */
  private List<Role> listRoles;
  
  /** the xtype **/
  private String xtype;
  
  /**the list of modules parameters **/
  private List<Property> listProjectModulesConfig;
  
  /** The label of the project */
  private String label;

  
  /**
   * Get the roles List
   * @return List<Role>
   */
  public List<Role> getListRoles() {
    return listRoles;
  }

  /**
   * set the roles list
   * @param listRoles the list roles
   */
  public void setListRoles(List<Role> listRoles) {
    this.listRoles = listRoles;
  }

  /**
   * Gets the priority value
   * 
   * @return the priority
   */
  public Integer getPriority() {
    return priority;
  }


  /**
   * Sets the value of priority
   * 
   * @param priority
   *          the priority to set
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }
  /**
   * Gets the category value
   * 
   * @return the category
   */
  public String getCategoryModule() {
    return categoryModule;
  }


  /**
   * Sets the value of divIdToDisplay
   * 
   * @param divIdToDisplay
   *          the categoryModule to set
   */
  public void setDivIdToDisplay(String divIdToDisplay) {
    this.divIdToDisplay = divIdToDisplay;
  }
  /**
   * Gets the divIdToDisplay value
   * 
   * @return the divIdToDisplay
   */
  public String getDivIdToDisplay() {
    return divIdToDisplay;
  }


  /**
   * Sets the value of category
   * 
   * @param categoryModule
   *          the categoryModule to set
   */
  public void setCategoryModule(String categoryModule) {
    this.categoryModule = categoryModule;
  }

  public String getXtype() {
    return xtype;
  }

  public void setXtype(String xtype) {
    this.xtype = xtype;
  }

  public List<Property> getListProjectModulesConfig() {
    return listProjectModulesConfig;
  }

  public void setListProjectModulesConfig(List<Property> listProjectModulesConfig) {
    this.listProjectModulesConfig = listProjectModulesConfig;
  }

  /**
   * Gets the label value
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the value of label
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

}