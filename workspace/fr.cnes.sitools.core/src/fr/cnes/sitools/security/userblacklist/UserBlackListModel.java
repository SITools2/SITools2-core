package fr.cnes.sitools.security.userblacklist;

import java.util.Date;

import fr.cnes.sitools.common.model.IResource;

public class UserBlackListModel implements IResource {
  /** The userblacklist identifier */
  private String id;
  /** The name of the user which is blacklisted */
  private String username;
  /** The IP address which blacklisted the user */
  private String ipAddress;
  /** The date when the user was blacklisted */
  private Date date;
  /** If the user exists in the database or not */
  private Boolean userExists;

  private String name;

  private String description;

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the username value
   * 
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the value of username
   * 
   * @param username
   *          the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the ip value
   * 
   * @return the ip
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * Sets the value of ip
   * 
   * @param ipAddress
   *          the ip to set
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * Gets the date value
   * 
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Sets the value of date
   * 
   * @param date
   *          the date to set
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Gets the userExists value
   * 
   * @return the userExists
   */
  public Boolean getUserExists() {
    return userExists;
  }

  /**
   * Sets the value of userExists
   * 
   * @param userExists
   *          the userExists to set
   */
  public void setUserExists(Boolean userExists) {
    this.userExists = userExists;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
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
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
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

}
