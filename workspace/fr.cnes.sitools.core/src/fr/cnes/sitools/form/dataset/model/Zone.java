package fr.cnes.sitools.form.dataset.model;

import java.util.List;

import fr.cnes.sitools.form.model.AbstractParameter;

public class Zone {
  
  
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
   * The list of parameters in the zone
   */
  private List<AbstractParameter> params;
  


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCss() {
    return css;
  }

  public void setCss(String css) {
    this.css = css;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public List<AbstractParameter> getParams() {
    return params;
  }

  public void setParams(List<AbstractParameter> params) {
    this.params = params;
  }


}
