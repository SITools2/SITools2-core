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
package fr.cnes.sitools.security.authorization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlArrayList;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.persistence.Paginable;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;

/**
 * Implementation of AuthorizationStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class AuthorizationStoreXML extends Paginable<ResourceAuthorization> implements AuthorizationStore {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "authorizations";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public AuthorizationStoreXML(File location, Context context) {
    super(location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public AuthorizationStoreXML(Context context) {
    super(context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public ResourceAuthorization create(ResourceAuthorization authorization) {
    ResourceAuthorization result = null;

    // id resource obligatoire
    if (authorization.getId() == null || "".equals(authorization.getId())) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Resource id is mandatory");
    }

    // Recherche sur l'id
    for (Iterator<ResourceAuthorization> it = getRawList().iterator(); it.hasNext();) {
      ResourceAuthorization current = it.next();
      if (current.getId().equals(authorization.getId())) {
        getLog().info("AuthorizationStoreXML.create Authorization found for resource " + current.getId());
        result = current;
        break;
      }
    }

    if (result == null) {
      getRawList().add(authorization);
      result = authorization;
    }
    // on retourne l'objet trouve sans modifiation et sans exception.
    return result;
  }

  @Override
  public ResourceAuthorization retrieve(String id) {
    ResourceAuthorization result = null;
    for (Iterator<ResourceAuthorization> it = getRawList().iterator(); it.hasNext();) {
      ResourceAuthorization current = it.next();
      if (current.getId().equals(id)) {
        getLog().info("AuthorizationStoreXML.retrieve Authorization found for resource " + id);
        result = current;
        break;
      }
    }
    return result;
  }

  @Override
  public ResourceAuthorization update(ResourceAuthorization authorization) {
    ResourceAuthorization result = null;
    for (Iterator<ResourceAuthorization> it = getRawList().iterator(); it.hasNext();) {
      ResourceAuthorization current = it.next();
      if (current.getId().equals(authorization.getId())) {
        getLog().info("Updating Authorization for resource " + current.getId());

        result = current;
        current.setName(authorization.getName());
        current.setDescription(authorization.getDescription());
        current.setUrl(authorization.getUrl());
        current.setAuthorizations(authorization.getAuthorizations());
        it.remove();

        break;
      }
    }
    // authorizer la creation sur le PUT
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  @Override
  public boolean delete(String id) {
    boolean result = false;
    for (Iterator<ResourceAuthorization> it = getRawList().iterator(); it.hasNext();) {
      ResourceAuthorization current = it.next();
      if (current.getId().equals(id)) {
        getLog().info("Removing Authorization");
        it.remove();
        result = true;
        break;
      }
      // else {
      // // next
      // }
    }
    return result;
  }

  @Override
  public ResourceAuthorization[] getArray() {
    ResourceAuthorization[] result = null;
    if ((getRawList() != null) && (getRawList().size() > 0)) {
      result = getRawList().toArray(new ResourceAuthorization[getRawList().size()]);
    }
    else {
      result = new ResourceAuthorization[0];
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.authorization.AuthorizationStore#getArray(fr.cnes.sitools.common.model.
   * ResourceCollectionFilter)
   */
  @Override
  public ResourceAuthorization[] getArray(ResourceCollectionFilter filter) {
    List<ResourceAuthorization> resultList = getList(filter);

    ResourceAuthorization[] result = null;
    if ((resultList != null) && (resultList.size() > 0)) {
      result = resultList.toArray(new ResourceAuthorization[resultList.size()]);
    }
    else {
      result = new ResourceAuthorization[0];
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.authorization.AuthorizationStore#getArray(fr.cnes.sitools.common.model.
   * ResourceCollectionFilter)
   */
  @Override
  public ResourceAuthorization[] getArrayByType(ResourceCollectionFilter filter, String type) {
    List<ResourceAuthorization> resultList = getListByType(filter, type);

    ResourceAuthorization[] result = null;
    if ((resultList != null) && (resultList.size() > 0)) {
      result = resultList.toArray(new ResourceAuthorization[resultList.size()]);
    }
    else {
      result = new ResourceAuthorization[0];
    }
    return result;
  }

  @Override
  public ResourceAuthorization[] getArrayByXQuery(String xquery) {
    getLog().severe("getArrayByXQuery NOT IMPLEMENTED");
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.cnes.sitools.security.authorization.AuthorizationStore#getList(fr.cnes.sitools.common.model.ResourceCollectionFilter
   * )
   */
  @Override
  public List<ResourceAuthorization> getList(ResourceCollectionFilter filter) {
    List<ResourceAuthorization> result = new ArrayList<ResourceAuthorization>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (ResourceAuthorization object : getRawList()) {
        if (null == object.getName()) {
          continue;
        }
        if (object.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          result.add(object);
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

  @Override
  public List<ResourceAuthorization> getListByType(ResourceCollectionFilter filter, String type) {
    List<ResourceAuthorization> listFiltered = getList(filter);
    List<ResourceAuthorization> result = new ArrayList<ResourceAuthorization>();
    if ((listFiltered != null) && (listFiltered.size() > 0)) {

      for (ResourceAuthorization resourceAuth : listFiltered) {
        if (resourceAuth.getType() != null && resourceAuth.getType().equals(type)) {
          result.add(resourceAuth);
        }
      }
    }

    // Tri
    sort(result, null);

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
  public void sort(List<ResourceAuthorization> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<ResourceAuthorization>(filter));
    }
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  @SuppressWarnings("unchecked")
  public void init(File location) {
    getLog().info("Store location " + location.getAbsolutePath());
    XStream xstream = getParser();

    FilePersistenceStrategy strategy = new FilePersistenceStrategy(location, xstream);
    setList(new XmlArrayList(strategy));
  }

  /**
   * Get XStream parser configured.
   * 
   * @return XStream
   */
  public XStream getParser() {
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    boolean strict = !settings.isStartWithMigration();

    XStream localXstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, context, strict);
    localXstream.autodetectAnnotations(true);
    localXstream.alias("resourceAuthorization", ResourceAuthorization.class);
    localXstream.alias("authorize", RoleAndMethodsAuthorization.class);
    localXstream.aliasField("ALL", ResourceAuthorization.class, "allMethod");
    localXstream.aliasField("POST", ResourceAuthorization.class, "postMethod");
    localXstream.aliasField("GET", ResourceAuthorization.class, "getMethod");
    localXstream.aliasField("PUT", ResourceAuthorization.class, "putMethod");
    localXstream.aliasField("DELETE", ResourceAuthorization.class, "deleteMethod");
    localXstream.aliasField("OPTIONS", ResourceAuthorization.class, "optionsMethod");
    localXstream.aliasField("HEAD", ResourceAuthorization.class, "headMethod");
    return localXstream;
  }

}
