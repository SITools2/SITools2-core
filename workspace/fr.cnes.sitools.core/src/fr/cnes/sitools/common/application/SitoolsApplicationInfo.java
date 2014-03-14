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
package fr.cnes.sitools.common.application;

/**
 * Class for application description
 *
 * @author jp.boignard (AKKA Technologies)
 */
public final class SitoolsApplicationInfo {

  /** Signature */
  private String comment = null;

  /** Version */
  private String version = null;
  
  /** Organism */
  private String organism = null;

  /** Mail */
  private String mail = null;

  /**
   * Constructor
   */
  public SitoolsApplicationInfo() {
    super();
  }

  /**
   * Gets the comment value
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the value of comment
   * @param comment the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Gets the version value
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the value of version
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the organism value
   * @return the organism
   */
  public String getOrganism() {
    return organism;
  }

  /**
   * Sets the value of organism
   * @param organism the organism to set
   */
  public void setOrganism(String organism) {
    this.organism = organism;
  }

  /**
   * Gets the mail value
   * @return the mail
   */
  public String getMail() {
    return mail;
  }

  /**
   * Sets the value of mail
   * @param mail the mail to set
   */
  public void setMail(String mail) {
    this.mail = mail;
  }

}
