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
package fr.cnes.sitools.form.components;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.form.components.model.FormComponent;
import fr.cnes.sitools.persistence.XmlMapStore;

/**
 * Implementation of FormComponentStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class FormComponentsStoreXMLMap extends XmlMapStore<FormComponent> implements FormComponentsStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "formComponents";

  
  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public FormComponentsStoreXMLMap(File location, Context context) {
    super(FormComponent.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public FormComponentsStoreXMLMap(Context context) {
    super(FormComponent.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }
  
  
  @Override
  public List<FormComponent> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("formComponent", FormComponent.class);
    this.init(location, aliases);    
  }

  
}
