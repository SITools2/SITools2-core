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
package fr.cnes.sitools.service.storage;

import java.util.ArrayList;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.FeedSource;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.service.storage.runnables.IndexRefreshRunnable;
import fr.cnes.sitools.solr.directory.DirectoryConfigDTO;
import fr.cnes.sitools.solr.model.SolRConfigDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Actions on StorageDirectory
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class SearchActionResource extends AbstractStorageResource {
  /**
   * Storage model object
   */
  private StorageDirectory sd;

  @Override
  public void sitoolsDescribe() {
    setDescription("Actions on Search directory service configuration => Solr index management");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    // on charge la config
// FIXME    
//    sd = getStore().retrieve(getDatasetId());
//    if (sd != null) {
//      setIndexName(sd.getId());
//    }
  }

  /**
   * Executes the actions on index. Actions available are start, stop, refresh
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          the variant parameter
   * @return a representation
   */
  @Put
  public Representation action(Representation representation, Variant variant) {
    Response response = null;

    do {

      if (sd == null) {
        response = new Response(false, "OPENSEARCH_NOT_FOUND");
        break;
      }

      if (this.getReference().toString().endsWith("start")) {
        if ("ACTIVE".equals(sd.getStatus())) {
          response = new Response(false, "OPENSEARCH_ACTIVE");
          break;
        }
        
        // creation de l'index SolR
        Response resp = createSolRIndex(sd);
        if (resp.isSuccess()) {
          // Register Opensearch as observer of datasets resources
//          unregisterObserver(sd);
//          registerObserver(sd);

//          this.sd.setErrorMsg(null);
          this.sd.setStatus("PENDING");

          createFeedOpensearch();

        }
        else {
          this.sd.setStatus("INACTIVE");
//          this.sd.setErrorMsg(resp.getMessage());
        }
//        this.getOpenSearchApplication().setCancelled(false);
        // TODO DataStorageStore sdResult = 
        getStore().update(sd);
        response = new Response(true, sd, StorageDirectory.class, "storage");

      }

      if (this.getReference().toString().endsWith("refresh")) {
        response = refreshSDIndex(sd);
      }

      if (this.getReference().toString().endsWith("stop")) {
        response = stopSDIndex(sd);
// FIXME        deleteFeedOpensearch();

      }
      if (this.getReference().toString().endsWith("cancel")) {
        response = cancelCurrentOperation(sd);
      }
    } while (false);
    // Response

    Representation rep = getRepresentation(response, variant);

    return rep;
  }

  /**
   * Cancel the current operation
   * 
   * @param os2
   *          the opensearch to cancel
   * @return a response
   */
  private Response cancelCurrentOperation(StorageDirectory os2) {
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/"
        + os2.getId() + "/cancel");
    Response response = null;

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response responseSolr = null;
    try {
      responseSolr = getContext().getClientDispatcher().handle(reqPOST);

      if (responseSolr == null || Status.isError(responseSolr.getStatus().getCode())) {
        response = new Response(false, "OPENSEARCH_CANCEL_ERROR");
      }
      else {
        @SuppressWarnings("unchecked")
        XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) responseSolr.getEntity();
        Response resp = (Response) repr.getObject();
        if (resp.getSuccess()) {
//          this.getOpenSearchApplication().setCancelled(true);
          response = new Response(true, "opensearch.cancel.successfull");
        }
        else {
          response = new Response(false, resp.getMessage());
        }
      }
      return response;
    }
    finally {
      RIAPUtils.exhaust(responseSolr);
    }
  }

  @Override
  public void describePut(MethodInfo info, String path) {
    if (path.endsWith("start")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Activates the opensearch service on the dataset making it available for the DataSetApplication API users.");
    }
    else if (path.endsWith("stop")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Disactivates the opensearch service on the dataset making it unavailable for the DataSetApplication API users.");
    }
    else if (path.endsWith("refresh")) {
      info.setDocumentation(" PUT /" + path
          + " : Regenerates the opensearch service indexes. This operation is asynchronous.");
    }
    else if (path.endsWith("cancel")) {
      info.setDocumentation(" PUT /" + path + " : Interrupts the task of lucene indexes generation.");
    }
    else {
      info.setDocumentation("Method to modify the opensearch service status.");
    }

    info.setIdentifier(" PUT /" + path);
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Creates the FeedModel bean associated to that OpenSearch
   */
  private void createFeedOpensearch() {
    // we create a feedModel representing the feed on this opensearch

    FeedModel feed = new FeedModel();

    feed.setFeedSource(FeedSource.OPENSEARCH);
    feed.setParent(sd.getId());
    feed.setId(sd.getId());
    feed.setName(sd.getName());
    feed.setDescription(sd.getDescription());
    feed.setTitle(sd.getName());
    feed.setFeedType("rss_2.0");

// FIXME    this.getStoreFeed().create(feed);

  }

  /**
   * creation l'index solR.
   * 
   * @param sd
   *          the StorageDirectory object
   * @return The response of the server
   */
  @SuppressWarnings("unchecked")
  private Response createSolRIndex(final StorageDirectory sd) {
    // ... Activer => Creer l'index

//    List<Column> columns = this.getIndexedColumns(sd, os);

    SolRConfigDTO solRcDTO = new SolRConfigDTO();

    DirectoryConfigDTO dataConf = createSolRdirectoryConfig(sd);

    //    SchemaConfigDTO scDTO = createSolRSchema(sd, columns, solRcDTO, os);
    // set the rssXSLTDTO
    
//    RssXSLTDTO rssConfig = getRssXSLTDTO(sd);
//    
//    solRcDTO.setRssXSLTDTO(rssConfig);

    // solRcDTO.setIndexName(this.getIndexName());
    // solRcDTO.setSchemaConfigDTO(scDTO);

    solRcDTO.setDataConfigDTO(dataConf);

    // create the core
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL)
        + "/create", new ObjectRepresentation<SolRConfigDTO>(solRcDTO));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response r = null;
    try {
      r = getContext().getClientDispatcher().handle(reqPOST);

      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) r.getEntity();
      Response resp = (Response) repr.getObject();

      if (resp.isSuccess()) {
        // create a runnable task to index asynchronously
        IndexRefreshRunnable sdRunnable = new IndexRefreshRunnable(sd, getStore(),
            getSitoolsSetting(Consts.APP_SOLR_URL), this.getContext(), this.getStorageAdministration());
        // run the task
        getApplication().getTaskService().execute(sdRunnable);

      }
      return resp;
    }
    finally {
      RIAPUtils.exhaust(r);
    }

  }

//  /**
//   * SolR configuration, schema part. And set returned field
//   * 
//   * @param solRConf
//   *          the solRConf model object
//   * @return a schemaConfigDTO
//   */
//  private SchemaConfigDTO createSolRSchema( SolRConfigDTO solRConf,
//      StorageDirectory os) {
//
//    SchemaConfigDTO scDTO = new SchemaConfigDTO();
//    // List of SchemaFields
//    List<SchemaFieldDTO> fields = new ArrayList<SchemaFieldDTO>();
//    // A schema field used in the loop
//    SchemaFieldDTO field;
//
//    // ...
//    
//    scDTO.setFields(fields);
//    return scDTO;
//  }


  /**
   * SolR configuration, directory configuration part
   * 
   * @param sd
   *          the storage directory

   * @return a DataConfigDTO model object
   */
  private DirectoryConfigDTO createSolRdirectoryConfig(StorageDirectory sd) {

    DirectoryConfigDTO dataConf = new DirectoryConfigDTO();
    dataConf.setDocument(sd.getId());
    dataConf.setBaseDir(sd.getLocalPath());
    // dataConf.setFileName(ds.getDefaultFileName());
    // dataConf.setNewerThan(ds.getDefaultNewerThan());

    return dataConf;
  }

}
