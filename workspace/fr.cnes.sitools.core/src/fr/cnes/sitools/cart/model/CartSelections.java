package fr.cnes.sitools.cart.model;

import java.io.Serializable;
import java.util.List;

import fr.cnes.sitools.common.model.IResource;

public class CartSelections implements IResource, Serializable {
  
  
  /**
   * 
   */
  private static final long serialVersionUID = 6066827119241221999L;
  
  private List<CartSelection> selections;
  
  public CartSelections(){
  }

  public List<CartSelection> getSelections() {
    return selections;
  }

  public void setSelections(List<CartSelection> selections) {
    this.selections = selections;
  }

  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setId(String id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }
  
  

}
