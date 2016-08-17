/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * <p/>
 * This file is part of SITools2.
 * <p/>
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.role;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Base class for role resource management
 *
 * @author jp.boignard (AKKA Technologies)
 *
 */
public abstract class AbstractRoleResource extends SitoolsResource {

  /** Application */
  private RoleApplication application = null;

  /** Store */
  private RoleStoreInterface store = null;

  /** id in the request */
  private String roleId = null;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (RoleApplication) getApplication();
    store = application.getStore();

    roleId = (String) this.getRequest().getAttributes().get("roleId");
  }

  /**
   * Gets a representation according to the requested media
   *
   * @param response
   *          Response
   * @param media
   *          requested MediaType
   * @return ObjectRepresentation or XStreamRepresentation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("role", Role.class);
    xstream.alias("users", User.class);
    xstream.alias("groups", Group.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the Role from a representation
   *
   * @param representation
   *          the representation to use
   * @param variant
   *          the variant used
   * @return a role corresponding to the representation
   */
  public final Role getObject(Representation representation, Variant variant) {
    Role roleInput = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      XstreamRepresentation<Role> repXML = new XstreamRepresentation<Role>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("role", Role.class);
      xstream.alias("users", User.class);
      xstream.alias("groups", Group.class);
      repXML.setXstream(xstream);
      roleInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      roleInput = new JacksonRepresentation<Role>(representation, Role.class).getObject();
    }
    return roleInput;
  }

  /**
   * Get the identifier of the role
   *
   * @return the role identifier
   */
  public final String getRoleId() {
    return this.roleId;
  }

  /**
   * Get the store associated to the role application
   *
   * @return the store associated
   */
  public final RoleStoreInterface getStore() {
    return this.store;
  }

  /**
   * Gets the application value
   *
   * @return the application
   */
  public final RoleApplication getRoleApplication() {
    return application;
  }

  /**
   * Check if a role with the same name already exists in the store.
   *
   * @param role
   *          the role
   * @return true if a role exist with the same name, false otherwise
   */
  public boolean checkRoleExists(Role role) {
    ResourceCollectionFilter filter = new ResourceCollectionFilter(0, 1, role.getName());
    filter.setMode("strict");
    return !getStore().getList(filter).isEmpty();
  }

  /**
   * Check if the passed role is attached to authorizations
   *
   * @param role
   *          the role to check with the authorizations
   * @return a Response with the error message and the authorizations list where the role is used if the checking fail, null otherwise
   */
  public final Response checkRoleUsed(Role role) {
    List<ResourceAuthorization> roleUsedInAuthorizationList = getResourceAuthorizations(role);

    Response response = null;
    if (!roleUsedInAuthorizationList.isEmpty()) {
      response = new Response(false, roleUsedInAuthorizationList, ResourceAuthorization.class, "authorizations");
    }

    return response;

  }

  /**
   * Create a list with all authorizations used from a role
   *
   * @param role
   *      The user role
   *
   * @return a list of @ResourceAuthorization
   */
  public final List<ResourceAuthorization> getResourceAuthorizations(Role role) {
    List<ResourceAuthorization> authorizationList = RIAPUtils
        .getListOfObjects(application.getSettings().getString(Consts.APP_AUTHORIZATIONS_URL), getContext());

    List<ResourceAuthorization> roleUsedInAuthorizationList = new ArrayList<ResourceAuthorization>();
    if (authorizationList != null) {
      for (ResourceAuthorization authorization : authorizationList) {
        List<RoleAndMethodsAuthorization> roleAuthorizationList = authorization.getAuthorizations();
        if (roleAuthorizationList != null) {
          for (RoleAndMethodsAuthorization roleAuthorization : roleAuthorizationList) {
            if (roleAuthorization.getRole().equals(role.getName())) {
              roleUsedInAuthorizationList.add(authorization);
            }
          }
        }
      }
    }
    return roleUsedInAuthorizationList;
  }
}
