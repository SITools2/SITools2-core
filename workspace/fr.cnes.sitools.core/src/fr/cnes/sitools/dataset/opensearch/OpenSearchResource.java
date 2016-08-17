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
package fr.cnes.sitools.dataset.opensearch;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;

/**
 * OpenSearch CRUD resource
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OpenSearchResource extends AbstractSearchResource {

  @Override
  public void sitoolsDescribe() {
    setName("OpenSearchResource");
    setDescription("Resource for managing opensearch configuration within a dataset");
  }

  @Override
  public void doInit() {
    super.doInit();
    this.setNegotiated(false);
  }

  /**
   * Create / attach OpenSearch to dataset
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newOpensearch(Representation representation, Variant variant) {
    try {
      Opensearch osearchInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        osearchInput = new XstreamRepresentation<Opensearch>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        osearchInput = new JacksonRepresentation<Opensearch>(representation, Opensearch.class).getObject();
      }

      // set opensearch id
      if (osearchInput.getId() == null || osearchInput.getId().equals("")) {
        osearchInput.setId(getDatasetId());
      }

      // set opensearch parent url
      DataSet ds = this.getDataset(getDatasetId());

      if (ds != null) {
        osearchInput.setParentUrl(ds.getSitoolsAttachementForUsers());
      }

      // Business service
      osearchInput.setParent(getDatasetId());

      Opensearch osearchOutput = getStore().create(osearchInput);

      registerObserver(osearchOutput);

      // Response

      Response response = new Response(true, osearchOutput, Opensearch.class, "opensearch");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the POST HTTP command
   * 
   * @param info
   *          the method WADL info
   */
  @Override
  public void describePost(MethodInfo info) {
    info.setIdentifier("create_opensearch");
    info.setDocumentation("Method to create a new opensearch sending its XML or JSON representation");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  @Get
  @Override
  public Representation get(Variant variant) {

    if (getDatasetId() != null) {
      Opensearch osearch = getStore().retrieve(getDatasetId());
      Response response = new Response(true, osearch, Opensearch.class, "opensearch");
      return getRepresentation(response, variant);
    }
    else {
      Opensearch[] osearchs = getStore().getArray();
      Response response = new Response(true, osearchs);
      return getRepresentation(response, variant);
    }

  }

  /**
   * Describe the GET HTTP command
   * 
   * @param info
   *          the method WADL info
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("get_opensearch");
    info.setDocumentation("Method to retrieve one (if ID is present) or all opensearches.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("opensearchId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the opensearch");
    info.getRequest().getParameters().add(pic);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Update / Validate existing OpenSearchs
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateOpensearch(Representation representation, Variant variant) {
    Opensearch osearchOutput = null;
    try {
      Opensearch osearchInput = null;
      if (representation != null) {
        if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
          // Parse the XML representation to get the bean
          osearchInput = new XstreamRepresentation<Opensearch>(representation).getObject();

        }
        else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
          // Parse the JSON representation to get the bean
          osearchInput = new JacksonRepresentation<Opensearch>(representation, Opensearch.class).getObject();
        }

        // retrieve the parentUrl
        Opensearch osearchData = getStore().retrieve(osearchInput.getId());
        osearchInput.setParentUrl(osearchData.getParentUrl());

        // Business service
        osearchOutput = getStore().update(osearchInput);

        // Register Opensearch as observer of datasets resources
        unregisterObserver(osearchOutput);

        registerObserver(osearchOutput);
      }

      if (osearchOutput != null) {
        // Response
        Response response = new Response(true, osearchOutput, Opensearch.class, "opensearch");
        return getRepresentation(response, variant);
      }
      else {
        Response response = new Response(false, "Can not validate osearch");
        return getRepresentation(response, variant);
      }

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the PUT HTTP command
   * 
   * @param info
   *          the method WADL info
   */
  @Override
  public void describePut(MethodInfo info) {
    info.setIdentifier("modify_opensearch");
    info.setDocumentation("Method to to modify a given opensearch by sending its XML or JSON representation.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("opensearchId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the opensearch");
    info.getRequest().getParameters().add(pic);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete OpenSearch
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteOpensearch(Variant variant) {
    try {
      Opensearch os = getStore().retrieve(getDatasetId());
      Response response = null;

      if (os != null) {
        unregisterObserver(os);

        // Business service
        getStore().delete(getDatasetId());

        // Response
        response = new Response(true, "opensearch.delete.success");
      }
      else {
        response = new Response(false, "opensearch.delete.notfound");
      }

      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the DELETE HTTP command
   * 
   * @param info
   *          the method WADL info
   */
  @Override
  public void describeDelete(MethodInfo info) {
    info.setIdentifier("modify_opensearch");
    info.setDocumentation("Method to to modify a given opensearch by sending its XML or JSON representation.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("opensearchId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the opensearch");
    info.getRequest().getParameters().add(pic);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
