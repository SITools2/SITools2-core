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
package fr.cnes.sitools.plugins.applications;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginDescriptionDTO;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;

/**
 * Represent the details of a given Application
 * 
 * @author AKKA Technologies
 */
public final class ApplicationPluginListingResource extends AbstractApplicationPluginCommonResource {

  /** The Application full class name */
  private String applicationClass;

  @Override
  public void sitoolsDescribe() {
    setName("ApplicationPluginListingResource");
    setDescription("Get details about a class in the list.");
  }

  @Override
  public void doInit() {
    super.doInit();
    applicationClass = (String) this.getRequest().getAttributes().get("applicationPluginClass");
  }

  /**
   * Return a representation of an application plug-in
   * 
   * @param variant
   *          the variant needed
   * @return the representation of a Application
   */
  @Get
  public Representation getApplication(Variant variant) {

    Response response;
    if (this.applicationClass != null) {

      try {
        // gets the class corresponding to the given className
        @SuppressWarnings("unchecked")
        Class<AbstractApplicationPlugin> description = (Class<AbstractApplicationPlugin>) Class
            .forName(applicationClass);

        try {
          // Look for a constructor with no parameters
          // get the default constructor
          Constructor<AbstractApplicationPlugin> constru = description.getDeclaredConstructor();
          // instanciate an instance of Application
          AbstractApplicationPlugin object = constru.newInstance();

          ApplicationPluginDescriptionDTO dto = new ApplicationPluginDescriptionDTO();
          dto.setClassAuthor(object.getAuthor());
          dto.setClassName(applicationClass);
          dto.setDescription(object.getDescription());
          dto.setModel(getApplicationModelDTO(object.getModel()));
          dto.setName(object.getName());
          dto.setClassVersion(object.getModel().getClassVersion());
          dto.setClassOwner(object.getModel().getClassOwner());
          // serialize the objet and return a representation of it
          response = new Response(true, dto, ApplicationPluginDescriptionDTO.class, "ApplicationPlugin");

        }
        catch (IllegalArgumentException e) {
          response = new Response(false, "IllegalArgumentException");
        }
        catch (InstantiationException e) {
          response = new Response(false, "InstantiationException");
        }
        catch (IllegalAccessException e) {
          response = new Response(false, "IllegalAccessException");
        }
        catch (InvocationTargetException e) {
          response = new Response(false, "InvocationTargetException");
        }
        catch (SecurityException e) {
          response = new Response(false, "SecurityException");
        }
        catch (NoSuchMethodException e) {
          response = new Response(false, "NoSuchMethodException");
        }
      }
      catch (ClassNotFoundException e) {
        response = new Response(false, "module.Application.classnotfound");
      }
    }
    else {
      response = new Response(false, "BAD_ARGUMENT");
    }

    return getRepresentation(response, variant);
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          the response to be transformed
   * @param media
   *          the media type (json/xml)
   * @return Representation the representation of the response
   */
  public Representation getRepresentation(Response response, MediaType media) {

    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("ApplicationPluginParameter", ApplicationPluginParameter.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  @Override
  protected void describeGet(MethodInfo info) {
    // Global method info
    info.setIdentifier("get_appPlugins_detail");
    info.setDocumentation("Get details about a class in the list");

    // Standard GET request
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramInfo = new ParameterInfo("applicationPluginClass", true, "xs:string", ParameterStyle.TEMPLATE,
        "Name of the class to retrieve.");
    info.getRequest().getParameters().add(paramInfo);

    // Standard response
    this.addStandardResponseInfo(info);
  }

}
