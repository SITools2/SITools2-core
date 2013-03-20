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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource for refreshing OpenSearch definition when DataSet change notification.
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class RefreshDataSetResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("RefreshDataSetResource");
    setDescription("Refresh dataset => modification of the dataset date");
  }

  /**
   * Method for refreshing OpenSearch definition when dataset change notification.
   * 
   * @param id
   *          String DataSet identifier
   * @return OpenSearch
   */
  @SuppressWarnings("unused")
  private Opensearch refreshOpensearch(String id) {
    Request reqPUT = new Request(Method.PUT, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_DATASETS_URL) + "/"
        + id + "/opensearch/refresh");
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPUT.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqPUT);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      return null;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        return null;
      }
      Opensearch opensearch = (Opensearch) resp.getItem();
      return opensearch;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Actions on PUT
   * 
   * @param representation
   *          could be null.
   * @param variant
   *          MediaType of response
   * @return Representation response
   */
  @Put
  public Representation validate(Representation representation, Variant variant) {
    Response response = null;

    do {
      // on charge le dataset
      DataSet ds = store.retrieve(datasetId);
      if (ds == null) {
        response = new Response(false, "dataset.refresh.notfound");
        break;
      }

      if (!"ACTIVE".equals(ds.getStatus())) {
        response = new Response(false, "dataset.refresh.statuserror");
        break;
      }

      try {

        ds.setExpirationDate(new Date(new GregorianCalendar().getTime().getTime()));

        // ds.refreshNotion(getContext(), getSitoolsSetting(Consts.APP_DICTIONARIES_URL));

        DataSet dsResult = store.update(ds);
        response = new Response(true, dsResult, DataSet.class);
        response.setMessage("dataset.refresh.success");
      }
      catch (Exception e) {
        response = new Response(false, "dataset.refresh.error");
      }
    } while (false);

    // Response
    return getRepresentation(response, variant);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to refresh dataset status");
    info.setIdentifier("update_dataset");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    info.getRequest().getParameters().add(pic);
    addStandardSimpleResponseInfo(info);
  }

}
