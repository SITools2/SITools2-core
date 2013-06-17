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
package fr.cnes.sitools.plugins.resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.restlet.Context;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;

/**
 * Class handling a specific parameterized resource
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class ResourceClassPluginResource extends AbstractResourcePluginResource {

  /** The converter full class name */
  private String resourceClass;

  // /** The converter plugin application */
  // private ResourceClassPluginApplication application;
  /** The parent application id */
  private String parent;
  /** The parent application classname */
  private String appClassName;

  @Override
  public void sitoolsDescribe() {
    setName(this.getClass().getName());
    setDescription("lass handling a specific parameterized resource");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    resourceClass = (String) this.getRequest().getAttributes().get("resourceClass");
    parent = this.getRequest().getResourceRef().getQueryAsForm().getFirstValue("parent");
    appClassName = this.getRequest().getResourceRef().getQueryAsForm().getFirstValue("appClassName");
    // application = (ResourceClassPluginApplication) getApplication();

  }

  /**
   * return a representation of a Resource
   * 
   * @param variant
   *          the variant needed
   * @return the representation of a converter
   */
  @Get
  public Representation getResource(Variant variant) {

    Response response;
    if (this.resourceClass != null && this.appClassName != null && this.parent != null) {
      try {
        // gets the class corresponding to the given className
        @SuppressWarnings("unchecked")
        Class<ResourceModel> description = (Class<ResourceModel>) Class.forName(resourceClass);

        ResourceModel object = null;

        object = this.getObject(description);

        ResourceModelDTO dto = getResourceModelDTO(object);
        // serialize the objet and return a representation of it
        response = new Response(true, dto, ResourceModelDTO.class, "resourcePlugin");

      }
      catch (ClassNotFoundException e) {
        response = new Response(false, "module.Sva.classnotfound");
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
    else {
      response = new Response(false, "BAD_ARGUMENT");
    }

    return getRepresentation(response, variant);
  }

  /**
   * GET description
   * 
   * @param info
   *          WADL method information
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the description of a single converter class.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("converterClass", true, "xs:string", ParameterStyle.TEMPLATE,
        "Converter class name");
    info.getRequest().getParameters().add(pic);
    ParameterInfo pid = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE, "Dataset identifier");
    info.getRequest().getParameters().add(pid);
    this.addStandardResponseInfo(info);
  }

  /**
   * Get an AbstractConverter instance from the given Class<AbstractConverter>
   * 
   * @param description
   *          the Class<AbstractConverter>
   * @return an AbstractConverter instance
   * @throws NoSuchMethodException
   *           if a matching method is not found.
   * @throws IllegalAccessException
   *           - if this Constructor object enforces Java language access control and the underlying constructor is
   *           inaccessible.
   * @throws InstantiationException
   *           - if the class that declares the underlying constructor represents an abstract class.
   * @throws InvocationTargetException
   *           - if the underlying constructor throws an exception.
   */
  private ResourceModel getObject(Class<ResourceModel> description) throws NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException {
    Constructor<ResourceModel> constructor = description.getDeclaredConstructor();
    // create a new instance of AbstractConverter
    ResourceModel resourceModel = constructor.newInstance();

    Context context = getContext().createChildContext();
    context.getAttributes().put(ContextAttributes.SETTINGS, ((SitoolsApplication) getApplication()).getSettings());
    context.getAttributes().put("parent", parent);
    context.getAttributes().put("appClassName", appClassName);
    // call the initParametersForAdmin method to initialize some dynamic parameters
    resourceModel.initParametersForAdmin(context);

    return resourceModel;
  }

}
