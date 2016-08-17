    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.login;

/**
 * Sitools authentifiaction Information
 * @author AKKA
 */
public final class SitoolsAuthenticationInfo {

  /** Realm */
  private String realm;
  
  /** Scheme */
  private String scheme;
  
  /** Nonce */
  private String nonce;
  
  /** Algorithm */
  private String algorithm;
  
  /**
   *  Constructor
   */
  public SitoolsAuthenticationInfo() {
    super();
  }
  
  /**
   * Gets the rEALM value
   * @return the rEALM
   */
  public String getREALM() {
    return realm;
  }
  /**
   * Sets the value of rEALM
   * @param rEALM the rEALM to set
   */
  public void setREALM(String rEALM) {
    realm = rEALM;
  }
  /**
   * Gets the sCHEME value
   * @return the sCHEME
   */
  public String getSCHEME() {
    return scheme;
  }
  /**
   * Sets the value of sCHEME
   * @param sCHEME the sCHEME to set
   */
  public void setSCHEME(String sCHEME) {
    scheme = sCHEME;
  }
  /**
   * Gets the nONCE value
   * @return the nONCE
   */
  public String getNONCE() {
    return nonce;
  }
  /**
   * Sets the value of nONCE
   * @param nONCE the nONCE to set
   */
  public void setNONCE(String nONCE) {
    nonce = nONCE;
  }
  /**
   * Gets the aLGORITHM value
   * @return the aLGORITHM
   */
  public String getALGORITHM() {
    return algorithm;
  }
  /**
   * Sets the value of aLGORITHM
   * @param aLGORITHM the aLGORITHM to set
   */
  public void setALGORITHM(String aLGORITHM) {
    algorithm = aLGORITHM;
  }
  
  
}
