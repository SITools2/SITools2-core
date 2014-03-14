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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.order.model.ConstsOrder;
import fr.cnes.sitools.order.model.Order;

/**
 * Implementation of OrderStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class OrderStoreXML extends SitoolsStoreXML<Order> {

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
  public OrderStoreXML(File location, Context context) {
    super(Order.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the context
   */
  public OrderStoreXML(Context context) {
    super(Order.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public Order create(Order order) {
    Order result = null;

    if (order.getId() == null || "".equals(order.getId())) {
      order.setId(UUID.randomUUID().toString());
    }

    if (order.getDateOrder() == null || "".equals(order.getDateOrder())) {
      order.setDateOrder(new Date());
    }

    // Recherche sur l'id
    for (Iterator<Order> it = getRawList().iterator(); it.hasNext();) {
      Order current = it.next();
      if (current.getId().equals(order.getId())) {
        getLog().info("Order found");
        result = current;
        break;
      }
    }
    order.setStatus("NEW");

    if (result == null) {
      getRawList().add(order);
      result = order;
    }
    return result;
  }

  @Override
  public Order update(Order order) {
    Order result = null;
    for (Iterator<Order> it = getRawList().iterator(); it.hasNext();) {
      Order current = it.next();
      if (current.getId().equals(order.getId())) {
        getLog().info("Updating Order");

        result = current;
        current.setUserId(order.getUserId());
        current.setDescription(order.getDescription());
        current.setResourceCollection(order.getResourceCollection());
        current.setResourceDescriptor(order.getResourceDescriptor());
        current.setStatus(order.getStatus());
        current.setDateOrder(order.getDateOrder());
        current.setEvents(order.getEvents());
        current.setAdminResourceCollection(order.getAdminResourceCollection());
        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  @Override
  public List<Order> getList(ResourceCollectionFilter filter) {
    List<Order> result = new ArrayList<Order>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (Order order : getRawList()) {
        if (null == order.getUserId()) {
          continue;
        }

        if (order.getUserId().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          switch (filter.getFilterMode()) {
            case ConstsOrder.STATUS_NOT_DELETED:
              if (!"deleted".equals(order.getStatus().toLowerCase())) {
                result.add(order);
              }
              break;
            default:
              result.add(order);
              break;
          }
        }

      }
    }
    else {
      result.addAll(getRawList());
    }

    // Si index premier element > nombre d'elements filtres => resultat vide
    if (filter.getStart() > result.size()) {
      result.clear();
      return result;
    }

    // Tri
    sort(result, filter);

    return result;
  }

  /**
   * Sort the list (by default on the name)
   * 
   * @param result
   *          list to be sorted
   * @param filter
   *          ResourceCollectionFilter with sort properties.
   */
  public void sort(List<Order> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {

      if (filter.getSort().equals("dateOrder")) {

        Collections.sort(result, new ResourceComparator<Order>(filter) {
          @Override
          public int compare(Order arg0, Order arg1) {

            if (arg0.getDateOrder() != null && arg1.getDateOrder() != null) {
              Date d1 = arg0.getDateOrder();
              Date d2 = arg1.getDateOrder();
              if ((getFilter() != null) && (getFilter().getOrder() != null) && (getFilter().getOrder().equals(DESC))) {
                return d1.compareTo(d2);
              }
              else {
                return d2.compareTo(d1);
              }

            }
            else {
              return -1;
            }
          }
        });

      }
      else if (filter.getSort().equals("description")) {

        Collections.sort(result, new ResourceComparator<Order>(filter) {
          @Override
          public int compare(Order arg0, Order arg1) {

            if (arg0.getDescription() == null) {
              return 1;
            }
            if (arg1.getDescription() == null) {
              return -1;
            }
            String s1 = (String) arg0.getDescription();
            String s2 = (String) arg1.getDescription();

            return super.compare(s1, s2);

          }
        });

      }
      else if (filter.getSort().equals("status")) {

        Collections.sort(result, new ResourceComparator<Order>(filter) {
          @Override
          public int compare(Order arg0, Order arg1) {

            if (arg0.getStatus() == null) {
              return 1;
            }
            if (arg1.getStatus() == null) {
              return -1;
            }
            String s1 = (String) arg0.getStatus();
            String s2 = (String) arg1.getStatus();

            return super.compare(s1, s2);

          }
        });

      }
      else {

        Collections.sort(result, new ResourceComparator<Order>(filter) {
          @Override
          public int compare(Order arg0, Order arg1) {

            if (arg0.getUserId() == null) {
              return 1;
            }
            if (arg1.getUserId() == null) {
              return -1;
            }
            String s1 = (String) arg0.getUserId();
            String s2 = (String) arg1.getUserId();

            return super.compare(s1, s2);

          }
        });

      }

    }
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("order", Order.class);
    this.init(location, aliases);
  }

  @Override
  public List<Order> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}
