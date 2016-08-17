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
package fr.cnes.sitools.proxy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.representation.Representation;

/**
 * To provide more complete information on files references in Directory Resource representation.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class ReferenceFileList extends ReferenceList {

  /** Hashmap for files */
  private Map<String, File> hash = null;

  /** restricted file types */
  private String regexp = null;

  /**
   * Constructor
   */
  public ReferenceFileList() {
    super();
    hash = new ConcurrentHashMap<String, File>();
  }

  /**
   * Constructor with initial capacity
   * 
   * @param initialCapacity
   *          the initial capacity
   */
  public ReferenceFileList(int initialCapacity) {
    super(initialCapacity);
    hash = new ConcurrentHashMap<String, File>(initialCapacity);
  }

  /**
   * Constructor with references
   * 
   * @param delegate
   *          list of references
   */
  public ReferenceFileList(List<Reference> delegate) {
    super(delegate);
    hash = new ConcurrentHashMap<String, File>();
  }

  /**
   * Constructor with URIs
   * 
   * @param uriList
   *          list of URI
   * @throws IOException
   *           when occurs
   */
  public ReferenceFileList(Representation uriList) throws IOException {
    super(uriList);
    hash = new ConcurrentHashMap<String, File>();
  }

  /**
   * Add a reference to file
   * 
   * @param reference
   *          reference to add
   * @param file
   *          to use
   */
  public void addFileReference(String reference, File file) {
    if (regexp != null) {
      if (reference.matches(regexp)) {
        if (super.add(reference)) {
          hash.put(reference, file);
        }
      }
      return;
    }
    if (super.add(reference)) {
      hash.put(reference, file);
    }
  }

  /**
   * Get a file
   * 
   * @param item
   *          the item name
   * @return the file corresponding
   */
  public File get(String item) {
    return hash.get(item);
  }

  /**
   * Gets the regexp value
   * 
   * @return the regexp
   */
  public String getRegexp() {
    return regexp;
  }

  /**
   * Sets the value of regexp
   * 
   * @param regexp
   *          the regexp to set
   */
  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

}
