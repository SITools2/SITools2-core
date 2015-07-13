package fr.cnes.sitools.form.dataset.dto;

import java.util.List;

/**
 * A DTO for form zone
 * 
 * @author b.fiorito
 */
public final class ZoneDTO {

  /**
   * Zone Id
   */
  private String id;

  /**
   * Zone title
   */
  private String title;

  /**
   * Associated CSS
   */
  private String css;

  /**
   * Zone Width
   */
  private int width;

  /**
   * Zone height
   */
  private int height;

  /**
   * Zone position in parent form
   */
  private int position;

  /**
   * Zone collapsible property
   */
  private boolean collapsible;

  /**
   * Zone collapsed at startup
   */
  private boolean collapsed;

  /**
   * Comment for <code>parametersDTO</code>
   */
  private List<ParameterDTO> params;

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
   * Gets the title value
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the value of title
   * 
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the css value
   * 
   * @return the css
   */
  public String getCss() {
    return css;
  }

  /**
   * Sets the value of css
   * 
   * @param css
   *          the css to set
   */
  public void setCss(String css) {
    this.css = css;
  }

  /**
   * Gets the width value
   * 
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the value of width
   * 
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Gets the height value
   * 
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Sets the value of height
   * 
   * @param height
   *          the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Gets the position value
   * 
   * @return the position
   */
  public int getPosition() {
    return position;
  }

  /**
   * Sets the value of position
   * 
   * @param position
   *          the position to set
   */
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * Gets the collapsible value
   * 
   * @return the collapsible
   */
  public boolean isCollapsible() {
    return collapsible;
  }

  /**
   * Sets the value of collapsible
   * 
   * @param collapsible
   *          the collapsible to set
   */
  public void setCollapsible(boolean collapsible) {
    this.collapsible = collapsible;
  }

  /**
   * Gets the params value
   * 
   * @return the params
   */
  public List<ParameterDTO> getParams() {
    return params;
  }

  /**
   * Sets the value of params
   * 
   * @param params
   *          the params to set
   */
  public void setParams(List<ParameterDTO> params) {
    this.params = params;
  }

  /**
   * Gets the collapsed value
   * 
   * @return the collapsed
   */
  public boolean isCollapsed() {
    return collapsed;
  }

  /**
   * Sets the value of collapsed
   * 
   * @param collapsed
   *          the collapsed to set
   */
  public void setCollapsed(boolean collapsed) {
    this.collapsed = collapsed;
  }
}
