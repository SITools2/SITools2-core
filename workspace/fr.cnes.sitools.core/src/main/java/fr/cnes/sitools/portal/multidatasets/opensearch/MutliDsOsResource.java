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
package fr.cnes.sitools.portal.multidatasets.opensearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.portal.model.Portal;
import fr.cnes.sitools.portal.multidatasets.opensearch.dto.OpensearchDescriptionDTO;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to handle multiple datasets opensearch request
 * 
 * @author m.gond (AKKA Technologies)
 * 
 * @version
 * 
 */
public final class MutliDsOsResource extends SitoolsResource {

  /**
   * Number of results asked by the client, default to 10
   */
  private int nbResults = 10;

  /**
   * Search query
   */
  private String searchQuery;
  /**
   * The list of OpensearchDescriptionDTO
   */
  private List<OpensearchDescriptionDTO> osList;

  /** A map of opensearch object */
  private Map<String, Opensearch> osMap;

  /**
   * The portal object
   */
  private Portal portal;

  @Override
  public void sitoolsDescribe() {
    setName("MutliDsOsResource");
    setDescription("Handle multiple datasets opensearch request");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();

    Form query = this.getRequest().getResourceRef().getQueryAsForm();
    if (query.getFirstValue("nbResults") != null) {
      nbResults = new Integer(query.getFirstValue("nbResults"));
    }
    searchQuery = (String) query.getFirstValue("q");

  }

  /**
   * Handle the search over multiple opensearch index
   * 
   * @return The result of the search over multiple opensearch index
   */
  @Get
  public Representation getMutliDsOpensearch() {
    Representation repr = null;
    // get the client role
    ClientInfo clientInfo = this.getRequest().getClientInfo();

    // get the list of opensearch according the the client role
    osList = getOsList(clientInfo);

    if (this.getReference().getBaseRef().toString().endsWith("search")) {

      // get the portal details
      this.setPortal(getPortalDetails());

      repr = new MutliDsOsSearchRepresentation(MediaType.APPLICATION_RSS, this);

    }
    else if (this.getReference().getBaseRef().toString().endsWith("suggest")) {
      this.fillOsMap();
      repr = new MultiDsOsSuggestRepresentation(MediaType.APPLICATION_JSON, this);

    }
    return repr;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the portal details of feeds definition for search and suggest.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Fill the Opensearch Map with Opensearch object
   */
  private void fillOsMap() {
    this.osMap = new ConcurrentHashMap<String, Opensearch>();

    for (Iterator<OpensearchDescriptionDTO> iterator = osList.iterator(); iterator.hasNext();) {
      OpensearchDescriptionDTO osDesc = iterator.next();
      Opensearch os = getOpensearch(osDesc.getIdOs());
      if (os != null) {
        this.osMap.put(os.getId(), os);
      }
    }
  }

  /**
   * Get the RSS result of an opensearch query
   * 
   * @param query
   *          The query
   * @param osId
   *          The opensearchId
   * @return A String representing the RSS result of an opensearch query
   */
  public String getOpensearchRSS(String query, String osId) {
    String url = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/" + osId + "/execute" + query;

    Request reqGET = new Request(Method.GET, url);

    List<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

    String repr = null;

    if (response == null || Status.isError(response.getStatus().getCode())) {
      return null;
    }

    try {
      repr = response.getEntity().getText();
    }
    catch (IOException e) {
      getLogger().log(Level.SEVERE, "response exhaust fault", e);
    }
    return repr;

  }

  /**
   * Get the list of Opensearch.
   * 
   * @param clientInfo
   *          The clientInfo
   * @return an project model object corresponding to the given id null if the
   *         is no project object corresponding to the given id
   * 
   */
  private ArrayList<OpensearchDescriptionDTO> getOsList(ClientInfo clientInfo) {
    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_PORTAL_URL)
        + "/opensearch/list");
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));

    reqGET.setClientInfo(clientInfo);
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

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
      ArrayList<OpensearchDescriptionDTO> listOs = new ArrayList<OpensearchDescriptionDTO>();
      ArrayList<Object> list = resp.getData();
      for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
        OpensearchDescriptionDTO osDesc = (OpensearchDescriptionDTO) iterator.next();
        listOs.add(osDesc);
      }
      return listOs;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Get the Suggest Representation for a given query and Opensearch Id
   * 
   * @param query
   *          The query
   * @param osId
   *          the Opensearch Id
   * @param mediaType
   *          the mediaType needed
   * @return a representation representing the suggest for a given query
   */
  public String getOpensearchSuggest(String query, String osId, MediaType mediaType) {
    String suggest = null;
    Opensearch os = this.osMap.get(osId);
    if (os != null) {

      ArrayList<String> fieldList = os.getKeywordColumns();
      String fields = fieldList.toString().replace("[", "").replace("]", "").replace(" ", "");

      String reqString = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/" + os.getId() + "/"
          + fields + "/suggest?q=" + query;

      Request reqGET = new Request(Method.GET, reqString);

      ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
      objectMediaType.add(new Preference<MediaType>(mediaType));
      reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
      org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

      if (response == null || Status.isError(response.getStatus().getCode())) {
        return null;
      }

      try {
        suggest = response.getEntity().getText();
      }
      catch (IOException e) {
        getLogger().log(Level.SEVERE, "response exhaust fault", e);
      }
    }
    return suggest;
  }

  /**
   * Return the default portal, index 0
   * 
   * @return the default portal, index 0
   */
  private Portal getPortalDetails() {
    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_PORTAL_URL));
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

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
      ArrayList<Object> list = resp.getData();
      Portal portalTmp = null;
      // get the first portal
      if (list.size() > 0) {
        portalTmp = (Portal) list.get(0);
      }
      return portalTmp;

    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Get the opensearch model object using RIAP
   * 
   * @param id
   *          : the opensearch model object id
   * @return an opensearch model object corresponding to the given id null if
   *         the is no opensearch object corresponding to the given id
   * 
   */
  private Opensearch getOpensearch(String id) {
    return RIAPUtils.getObject(getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + id
        + getSitoolsSetting(Consts.APP_OPENSEARCH_URL), getContext());
  }

  /**
   * Sets the value of osList
   * 
   * @param osList
   *          the osList to set
   */
  public void setOsList(List<OpensearchDescriptionDTO> osList) {
    this.osList = osList;
  }

  /**
   * Gets the osList value
   * 
   * @return the osList
   */
  public List<OpensearchDescriptionDTO> getOsList() {
    return osList;
  }

  /**
   * Gets the nbResults value
   * 
   * @return the nbResults
   */
  public int getNbResults() {
    return nbResults;
  }

  /**
   * Sets the value of nbResults
   * 
   * @param nbResults
   *          the nbResults to set
   */
  public void setNbResults(int nbResults) {
    this.nbResults = nbResults;
  }

  /**
   * Gets the searchQuery value
   * 
   * @return the searchQuery
   */
  public String getSearchQuery() {
    return searchQuery;
  }

  /**
   * Sets the value of searchQuery
   * 
   * @param searchQuery
   *          the searchQuery to set
   */
  public void setSearchQuery(String searchQuery) {
    this.searchQuery = searchQuery;
  }

  /**
   * Sets the value of portal
   * 
   * @param portal
   *          the portal to set
   */
  public void setPortal(Portal portal) {
    this.portal = portal;
  }

  /**
   * Gets the portal value
   * 
   * @return the portal
   */
  public Portal getPortal() {
    return portal;
  }

}
