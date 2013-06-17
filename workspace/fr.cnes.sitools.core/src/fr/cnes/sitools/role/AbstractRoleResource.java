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
package fr.cnes.sitools.role;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;

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
  private SitoolsStore<Role> store = null;
  
  /** id in the request */
  private String roleId = null;

  @Override
  public final void doInit() {
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
   * @return the role identifier
   */
  public final String getRoleId() {
    return this.roleId;
  }
  
  /**
   * Get the store associated to the role application
   * @return the store associated
   */
  public final SitoolsStore<Role> getStore() {
    return this.store;
  }

  /**
   * Gets the application value
   * @return the application
   */
  public final RoleApplication getRoleApplication() {
    return application;
  }

}
