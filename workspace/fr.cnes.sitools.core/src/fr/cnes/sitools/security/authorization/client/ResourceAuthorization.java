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
package fr.cnes.sitools.security.authorization.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.restlet.Application;
import org.restlet.data.Method;
import org.restlet.security.Authorizer;
import org.restlet.security.DelegatedAuthorizer;
import org.restlet.security.MethodAuthorizer;
import org.restlet.security.Role;
import org.restlet.security.RoleAuthorizer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.security.authorization.business.SitoolsAndAuthorizer;
import fr.cnes.sitools.security.authorization.business.SitoolsMethodAuthorizer;
import fr.cnes.sitools.security.authorization.business.SitoolsOrAuthorizer;

/**
 * TODO renommer package ou classes en dto
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("resourceAuthorization")
public final class ResourceAuthorization implements Serializable, IResource {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 338228119578663776L;

  /** Resource identifier */
  private String id = null;
  /** Resource name */
  private String name = null;

  /** Type / Authorization or resource class */
  private String type = null;

  /** resource description / configuration */
  private String description = null;

  /** Resource URL */
  private String url = null;

  /** reference ResourceAuthorization for managing Classes of ResourceAuthorization */
  private String refId = null;
  
  /** Authorization list to apply on resource */
  private ArrayList<RoleAndMethodsAuthorization> authorizations = null;

  /**
   * Default constructor
   */
  public ResourceAuthorization() {
    super();
  }

  /**
   * Gets the authorizations value
   * 
   * @return the authorizations
   */
  public ArrayList<RoleAndMethodsAuthorization> getAuthorizations() {
    return authorizations;
  }

  /**
   * Sets the value of authorizations
   * 
   * @param authorizations
   *          the authorizations to set
   */
  public void setAuthorizations(ArrayList<RoleAndMethodsAuthorization> authorizations) {
    this.authorizations = authorizations;
  }

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the refId value
   * 
   * @return the refId
   */
  public String getRefId() {
    return refId;
  }

  /**
   * Sets the value of refId
   * 
   * @param refId
   *          the refId to set
   */
  public void setRefId(String refId) {
    this.refId = refId;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the url value
   * 
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of url
   * 
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Wrap security configuration to Authorizer
   * 
   * @param application the application to check
   * @return Authorizer
   */
  public DelegatedAuthorizer wrap(Application application) {
    ArrayList<Authorizer> rama = new ArrayList<Authorizer>();
    if (this.getAuthorizations() == null) {
      return new DelegatedAuthorizer(Authorizer.ALWAYS);
    }
    else {
      for (RoleAndMethodsAuthorization authorization : this.getAuthorizations()) {
        if (authorization.getRole() != null) {
          RoleAuthorizer ra = new RoleAuthorizer(authorization.getRole());
          ra.getAuthorizedRoles().add(new Role(authorization.getRole(), authorization.getDescription()));
          MethodAuthorizer ma = new SitoolsMethodAuthorizer(application);

          if ((null != authorization.getAllMethod()) && authorization.getAllMethod()) {
            rama.add(ra);
            continue;
          }

          if ((null != authorization.getGetMethod()) && authorization.getGetMethod()) {
            ma.getAuthenticatedMethods().add(Method.GET);
          }
          if ((null != authorization.getPostMethod()) && authorization.getPostMethod()) {
            ma.getAuthenticatedMethods().add(Method.POST);
          }
          if ((null != authorization.getPutMethod()) && authorization.getPutMethod()) {
            ma.getAuthenticatedMethods().add(Method.PUT);
          }
          if ((null != authorization.getDeleteMethod()) && authorization.getDeleteMethod()) {
            ma.getAuthenticatedMethods().add(Method.DELETE);
          }
          if ((null != authorization.getOptionsMethod()) && authorization.getOptionsMethod()) {
            ma.getAuthenticatedMethods().add(Method.OPTIONS);
          }
          if ((null != authorization.getHeadMethod()) && authorization.getHeadMethod()) {
            ma.getAuthenticatedMethods().add(Method.HEAD);
          }

          rama.add(new SitoolsAndAuthorizer(ra, ma));
        }
      }
    }

    if (rama.size() == 1) {
      return new DelegatedAuthorizer(rama.get(0));
    }
    if (rama.size() == 0) {
      return new DelegatedAuthorizer(Authorizer.ALWAYS);
    }
    return new SitoolsOrAuthorizer(rama);
  }

  /**
   * Wrap security configuration to Authorizer
   * 
   * @param reference
   *          Map<String, Role>
   * @param application
   *          Application parent of the authorizer (can be null)
   * @return Authorizer
   */
  public DelegatedAuthorizer wrap(Map<String, Role> reference, Application application) {
    ArrayList<Authorizer> rama = new ArrayList<Authorizer>();
    if (this.getAuthorizations() == null) {
      return new DelegatedAuthorizer(Authorizer.ALWAYS);
    }
    else {
      for (RoleAndMethodsAuthorization authorization : this.getAuthorizations()) {
        if (authorization.getRole() != null) {
          RoleAuthorizer ra = new RoleAuthorizer(authorization.getRole());

          ra.getAuthorizedRoles().add(reference.get(authorization.getRole()));
          MethodAuthorizer ma = new SitoolsMethodAuthorizer(application);

          if ((null != authorization.getAllMethod()) && authorization.getAllMethod()) {
            rama.add(ra);
            continue;
          }

          // ANONYMOUS METHODS FOR ROLE PUBLIC
          if (authorization.getRole().equals(SecurityUtil.PUBLIC_ROLE)) {
            if ((null != authorization.getGetMethod()) && authorization.getGetMethod()) {
              ma.getAnonymousMethods().add(Method.GET);
              ma.getAuthenticatedMethods().add(Method.GET);
            }
            if ((null != authorization.getPostMethod()) && authorization.getPostMethod()) {
              ma.getAnonymousMethods().add(Method.POST);
              ma.getAuthenticatedMethods().add(Method.POST);
            }
            if ((null != authorization.getPutMethod()) && authorization.getPutMethod()) {
              ma.getAnonymousMethods().add(Method.PUT);
              ma.getAuthenticatedMethods().add(Method.PUT);
            }
            if ((null != authorization.getDeleteMethod()) && authorization.getDeleteMethod()) {
              ma.getAnonymousMethods().add(Method.DELETE);
              ma.getAuthenticatedMethods().add(Method.DELETE);
            }
            if ((null != authorization.getOptionsMethod()) && authorization.getOptionsMethod()) {
              ma.getAnonymousMethods().add(Method.OPTIONS);
              ma.getAuthenticatedMethods().add(Method.OPTIONS);
            }
            if ((null != authorization.getHeadMethod()) && authorization.getHeadMethod()) {
              ma.getAnonymousMethods().add(Method.HEAD);
              ma.getAuthenticatedMethods().add(Method.HEAD);
            }
          }
          else {
            // Authenticated Role.
            if ((null != authorization.getGetMethod()) && authorization.getGetMethod()) {
              ma.getAuthenticatedMethods().add(Method.GET);
            }
            if ((null != authorization.getPostMethod()) && authorization.getPostMethod()) {
              ma.getAuthenticatedMethods().add(Method.POST);
            }
            if ((null != authorization.getPutMethod()) && authorization.getPutMethod()) {
              ma.getAuthenticatedMethods().add(Method.PUT);
            }
            if ((null != authorization.getDeleteMethod()) && authorization.getDeleteMethod()) {
              ma.getAuthenticatedMethods().add(Method.DELETE);
            }
            if ((null != authorization.getOptionsMethod()) && authorization.getOptionsMethod()) {
              ma.getAuthenticatedMethods().add(Method.OPTIONS);
            }
            if ((null != authorization.getHeadMethod()) && authorization.getHeadMethod()) {
              ma.getAuthenticatedMethods().add(Method.HEAD);
            }
          }

          rama.add(new SitoolsAndAuthorizer(ra, ma));
        }
      }
    }

    if (rama.size() == 1) {
      return new DelegatedAuthorizer(rama.get(0));
    }
    if (rama.size() == 0) {
      return new DelegatedAuthorizer(Authorizer.ALWAYS);
    }
    return new SitoolsOrAuthorizer(rama);
  }

}
