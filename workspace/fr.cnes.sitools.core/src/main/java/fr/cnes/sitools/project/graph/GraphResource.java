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
package fr.cnes.sitools.project.graph;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.graph.model.Graph;

/**
 * Sprint 6
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class GraphResource extends AbstractGraphResource {
  /**
   * The name of the graph element in the Response returned
   */
  private static final String GRAPH_RESPONSE_NAME = "graph";

  @Override
  public void sitoolsDescribe() {
    setName("GraphResource");
    setDescription("Resource for managing graph research within a graph");
    setNegotiated(true);
  }

  /**
   * get all projects
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveGraph(Variant variant) {
    if (getProjectId() != null) {
      Graph graph = getStore().retrieve(getGraphId());
      Response response = new Response(true, graph, Graph.class, GRAPH_RESPONSE_NAME);
      trace(Level.FINE, "Edit browse by collection for project - id:" + getProjectId());
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<Graph> graphs = getStore().getList(filter);
      int total = graphs.size();
      graphs = getStore().getPage(filter, graphs);
      Response response = new Response(true, graphs, Graph.class, "graphs");
      response.setTotal(total);
      trace(Level.FINE, "View available browses by collection");
      return getRepresentation(response, variant);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Retrieve the graph definition.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo(PROJECT_ID_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Update / Validate existing graph
   * 
   * @param representation
   *          Graph representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateGraph(Representation representation, Variant variant) {
    Graph graphOutput = null;
    try {

      Graph graphInput = null;
      if (representation != null) {
        // Parse object representation
        graphInput = getObject(representation, variant);

        unregisterObserver(graphInput);
        // Business service
        graphOutput = getStore().update(graphInput);

        registerObserver(graphOutput);
      }
      Response response;
      if (graphOutput != null) {
        trace(Level.INFO, "Update browse by collection for project - id: " + getProjectId());
        response = new Response(true, graphOutput, Graph.class, GRAPH_RESPONSE_NAME);
      }
      else {
        trace(Level.INFO, "Cannot update browse by collection for project - id: " + getProjectId());
        response = new Response(false, "graph.update.failure");
      }

      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update browse by collection for project - id: " + getProjectId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update browse by collection for project - id: " + getProjectId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Modifies the graph definition.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo(PROJECT_ID_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Update / Validate existing graph
   * 
   * @param representation
   *          Graph representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newGraph(Representation representation, Variant variant) {
    try {
      Graph graphInput = getObject(representation, variant);
      // set graph id
      if (graphInput.getId() == null || graphInput.getId().equals("")) {
        graphInput.setId(getGraphId());
      }
      // Business service
      graphInput.setParent(getProjectId());

      Graph graph = getStore().create(graphInput);

      registerObserver(graph);

      trace(Level.INFO, "Create browse by collection for project - id: " + getProjectId());
      // Response
      Response response = new Response(true, graph, Graph.class, GRAPH_RESPONSE_NAME);
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create browse by collection for project - id: " + getProjectId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create browse by collection for project - id: " + getProjectId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to add a graph definition.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo(PROJECT_ID_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Sets the xstream parameters
   * 
   * @param xstream
   *          A xstream object
   * @param response
   *          the response used
   */
  public void configure(XStream xstream, Response response) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
  }

  /**
   * Delete graph
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteGraph(Variant variant) {
    try {
      Response response;
      Graph graph = getStore().retrieve(getGraphId());
      if (graph != null) {

        // Business service
        getStore().delete(getGraphId());

        // unregister as observer
        unregisterObserver(graph);
        trace(Level.INFO, "Delete browse by collection for project - id: " + getProjectId());
        response = new Response(true, "graph.delete.success");

      }
      else {
        // Response
        response = new Response(true, "graph.delete.failure");
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete browse by collection for project - id: " + getProjectId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete browse by collection for project - id: " + getProjectId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete the graph definition.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo(PROJECT_ID_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
