     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class to define a predicate = WHERE clause line
 * 
 * A WHERE clause is a list of predicates
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("predicat")
public final class Predicat {

  /**
   * The index
   */
  private int indice;
  /**
   * The nbClosedParanthesis
   */
  private int nbClosedParanthesis = 0;
  /**
   * The closedParenthesis
   */
  private String closedParenthesis = null;
  /**
   * The rightValue
   */
  private Object rightValue;
  /**
   * The nbOpenedParanthesis
   */
  private int nbOpenedParanthesis = 0;
  /**
   * The openParenthesis
   */
  private String openParenthesis = null;
  /**
   * The logicOperator
   */
  private String logicOperator;
  /**
   * The compareOperator
   */
  private Operator compareOperator;

  /**
   * The leftString
   */
  private String leftString;
  /**
   * The rightAttribute
   */
  private Column rightAttribute;
  /**
   * The leftAttribute
   */
  private Column leftAttribute;

  /**
   * The entire stringDefinition
   */
  private String stringDefinition;
  /**
   * The wildcard to apply on the right value
   */
  private Wildcard wildcard;

  /**
   * Default constructor
   */
  public Predicat() {
    super();
  }

  /**
   * Gets the index value
   * 
   * @return the index
   */
  public int getIndice() {
    return indice;
  }

  /**
   * Sets the value of index
   * 
   * @param indice
   *          the index to set
   */
  public void setIndice(int indice) {
    this.indice = indice;
  }

  /**
   * Gets the nbClosedParanthesis value
   * 
   * @return the nbClosedParanthesis
   */
  public int getNbClosedParanthesis() {
    return nbClosedParanthesis;
  }

  /**
   * Sets the value of nbClosedParanthesis
   * 
   * @param nbClosedParanthesis
   *          the nbClosedParanthesis to set
   */
  public void setNbClosedParanthesis(int nbClosedParanthesis) {
    this.nbClosedParanthesis = nbClosedParanthesis;
  }

  /**
   * Gets the rightValue value
   * 
   * @return the rightValue
   */
  public Object getRightValue() {
    return rightValue;
  }

  /**
   * Sets the value of rightValue
   * 
   * @param rightValue
   *          the rightValue to set
   */
  public void setRightValue(Object rightValue) {
    this.rightValue = rightValue;
  }

  /**
   * Gets the nbOpenedParanthesis value
   * 
   * @return the nbOpenedParanthesis
   */
  public int getNbOpenedParanthesis() {
    return nbOpenedParanthesis;
  }

  /**
   * Sets the value of nbOpenedParanthesis
   * 
   * @param nbOpenedParanthesis
   *          the nbOpenedParanthesis to set
   */
  public void setNbOpenedParanthesis(int nbOpenedParanthesis) {
    this.nbOpenedParanthesis = nbOpenedParanthesis;
  }

  /**
   * Gets the logicOperator value
   * 
   * @return the logicOperator
   */
  public String getLogicOperator() {
    return logicOperator;
  }

  /**
   * Sets the value of logicOperator
   * 
   * @param logicOperator
   *          the logicOperator to set
   */
  public void setLogicOperator(String logicOperator) {
    this.logicOperator = logicOperator;
  }

  /**
   * Gets the compareOperator value
   * 
   * @return the compareOperator
   */
  public Operator getCompareOperator() {
    return compareOperator;
  }

  /**
   * Sets the value of compareOperator
   * 
   * @param compareOperator
   *          the compareOperator to set
   */
  public void setCompareOperator(Operator compareOperator) {
    this.compareOperator = compareOperator;
  }

  /**
   * Gets the rightAttribute value
   * 
   * @return the rightAttribute
   */
  public Column getRightAttribute() {
    return rightAttribute;
  }

  /**
   * Sets the value of rightAttribute
   * 
   * @param rightAttribute
   *          the rightAttribute to set
   */
  public void setRightAttribute(Column rightAttribute) {
    this.rightAttribute = rightAttribute;
  }

  /**
   * Gets the leftAttribute value
   * 
   * @return the leftAttribute
   */
  public Column getLeftAttribute() {
    return leftAttribute;
  }

  /**
   * Sets the value of leftAttribute
   * 
   * @deprecated use column instead.
   * @param leftAttribute
   *          the leftAttribute to set
   */
  public void setLeftAttributeString(String leftAttribute) {
    this.leftString = leftAttribute;
  }

  /**
   * Gets the leftAttribute value
   * 
   * @deprecated use column instead.
   * @return the leftAttribute
   */
  public String getLeftAttributeString() {
    return leftString;
  }

  /**
   * Sets the value of leftAttribute
   * 
   * @param leftAttribute
   *          the leftAttribute to set
   */
  public void setLeftAttribute(Column leftAttribute) {
    this.leftAttribute = leftAttribute;
  }

  /**
   * Gets the closedParenthesis value
   * 
   * @return the closedParenthesis
   */
  public String getClosedParenthesis() {
    return closedParenthesis;
  }

  /**
   * Sets the value of closedParenthesis
   * 
   * @param closedParenthesis
   *          the closedParenthesis to set
   */
  public void setClosedParenthesis(String closedParenthesis) {
    this.closedParenthesis = closedParenthesis;
  }

  /**
   * Gets the openParenthesis value
   * 
   * @return the openParenthesis
   */
  public String getOpenParenthesis() {
    return openParenthesis;
  }

  /**
   * Sets the value of openParenthesis
   * 
   * @param openParenthesis
   *          the openParenthesis to set
   */
  public void setOpenParenthesis(String openParenthesis) {
    this.openParenthesis = openParenthesis;
  }

  /**
   * Gets the leftString value
   * 
   * @return the leftString
   */
  public String getLeftString() {
    return leftString;
  }

  /**
   * Sets the value of leftString
   * 
   * @param leftString
   *          the leftString to set
   */
  public void setLeftString(String leftString) {
    this.leftString = leftString;
  }

  /**
   * Gets the stringDefinition value
   * 
   * @return the stringDefinition
   */
  public String getStringDefinition() {
    return stringDefinition;
  }

  /**
   * Sets the value of stringDefinition
   * 
   * @param stringDefinition
   *          the stringDefinition to set
   */
  public void setStringDefinition(String stringDefinition) {
    this.stringDefinition = stringDefinition;
  }

  /**
   * Gets the wildcard value
   * 
   * @return the wildcard
   */
  public Wildcard getWildcard() {
    return wildcard;
  }

  /**
   * Sets the value of wildcard
   * 
   * @param wildcard
   *          the wildcard to set
   */
  public void setWildcard(Wildcard wildcard) {
    this.wildcard = wildcard;
  }

}
