package fr.cnes.sitools.dataset.services.model;

import java.util.List;

import fr.cnes.sitools.common.model.IResource;

/**
 * Model class to represent a collection of {@link ServiceModel} on a dataset.
 * 
 * 
 * 
 * @author m.gond
 */
public class ServiceCollectionModel implements IResource {

  /** The id */
  private String id;
  /** The name */
  private String name;
  /** The description */
  private String description;
  /** The list of services */
  private List<ServiceModel> services;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the services value
   * 
   * @return the services
   */
  public List<ServiceModel> getServices() {
    return services;
  }

  /**
   * Sets the value of services
   * 
   * @param services
   *          the services to set
   */
  public void setServices(List<ServiceModel> services) {
    this.services = services;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
