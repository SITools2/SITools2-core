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
package fr.cnes.sitools.client.model;

/**
 * DTO to store build and version information
 * 
 * 
 * @author m.gond
 */
public class VersionBuildDateDTO {
  /** The version */
  private String version;
  /** The build date */
  private String buildDate;
  /** The copyright */
  private String copyright;

  /**
   * Gets the version value
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the value of version
   * 
   * @param version
   *          the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the buildDate value
   * 
   * @return the buildDate
   */
  public String getBuildDate() {
    return buildDate;
  }

  /**
   * Sets the value of buildDate
   * 
   * @param buildDate
   *          the buildDate to set
   */
  public void setBuildDate(String buildDate) {
    this.buildDate = buildDate;
  }

  /**
   * Gets the copyright value
   * 
   * @return the copyright
   */
  public String getCopyright() {
    return copyright;
  }

  /**
   * Sets the value of copyright
   * 
   * @param copyright
   *          the copyright to set
   */
  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

}
