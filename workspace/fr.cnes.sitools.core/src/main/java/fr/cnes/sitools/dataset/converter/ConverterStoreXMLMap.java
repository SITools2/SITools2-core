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
package fr.cnes.sitools.dataset.converter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.Context;

import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.persistence.XmlMapStore;

/**
 * Implementation of converterStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class ConverterStoreXMLMap extends XmlMapStore<ConverterChainedModel> implements ConverterStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "converters";
  
  
  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public ConverterStoreXMLMap(File location, Context context) {
    super(ConverterChainedModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public ConverterStoreXMLMap(Context context) {
    super(ConverterChainedModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  public List<ConverterChainedModel> retrieveByParent(String id) {

    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }
  
  
  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();
    aliases.put("converterChainedModel", ConverterChainedModel.class);
    aliases.put("converterModel", ConverterModel.class);
    aliases.put("converterParameter", ConverterParameter.class);
    this.init(location, aliases);
  }
  
  
}
