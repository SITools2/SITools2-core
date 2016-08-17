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
package fr.cnes.sitools.portal.multidatasets.opensearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.security.User;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.portal.multidatasets.opensearch.dto.OpensearchDescriptionDTO;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to handle list of multiple opensearch
 * 
 * @author AKKA Technologies
 */
public final class MutliDsOsCollectionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("MutliDsOsCollectionResource");
    setDescription("Resource to handle list of multiple opensearch for datasets");
    setNegotiated(false);
  }

  /**
   * Returns the list of opensearch feeds url
   * 
   * @param variant
   *          The variant
   * @return The list of opensearch feeds url
   */
  @Get
  public Representation getOpensearchList(Variant variant) {
    User user = this.getRequest().getClientInfo().getUser();

    ArrayList<OpensearchDescriptionDTO> attachmentList = new ArrayList<OpensearchDescriptionDTO>();
    OpensearchDescriptionDTO description;
    List<DataSet> dsList = this.getDatasets();

    String userIdentifier = (user == null) ? null : user.getIdentifier();

    AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();

    // si un utilisateur est connecte on verifie qu'il peut acceder aux datasets

    for (Iterator<DataSet> iterator = dsList.iterator(); iterator.hasNext();) {
      DataSet dataSet = iterator.next();

      // retrouver l'objet application
      SitoolsApplication myApp = appManager.getApplication(dataSet.getId());

      boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);
      if (authorized && "ACTIVE".equals(dataSet.getStatus())) {
        Opensearch os = this.getOpensearch(dataSet.getId());
        if (os != null && "ACTIVE".equals(os.getStatus())) {
          description = new OpensearchDescriptionDTO();
          description.setUrlFeed(dataSet.getSitoolsAttachementForUsers() + "/rss.xml");
          description.setUrlResource(dataSet.getSitoolsAttachementForUsers() + "/opensearch");
          description.setIdOs(os.getId());
          attachmentList.add(description);
        }
      }
    }

    Response response = new Response(true, attachmentList, OpensearchDescriptionDTO.class, "opensearchDescription");
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of opensearch feeds urls defined in the datasets of all projects");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Get the project model object using RIAP.
   * 
   * @return an project model object corresponding to the given id null if the
   *         is no project object corresponding to the given id
   * 
   */
  private List<DataSet> getDatasets() {
    return RIAPUtils.getListOfObjects(getSitoolsSetting(Consts.APP_DATASETS_URL), getContext());
  }

  /**
   * Get the opensearch model object using RIAP.
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
   * Response to Representation
   * 
   * @param response
   *          the response to treat
   * @param media
   *          the media to use
   * @return Representation
   */
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("response", Response.class);
    xstream.alias("opensearchDescription", OpensearchDescriptionDTO.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "OpensearchDescriptionDTO");
    }

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
