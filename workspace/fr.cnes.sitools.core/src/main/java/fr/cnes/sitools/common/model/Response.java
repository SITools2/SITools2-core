     /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Generic response class for XML/JSON serialization.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("response")
public final class Response implements Serializable {

  /** serialVersionUID. */
  @XStreamOmitField
  private static final long serialVersionUID = 4061612957303187030L;

  /** Status of the response. */
  private Boolean success = null;

  /** Number total of items. */
  private Integer total = null;

  /** Message. */
  private String message = null;

  /** Array of items. */
  private ArrayList<Object> data = null;

  /** One item. */
  private Object item = null;

  /** Class of Object item or ArrayList<Object>. */
  @XStreamOmitField
  private Class<?> itemClass = null;

  /** Alias of item. */
  @XStreamOmitField
  private String itemName = "data"; // "item" ou null ?

  /** Url of the request Only for test ? */
  private String url = null;

  /** The pagination number */
  private Integer count = null;

  /** The offset number */
  private Integer offset = null;

  /**
   * Constructor with array data.
   * 
   * @param succ
   *          set the response success
   * @param mess
   *          response message
   * @param dat
   *          the data sent
   * @param objectClass
   *          the class of the object sent
   */
  public Response(Boolean succ, String mess, Object[] dat, Class<?> objectClass) {
    super();
    this.success = succ;
    this.message = mess;
    this.itemClass = objectClass;
    this.data = new ArrayList<Object>(Arrays.asList(dat));
    this.total = this.data.size();
  }

  /**
   * Constructor with array data.
   * 
   * @param succ
   *          set the response success
   * @param mess
   *          response message
   * @param dat
   *          the data sent
   * @param objectClass
   *          the class of the object sent
   * @param objectName
   *          the name of the object
   */
  public Response(Boolean succ, String mess, Object[] dat, Class<?> objectClass, String objectName) {
    super();
    this.success = succ;
    this.message = mess;
    this.itemClass = objectClass;
    this.itemName = objectName;
    this.data = new ArrayList<Object>(Arrays.asList(dat));
    this.total = this.data.size();
  }

  /**
   * Constructor
   * 
   * @param succ
   *          set the response success
   * @param mess
   *          response message
   */
  public Response(Boolean succ, String mess) {
    super();
    this.success = succ;
    this.message = mess;
  }

  /**
   * Constructor.
   * 
   * @param succ
   *          set the response success
   * @param dat
   *          the data sent
   */
  public Response(Boolean succ, Object[] dat) {
    super();
    this.success = succ;
    this.data = new ArrayList<Object>(Arrays.asList(dat));
    this.total = this.data.size();
  }

  /**
   * Constructor.
   * 
   * @param succ
   *          set the response success
   * @param dat
   *          the data sent
   * @param objectClass
   *          the class of the object sent
   */
  public Response(Boolean succ, Object[] dat, Class<?> objectClass) {
    super();
    this.success = succ;
    this.data = new ArrayList<Object>(Arrays.asList(dat));
    this.total = this.data.size();
    this.itemClass = objectClass;
  }

  /**
   * Constructor.
   * 
   * @param succ
   *          set the response success
   * @param dat
   *          the data sent
   * @param objectClass
   *          the class of the object sent
   * @param objectName
   *          the name of the object
   */
  public Response(Boolean succ, Object[] dat, Class<?> objectClass, String objectName) {
    super();
    this.success = succ;
    this.data = new ArrayList<Object>(Arrays.asList(dat));
    this.total = this.data.size();
    this.itemClass = objectClass;
    this.itemName = objectName;
  }

  // =======================================================================
  // Object > response.item or ArrayList > response.data

  /**
   * Constructor.
   * 
   * @param succ
   *          set the response success
   * @param itm
   *          the item sent
   * @param objectClass
   *          the class of the object sent
   * @param objectName
   *          the name of the object
   */
  @SuppressWarnings("unchecked")
  public Response(Boolean succ, Object itm, Class<?> objectClass, String objectName) {
    super();
    this.success = succ;
    if (itm instanceof ArrayList) {
      this.data = (ArrayList<Object>) itm;
      this.total = this.data.size();
    }
    else {
      this.item = itm;
    }
    this.itemClass = objectClass;
    this.itemName = objectName;
  }

  /**
   * Default constructor.
   */
  public Response() {
    super();
  }

  /**
   * Constructor with default itemName.
   * 
   * @param succ
   *          set the response success
   * @param itm
   *          the item sent
   * @param objectClass
   *          the class of the object sent
   */
  @SuppressWarnings("unchecked")
  public Response(Boolean succ, Object itm, Class<?> objectClass) {
    super();
    this.success = succ;
    if (itm instanceof ArrayList) {
      this.data = (ArrayList<Object>) itm;
      this.total = this.data.size();
    }
    else {
      this.item = itm;
    }
    this.itemClass = objectClass;
  }

  // ====================================================================
  // GETTERS / SETTERS

  /**
   * Gets the success value.
   * 
   * @return the success
   */
  public Boolean getSuccess() {
    return success;
  }

  /**
   * Gets the success value.
   * 
   * @return the success
   */
  @JsonIgnore
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the value of success.
   * 
   * @param succ
   *          the success to set
   */
  public void setSuccess(Boolean succ) {
    this.success = succ;
  }

  /**
   * Gets the total value.
   * 
   * @return the total
   */
  public Integer getTotal() {
    return total;
  }

  /**
   * Sets the value of total.
   * 
   * @param tot
   *          the total to set
   */
  public void setTotal(Integer tot) {
    this.total = tot;
  }

  /**
   * Gets the message value.
   * 
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the value of message.
   * 
   * @param mess
   *          the message to set
   */
  public void setMessage(String mess) {
    this.message = mess;
  }

  /**
   * Gets the data value.
   * 
   * @return the data
   */
  public ArrayList<Object> getData() {
    return data;
  }

  /**
   * Sets the value of data.
   * 
   * @param dat
   *          the data to set
   */
  public void setData(ArrayList<Object> dat) {
    this.data = dat;
  }

  /**
   * Gets the item value.
   * 
   * @return the item
   */
  public Object getItem() {
    return item;
  }

  /**
   * Sets the value of item.
   * 
   * @param itm
   *          the item to set
   */
  public void setItem(Object itm) {
    this.item = itm;
  }

  /**
   * Gets the itemClass value.
   * 
   * @return the itemClass
   */
  public Class<?> getItemClass() {
    return itemClass;
  }

  /**
   * Sets the value of itemClass.
   * 
   * @param itemCls
   *          the itemClass to set
   */
  public void setItemClass(Class<?> itemCls) {
    this.itemClass = itemCls;
  }

  /**
   * Gets the URL value.
   * 
   * @return the URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of URL.
   * 
   * @param urlToSet
   *          the URL to set
   */
  public void setUrl(String urlToSet) {
    this.url = urlToSet;
  }

  /**
   * Gets the itemName value.
   * 
   * @return the itemName
   */
  public String getItemName() {
    return itemName;
  }

  /**
   * Sets the value of itemName.
   * 
   * @param itmName
   *          the itemName to set
   */
  public void setItemName(String itmName) {
    this.itemName = itmName;
  }

  /**
   * Gets the count value
   * 
   * @return the count
   */
  public Integer getCount() {
    return count;
  }

  /**
   * Sets the value of count
   * 
   * @param count
   *          the count to set
   */
  public void setCount(Integer count) {
    this.count = count;
  }

  /**
   * Gets the offset value
   * 
   * @return the offset
   */
  public Integer getOffset() {
    return offset;
  }

  /**
   * Sets the value of offset
   * 
   * @param offset
   *          the offset to set
   */
  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  // ====================================================================

  @Override
  public String toString() {
    String result = "Response object - Success:" + isSuccess();
    if (itemClass != null) {
      result += (" ItemClass:" + itemClass.getName());
    }
    if (itemName != null) {
      result += (" ItemName:" + itemName);
    }
    if (total != null) {
      result += (" Total: " + total);
    }
    if (message != null) {
      result += ("\nMessage: " + message);
    }
    if (url != null) {
      result += ("\nUrl: " + url);
    }

    return result;
  }

}
