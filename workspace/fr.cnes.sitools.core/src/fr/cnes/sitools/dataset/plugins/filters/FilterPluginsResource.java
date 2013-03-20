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
package fr.cnes.sitools.dataset.plugins.filters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.restlet.Context;
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
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.filter.AbstractFilterResource;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Represent the details of a given filter
 * 
 * @author AKKA Technologies
 */
public final class FilterPluginsResource extends AbstractFilterResource {
  /**
   * The filter full class name
   */
  private String filterClass;
  /** The datasetId */
  private String datasetId;

  /** Filter plugins application */
  private FilterPluginsApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("FilterResource");
    setDescription("Show the definition of a single filter");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    filterClass = (String) this.getRequest().getAttributes().get("filterClass");
    datasetId = (String) this.getRequest().getAttributes().get("datasetId");

    application = (FilterPluginsApplication) getApplication();
  }

  /**
   * return a representation of a filter
   * 
   * @param variant
   *          the variant needed
   * @return the representation of a filter
   */
  @Get
  public Representation getFilter(Variant variant) {

    Response response;
    if (this.filterClass != null) {
      try {
        // gets the class corresponding to the given className
        @SuppressWarnings("unchecked")
        Class<AbstractFilter> description = (Class<AbstractFilter>) Class.forName(filterClass);
        Context ctx = this.getContext().createChildContext();
        AbstractFilter object = null;
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
                // constructor with the Dataset in the context
                ctx.getAttributes().put("DATASET", ds);
                ctx.getAttributes().put("SETTINGS", application.getSettings());
                object = this.getObject(description, ctx);
              }
              catch (NoSuchMethodException e) {
                // if the constructor with the DataSet does not exists return a
                // instance with default constructor call
                object = this.getObject(description);
              }
            }
          }
          FilterModelDTO filterDTO = getFilterModelDTO(object);
          filterDTO.setClassName(object.getClass().getName());
          // serialize the objet and return a representation of it
          response = new Response(true, filterDTO, FilterModelDTO.class, "filter");
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
        response = new Response(false, "module.Filter.classnotfound");
      }

    }
    else {
      response = new Response(false, "BAD_ARGUMENT");
    }

    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info, String path) {
    if (path.endsWith("{datasetId}")) {
      // Instance
      info.setDocumentation("GET " + path + " : Method to retrieve filter definition for the dataset.");
    }
    else if (path.endsWith("{filterClass}")) {
      info.setDocumentation("GET " + path + " : Method to retrieve native filter class definition.");
    }

    // info.setDocumentation("Method to retrieve a single filter by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("filterClass", true, "class", ParameterStyle.TEMPLATE,
        "Name of the filter class");
    info.getRequest().getParameters().add(param);
    ParameterInfo paramDs = new ParameterInfo("datasetId", true, "ID", ParameterStyle.TEMPLATE,
        "Identifier of the dataset implementing the filter");
    info.getRequest().getParameters().add(paramDs);
    this.addStandardResponseInfo(info);
  }

  /**
   * Get an AbstractFilter instance from the given Class<AbstractFilter>
   * 
   * @param description
   *          the Class<AbstractFilter>
   * @return an AbstractFilter instance
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
  private AbstractFilter getObject(Class<AbstractFilter> description) throws NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException {
    // look for the default constructor, with no parameter
    Constructor<AbstractFilter> constructor = description.getDeclaredConstructor();
    // create a new instance of AbstractFilter
    return constructor.newInstance();
  }

  /**
   * Get an AbstractFilter instance from the given Class<AbstractFilter> and the given DataSet
   * 
   * @param description
   *          the Class<AbstractFilter>
   * @param ctx
   *          the Context to send to the constructor
   * @return an AbstractFilter instance
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
  private AbstractFilter getObject(Class<AbstractFilter> description, Context ctx) throws NoSuchMethodException,
    InstantiationException, IllegalAccessException, InvocationTargetException {

    // Look for a constructor with a dataset parameter
    Class<?>[] objParam = new Class<?>[1];
    objParam[0] = Context.class;

    // get the constructor with the dataSet parameter
    Constructor<AbstractFilter> constructor = description.getConstructor(objParam);
    // create a new instance of AbstractFilter
    return constructor.newInstance(ctx);
  }

  /**
   * Gets representation according to the specified Variant if present. If variant is null (when content negociation =
   * false) sets the variant to the first client accepted mediaType.
   * 
   * @param response
   *          the response to be transformed
   * @param variant
   *          restlet variant
   * @return Representation a restlet representation of the response
   */
  public Representation getRepresentation(Response response, Variant variant) {
    MediaType defaultMediaType = getMediaType(variant);
    return getRepresentation(response, defaultMediaType);
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
    xstream.alias("filterParameters", FilterParameter.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(ExtensionModel.class, "parametersMap");

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
