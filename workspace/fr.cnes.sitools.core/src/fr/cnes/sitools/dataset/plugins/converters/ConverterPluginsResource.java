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
package fr.cnes.sitools.dataset.plugins.converters;

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
import fr.cnes.sitools.dataset.converter.AbstractConverterResource;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Represent the details of a given converter
 * 
 * @author AKKA Technologies
 */
public final class ConverterPluginsResource extends AbstractConverterResource {

  /** The converter full class name */
  private String converterClass;

  /** The datasetId */
  private String datasetId;

  /** The converter plugin application */
  private ConverterPluginsApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("ConverterPluginsResource");
    setDescription("Expose the definition of a single converter class.");
  }

  @Override
  public void doInit() {
    super.doInit();
    converterClass = (String) this.getRequest().getAttributes().get("converterClass");
    datasetId = (String) this.getRequest().getAttributes().get("datasetId");

    application = (ConverterPluginsApplication) getApplication();
  }

  /**
   * return a representation of a converter
   * 
   * @param variant
   *          the variant needed
   * @return the representation of a converter
   */
  @Get
  public Representation getConverter(Variant variant) {

    Response response;
    if (this.converterClass != null) {
      try {
        // gets the class corresponding to the given className
        @SuppressWarnings("unchecked")
        Class<AbstractConverter> description = (Class<AbstractConverter>) Class.forName(converterClass);

        AbstractConverter object = null;
        try {
          // if the datasetId is null or empty return a instance with default
          // constructor call
          if (datasetId == null || "".equals(datasetId)) {
            object = this.getObject(description);
          }
          else {
            // get the DataSet corresponding to the given datasetId
            DataSet ds = RIAPUtils.getObject(datasetId, getSitoolsSetting(Consts.APP_DATASETS_URL), getContext());
            // if the DataSet is null return a instance with default constructor
            // call
            if (ds == null) {
              object = this.getObject(description);
            }
            else {
              try {
                // if the DataSet isn't null return a instance calling the
                // constructor with the DataSet
                Context ctx = this.getContext().createChildContext();
                ctx.getAttributes().put("DATASET", ds);
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
          // ArrayList<ConverterParameter> coll = new ArrayList<ConverterParameter>(object.getParametersMap().values());
          // object.setParameters(coll);

          ConverterModelDTO convDTO = getConverterModelDTO(object);
          convDTO.setClassName(object.getClass().getName());

          // serialize the objet and return a representation of it
          response = new Response(true, convDTO, ConverterModelDTO.class, "converter");
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
        response = new Response(false, "module.Sva.classnotfound");
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
  private AbstractConverter getObject(Class<AbstractConverter> description) throws NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException {
    // look for the default constructor, with no parameter
    Constructor<AbstractConverter> constructor = description.getDeclaredConstructor();
    // create a new instance of AbstractConverter
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
  private AbstractConverter getObject(Class<AbstractConverter> description, Context ctx) throws NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException {

    // Look for a constructor with a dataset parameter
    Class<?>[] objParam = new Class<?>[1];
    objParam[0] = Context.class;

    // get the constructor with the dataSet parameter
    Constructor<AbstractConverter> constructor = description.getConstructor(objParam);
    // create a new instance of AbstractConverter
    return constructor.newInstance(ctx);
  }

}
