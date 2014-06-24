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
package fr.cnes.sitools.order;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.persistence.XmlMapStore;

public class OrderStoreXMLMap extends XmlMapStore<Order> implements OrderStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "orders";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the context
   */
  public OrderStoreXMLMap(File location, Context context) {
    super(Order.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the context
   */
  public OrderStoreXMLMap(Context context) {
    super(Order.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public List<Order> retrieveByParent(String id) {
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
    aliases.put("order", Order.class);
    this.init(location, aliases);
  }

  @Override
  public Order update(Order order) {

    Order result = null;
    Map<String, Order> map = getMap();
    Order current = map.get(order.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    result = current;
    current.setUserId(order.getUserId());
    current.setDescription(order.getDescription());
    current.setResourceCollection(order.getResourceCollection());
    current.setResourceDescriptor(order.getResourceDescriptor());
    current.setStatus(order.getStatus());
    current.setDateOrder(order.getDateOrder());
    current.setEvents(order.getEvents());
    current.setAdminResourceCollection(order.getAdminResourceCollection());

    map.put(order.getId(), current);

    return result;

  }

}
