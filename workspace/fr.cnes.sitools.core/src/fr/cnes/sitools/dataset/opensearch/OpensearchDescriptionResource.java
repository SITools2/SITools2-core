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
package fr.cnes.sitools.dataset.opensearch;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource attached DataSetApplication. Context allows to retrieve the DataSetApplication.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OpensearchDescriptionResource extends SitoolsResource {
  /**
   * The OpenSearch model object
   */
  private Opensearch opensearch;

  /**
   * Initiate the resource
   */
  public void doInit() {
    super.doInit();
  }

  /**
   * Returns the description of an OpenSearch engine
   * 
   * @param variant
   *          : the variant asked
   * @return the description of an OpenSearch engine
   */
  @Override
  @Get()
  protected Representation get(Variant variant) {
    // get the dataset Id
    DataSetApplication dsApp = (DataSetApplication) getApplication();
    DataSet ds = dsApp.getDataSet();
    String datasetId = ds.getId();

    String uriApp = dsApp.getPublicBaseRef(getRequest());

    // get the opensearch model object
    opensearch = getOpensearch(datasetId);

    if (opensearch != null && "ACTIVE".equals(opensearch.getStatus())) {
      // set the uri to the opensearch search engine resource
      opensearch.setRequestPath(uriApp + "/opensearch");
      // create a new mediaType to represent the opensearch description file
      // mediaType
      MediaType mediaTypeOpensearch = new MediaType("application/opensearchdescription+xml");

      // create the opensearch engine description file from a template using
      // freemarker
      Representation tableFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
          + "/opensearchDef.ftl").get();

      // Wraps the bean with a FreeMarker representation
      TemplateRepresentation result = new TemplateRepresentation(tableFtl, opensearch, mediaTypeOpensearch);

      return result;
    }
    else {
      Response response = new Response(false, "OPENSEARCH_NOT_ACTIVATED");
      return getRepresentation(response, variant);
    }

  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL information
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("retrieve_opensearch_description");
    info.setDocumentation("Method to retrieve the opensearch description of a dataset");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
  }

  /**
   * Get the OpenSearch model object using RIAP
   * 
   * @param id
   *          : the OpenSearch model object id
   * @return an OpenSearch model object corresponding to the given id null if the is no opensearch object corresponding
   *         to the given id
   * 
   */
  private Opensearch getOpensearch(String id) {
    return RIAPUtils.getObject(getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + id
        + getSitoolsSetting(Consts.APP_OPENSEARCH_URL), getContext());
  }

  @Override
  public void sitoolsDescribe() {
    setName("OpensearchDescriptionResource");
    setDescription("Resource to retrieve the raw description of the opensearch.");

  }

}
