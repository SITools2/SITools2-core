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
package fr.cnes.sitools.dataset.services;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.persistence.XmlMapStore;

/**
 * Implementation of ServiceCollectionModelStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class ServiceStoreXMLMap extends XmlMapStore<ServiceCollectionModel> implements ServiceStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "Services";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public ServiceStoreXMLMap(File location, Context context) {
    super(ServiceCollectionModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public ServiceStoreXMLMap(Context context) {
    super(ServiceCollectionModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public ServiceCollectionModel update(ServiceCollectionModel services) {
    ServiceCollectionModel result = null;

    Map<String, ServiceCollectionModel> map = getMap();
    ServiceCollectionModel current = map.get(services.getId());
    result = current;
    current.setId(services.getId());
    current.setName(services.getName());
    current.setDescription(services.getDescription());
    current.setServices(services.getServices());
    map.put(services.getId(), current);
    return result;
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("services", ServiceCollectionModel.class);
    this.init(location, aliases);
  }

  public List<ServiceCollectionModel> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}
