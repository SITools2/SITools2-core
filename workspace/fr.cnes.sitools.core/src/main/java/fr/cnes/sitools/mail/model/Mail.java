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
package fr.cnes.sitools.mail.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class pojo for an email in SITools2
 * Alternative of XML format used by default in restlet extension
 * org.restlet.ext.javamail
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class Mail implements Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = 9133012904229218655L;

  /** Object */
  private String subject;

  /** Author */
  private String from;

  /** Content */
  private String body;

  /** Sending date */
  private Date sentDate;

  /** Targets */
  private List<String> toList = new ArrayList<String>();

  /** Copies */
  private List<String> ccList = new ArrayList<String>();

  /** Masked copies */
  private List<String> bccList = new ArrayList<String>();

  /**
   * Default constructor
   */
  public Mail() {
    super();
  }

  /**
   * Gets the subject value
   * 
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the value of subject
   * 
   * @param subject
   *          the subject to set
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the from value
   * 
   * @return the from
   */
  public String getFrom() {
    return from;
  }

  /**
   * Sets the value of from
   * 
   * @param from
   *          the from to set
   */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * Gets the body value
   * 
   * @return the body
   */
  public String getBody() {
    return body;
  }

  /**
   * Sets the value of body
   * 
   * @param body
   *          the body to set
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * Gets the sentDate value
   * 
   * @return the sentDate
   */
  public Date getSentDate() {
    return sentDate;
  }

  /**
   * Sets the value of sentDate
   * 
   * @param sentDate
   *          the sentDate to set
   */
  public void setSentDate(Date sentDate) {
    this.sentDate = sentDate;
  }

  /**
   * Gets the toList value
   * 
   * @return the toList
   */
  public List<String> getToList() {
    return toList;
  }

  /**
   * Sets the value of toList
   * 
   * @param toList
   *          the toList to set
   */
  public void setToList(List<String> toList) {
    this.toList = toList;
  }

  /**
   * Gets the ccList value
   * 
   * @return the ccList
   */
  public List<String> getCcList() {
    return ccList;
  }

  /**
   * Sets the value of ccList
   * 
   * @param ccList
   *          the ccList to set
   */
  public void setCcList(List<String> ccList) {
    this.ccList = ccList;
  }

  /**
   * Gets the bccList value
   * 
   * @return the bccList
   */
  public List<String> getBccList() {
    return bccList;
  }

  /**
   * Sets the value of bccList
   * 
   * @param bccList
   *          the bccList to set
   */
  public void setBccList(List<String> bccList) {
    this.bccList = bccList;
  }
}
