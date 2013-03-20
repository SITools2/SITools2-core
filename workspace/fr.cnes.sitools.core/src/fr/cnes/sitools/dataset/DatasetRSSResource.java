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
package fr.cnes.sitools.dataset;

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
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource which returns a RSS representation of the DataSet
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class DatasetRSSResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("DatasetRSSResource");
    setDescription("Resource which returns a RSS representation of the dataset");
  }

  
  /**
   * Return the DataSet entries with an RSS representation
   * 
   * @param variant
   *          The Variant Parameters
   * @return the entries as a RSS feed
   */
  @Get
  public Representation getRSS(Variant variant) {
    Representation represent = null;

    DataSetApplication dsApp = (DataSetApplication) getApplication();
    DataSet ds = dsApp.getDataSet();

    Opensearch os = getOpensearch(ds.getId());

    String start = (this.getQuery().getFirstValue("start") != null) ? this.getQuery().getFirstValue("start") : "0";
    String limit = (this.getQuery().getFirstValue("limit") != null) ? this.getQuery().getFirstValue("limit") : "10";

    // '*:*' list every entry in the index
    String requestStr = "?q=*:*&start=" + start + "&limit=" + limit;
    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/" + os.getId() + "/execute" + requestStr);

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {

      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
    represent = response.getEntity();
    represent.setModificationDate(ds.getExpirationDate());

    return represent;
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a RSS representation of dataset entries");
    info.setIdentifier("retrieve_dataset_entries_asRSS");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
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

}
