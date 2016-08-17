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
package fr.cnes.sitools.plugins.filters;

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
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.filters.dto.FilterModelDTO;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class handling a specific filter plugin
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class FilterClassPluginResource extends AbstractFilterPluginResource {

  /** The converter full class name */
  private String pluginClass;

  /** The applicationId */
  private String applicationId;

  /** The converter plugin application */
  private FilterClassPluginApplication application;

  @Override
  public void sitoolsDescribe() {
    setName(this.getClass().getName());
    setDescription("Class handling a specific plugin filter class");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    pluginClass = (String) this.getRequest().getAttributes().get("pluginClass");
    applicationId = (String) this.getRequest().getAttributes().get("applicationId");
    application = (FilterClassPluginApplication) getApplication();
  }

  /**
   * return a representation of a converter
   * 
   * @param variant
   *          the variant needed
   * @return the representation of a converter
   */
  @Get
  public Representation getPlugin(Variant variant) {

    Response response;
    if (this.pluginClass != null) {
      try {
        // gets the class corresponding to the given className
        @SuppressWarnings("unchecked")
        Class<FilterModel> description = (Class<FilterModel>) Class.forName(pluginClass);

        FilterModel object = null;
        try {
          // if the datasetId is null or empty return a instance with default
          // constructor call
          if (applicationId == null || "".equals(applicationId)) {
            object = this.getObject(description);
          }
          else {
            // FIXME >> application and not only project
            // get the Application corresponding to the given applicationId
            Project prj = RIAPUtils.getObject(applicationId, getSitoolsSetting(Consts.APP_PROJECTS_URL), getContext());
            // if the project is null return a instance with default constructor
            // call
            if (prj == null) {
              object = this.getObject(description);
            }
            else {
              try {
                // if the Project isn't null return a instance calling the
                // constructor with the DataSet
                Context ctx = this.getContext().createChildContext();
                ctx.getAttributes().put("PROJECT", prj);
                ctx.getAttributes().put(ContextAttributes.SETTINGS, application.getSettings());
                object = this.getObject(description, ctx);
              }
              catch (NoSuchMethodException e) {
                // if the constructor with the DataSet does not exists return a
                // instance with default constructor call
                object = this.getObject(description);
              }
            }
          }
          FilterModelDTO filterModelDTO = getFilterModelDTO(object);
          // serialize the objet and return a representation of it
          response = new Response(true, filterModelDTO, FilterModelDTO.class, "filterPlugin");
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
        response = new Response(false, "module.filter.classnotfound");
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
    info.setDocumentation("Method to get the description of a single plugin class.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("pluginClass", true, "xs:string", ParameterStyle.TEMPLATE,
        "Plugin class name");
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
  private FilterModel getObject(Class<FilterModel> description) throws NoSuchMethodException, InstantiationException,
    IllegalAccessException, InvocationTargetException {
    Constructor<FilterModel> constructor = description.getDeclaredConstructor();
    return constructor.newInstance();
  }

  /**
   * Get an AbstractConverter instance from the given Class<AbstractConverter> and the given DataSet
   * 
   * @param description
   *          the Class<AbstractConverter>
   * @param ctx
   *          the Context to send to the constructor
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
  private FilterModel getObject(Class<FilterModel> description, Context ctx) throws NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException {

    // Look for a constructor with a dataset parameter
    Class<?>[] objParam = new Class<?>[1];
    objParam[0] = Context.class;

    // get the constructor with the dataSet parameter
    Constructor<FilterModel> constructor = description.getConstructor(objParam);
    // create a new instance of AbstractConverter
    return constructor.newInstance(ctx);
  }

}
