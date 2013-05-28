package fr.cnes.sitools.plugins.guiservices.implement.model;

import java.util.List;

import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.util.Property;

/**
 * Model class for GuiService plugin on a dataset
 * 
 * 
 * @author m.gond
 */
public class GuiServicePluginModel extends GuiServiceModel {

  /** serialVersionUID */
  private static final long serialVersionUID = -5607479913913255555L;

  /**
   * Parent of the resource
   */
  private String parent;

  /** description of what the guiservice plugin does */
  private String descriptionAction;

  /** the gui service version, used only for client */
  private String currentGuiServiceVersion;

  /**
   * The list of parameters and their values
   */
  private List<Property> parameters;

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the descriptionAction value
   * 
   * @return the descriptionAction
   */
  public String getDescriptionAction() {
    return descriptionAction;
  }

  /**
   * Sets the value of descriptionAction
   * 
   * @param descriptionAction
   *          the descriptionAction to set
   */
  public void setDescriptionAction(String descriptionAction) {
    this.descriptionAction = descriptionAction;
  }

  /**
   * Gets the parameters value
   * 
   * @return the parameters
   */
  public List<Property> getParameters() {
    return parameters;
  }

  /**
   * Sets the value of parameters
   * 
   * @param parameters
   *          the parameters to set
   */
  public void setParameters(List<Property> parameters) {
    this.parameters = parameters;
  }

  /**
   * Gets the currentGuiServiceVersion value
   * @return the currentGuiServiceVersion
   */
  public String getCurrentGuiServiceVersion() {
    return currentGuiServiceVersion;
  }

  /**
   * Sets the value of currentGuiServiceVersion
   * @param currentGuiServiceVersion the currentGuiServiceVersion to set
   */
  public void setCurrentGuiServiceVersion(String currentGuiServiceVersion) {
    this.currentGuiServiceVersion = currentGuiServiceVersion;
  }

}
