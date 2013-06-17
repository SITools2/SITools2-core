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
package fr.cnes.sitools.persistence;

import java.io.File;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;

/**
 * Sitools file persistence strategy
 * 
 * @author AKKA
 * 
 */
public final class SitoolsFilePersistenceStrategy extends FilePersistenceStrategy {

  /**
   * Constructor
   * 
   * @param baseDirectory
   *          base directory
   * @param xstream
   *          xstream to save
   * @param encoding
   *          encoding used
   * @param illegalChars
   *          illegal characters
   */
  public SitoolsFilePersistenceStrategy(File baseDirectory, XStream xstream, String encoding, String illegalChars) {
    super(baseDirectory, xstream, encoding, illegalChars);
  }

  /**
   * Constructor
   * 
   * @param baseDirectory
   *          base directory
   * @param xstream
   *          xstream to save
   */
  public SitoolsFilePersistenceStrategy(File baseDirectory, XStream xstream) {
    super(baseDirectory, xstream);
  }

  /**
   * Constructor
   * 
   * @param baseDirectory
   *          base directory
   */
  public SitoolsFilePersistenceStrategy(File baseDirectory) {
    super(baseDirectory);
  }

  @Override
  public String getName(Object arg0) {
    return super.getName(arg0);
  }

}
