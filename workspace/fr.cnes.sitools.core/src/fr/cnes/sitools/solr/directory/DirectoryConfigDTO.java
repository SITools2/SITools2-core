/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.solr.directory;

import java.io.Serializable;

import fr.cnes.sitools.solr.model.DataConfigDTO;

/**
 * DataImportHandler#FileListEntityProcessor Configuration
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 * @see http://wiki.apache.org/solr/DataImportHandler#FileListEntityProcessor
 */
public class DirectoryConfigDTO extends DataConfigDTO implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -7257755058842837435L;

  /**
   * (required) The Base directory (absolute path)
   */
  private String baseDir;

  /**
   * (required) A regex pattern to identify files
   */
  private String fileName = ".*\\.(DOC)|(PDF)|(pdf)|(doc)|(docx)|(ppt)";

  /**
   * A date param . Use the format (yyyy-MM-dd HH:mm:ss) . <b/> It can also be a datemath string eg: ('NOW-3DAYS'). <b/>
   * The single quote is necessary . <b/> Or it can be a valid variableresolver format like (${var.name})
   */
  private String newerThan = "1900-01-01 00:00:00";

  /**
   * A date param . Same rules as above
   */
  private String olderThan = "'NOW'";

  /**
   * A int param.
   */
  private String biggerThan;

  /**
   * A int param.
   */
  private String smallerThan;

  /**
   * Constructor
   */
  public DirectoryConfigDTO() {
    super();
  }

  /**
   * Gets the baseDir value
   * 
   * @return the baseDir
   */
  public String getBaseDir() {
    return baseDir;
  }

  /**
   * Sets the value of baseDir
   * 
   * @param baseDir
   *          the baseDir to set
   */
  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  /**
   * Gets the fileName value
   * 
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the value of fileName
   * 
   * @param fileName
   *          the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Gets the newerThan value
   * 
   * @return the newerThan
   */
  public String getNewerThan() {
    return newerThan;
  }

  /**
   * Sets the value of newerThan
   * 
   * @param newerThan
   *          the newerThan to set
   */
  public void setNewerThan(String newerThan) {
    this.newerThan = newerThan;
  }

  /**
   * Gets the smallerThan value
   * 
   * @return the smallerThan
   */
  public String getSmallerThan() {
    return smallerThan;
  }

  /**
   * Sets the value of smallerThan
   * 
   * @param smallerThan
   *          the smallerThan to set
   */
  public void setSmallerThan(String smallerThan) {
    this.smallerThan = smallerThan;
  }

  /**
   * Gets the olderThan value
   * 
   * @return the olderThan
   */
  public String getOlderThan() {
    return olderThan;
  }

  /**
   * Sets the value of olderThan
   * 
   * @param olderThan
   *          the olderThan to set
   */
  public void setOlderThan(String olderThan) {
    this.olderThan = olderThan;
  }

  /**
   * Gets the biggerThan value
   * 
   * @return the biggerThan
   */
  public String getBiggerThan() {
    return biggerThan;
  }

  /**
   * Sets the value of biggerThan
   * 
   * @param biggerThan
   *          the biggerThan to set
   */
  public void setBiggerThan(String biggerThan) {
    this.biggerThan = biggerThan;
  }

}
