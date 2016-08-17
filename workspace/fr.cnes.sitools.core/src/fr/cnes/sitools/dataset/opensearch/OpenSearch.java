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

import java.util.ArrayList;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource attached to a DataSetApplication. The context allows to retrieve the DataSetApplication. Returns the results
 * of an OpenSearch
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OpenSearch extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("OpenSearch");
    setDescription("OpenSearch resource to perform opensearch requests");
  }

  @Override
  protected void doInit() {
    super.doInit();
  }

  /**
   * Returns the result of a SolR request
   * 
   * Can be called with /search to return the result of a search Can be called with /suggest to return the result of a
   * suggest request suggest request can be returned in 2 different JSON JSON for user-client JSON for browser (
   * OpenSearch standard compliant)
   * 
   * @param variant
   *          : The variant needed
   * @return Representation representation returned
   */
  @Override
  @Get()
  public Representation get(Variant variant) {
    Representation repr = null;

    DataSetApplication dsApp = (DataSetApplication) getApplication();
    DataSet ds = dsApp.getDataSet();

    Opensearch os = getOpensearch(ds.getId());

    String requestStr = this.getRequest().getResourceRef().getRemainingPart();
    String query = this.getRequest().getResourceRef().getQueryAsForm().getFirstValue("q");

    boolean error = false;
    Response responseReturn = null;

    do {
      if (requestStr == null || "".equals(requestStr) || query == null || "".equals(query)) {
        // this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        responseReturn = new Response(false, "opensearch.syntax.error");
        error = true;
        break;
      }
      else {

        String attach = getSitoolsSetting(Consts.APP_SOLR_URL);
        if (this.getReference().getBaseRef().toString().endsWith("search")) {
          String url = RIAPUtils.getRiapBase() + attach + "/" + os.getId() + "/execute" + requestStr;
          Request reqGET = new Request(Method.GET, url);
          ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
          objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
          reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
          org.restlet.Response response = null;

          response = getContext().getClientDispatcher().handle(reqGET);

          if (response == null || Status.isError(response.getStatus().getCode())) {
            responseReturn = new Response(false, response.getStatus().getDescription());
            error = true;
            break;
            // throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
          }
          repr = response.getEntity();
          repr.setModificationDate(ds.getExpirationDate());
        }

        if (this.getReference().getBaseRef().toString().endsWith("suggest")) {

          ArrayList<String> fieldList = os.getKeywordColumns();
          String fields = fieldList.toString().replace("[", "").replace("]", "").replace(" ", "");

          String reqString = RIAPUtils.getRiapBase() + attach + "/" + os.getId() + "/" + fields + "/suggest"
              + requestStr;

          Request reqGET = new Request(Method.GET, reqString);

          ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
          objectMediaType.add(new Preference<MediaType>(variant.getMediaType()));
          reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
          org.restlet.Response response = null;

          response = getContext().getClientDispatcher().handle(reqGET);

          if (response == null || Status.isError(response.getStatus().getCode())) {
            responseReturn = new Response(false, "opensearch.syntax.error");
            error = true;
            break;
          }

          repr = response.getEntity();
          repr.setModificationDate(ds.getExpirationDate());

        }
      }
    } while (false);
    if (error) {
      // Response
      repr = getRepresentation(responseReturn, variant);
    }
    return repr;

  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL information
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("retrieve_solr_results");
    info.setDocumentation("Method to retrieve the solr result of a request");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
  }

  /**
   * Get the OpenSearch model object using RIAP
   * 
   * @param id
   *          : the OpenSearch model object id
   * @return an OpenSearch model object corresponding to the given id null if the is no OpenSearch object corresponding
   *         to the given id
   * 
   */
  private Opensearch getOpensearch(String id) {
    return RIAPUtils.getObject(getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + id
        + getSitoolsSetting(Consts.APP_OPENSEARCH_URL), getContext());
  }

}
