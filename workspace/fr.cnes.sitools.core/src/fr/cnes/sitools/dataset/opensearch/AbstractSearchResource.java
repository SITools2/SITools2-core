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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.dataset.opensearch.runnables.OpensearchRefreshRunnable;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.solr.model.RssXSLTDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract Resource class for Forms management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractSearchResource extends SitoolsResource {

  /** parent application */
  private OpenSearchApplication application = null;

  /** store */
  private SitoolsStore<Opensearch> store = null;

  /** other store needed for Feeds definition */
  private SitoolsStore<FeedModel> storeFeed = null;

  /** DataSet identifier parameter */
  private String datasetId = null;
  /**
   * Solr index name
   */
  private String indexName = null;

  /**
   * Default constructor
   */
  public AbstractSearchResource() {
    super();
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public final OpenSearchApplication getOpenSearchApplication() {
    return application;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<Opensearch> getStore() {
    return store;
  }

  /**
   * Gets the storeFeed value
   * 
   * @return the storeFeed
   */
  public final SitoolsStore<FeedModel> getStoreFeed() {
    return storeFeed;
  }

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public final String getDatasetId() {
    return datasetId;
  }

  /**
   * Gets the indexName value
   * 
   * @return the indexName
   */
  public final String getIndexName() {
    return indexName;
  }

  /**
   * Sets the value of indexName
   * 
   * @param indexName
   *          the indexName to set
   */
  public final void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  @Override
  public void doInit() {
    super.doInit();

    // // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (OpenSearchApplication) getApplication();
    store = application.getStore();
    storeFeed = application.getStoreFeed();

    datasetId = (String) this.getRequest().getAttributes().get("datasetId");
    if (datasetId == null) {
      datasetId = (String) this.getRequest().getAttributes().get("opensearchId");
    }

  }

  /**
   * Gets representation according to the specified MediaType.
   * 
   * @param response
   *          : The response to get the representation from
   * @param media
   *          : The MediaType asked
   * @return The Representation of the response with the selected mediaType
   */
  public Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("opensearch", Opensearch.class);
    xstream.alias("opensearchColumn", OpensearchColumn.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The opensearch
   */
  public void registerObserver(Opensearch input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();
    // passage en RIAP
    String uriToNotify = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + input.getParent()
        + getSitoolsSetting(Consts.APP_OPENSEARCH_URL) + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("Opensearch." + input.getId());

    notificationManager.addObserver(input.getParent(), observer);
  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          OpenSearch Object
   */
  public void unregisterObserver(Opensearch input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    notificationManager.removeObserver(input.getParent(), "Opensearch." + input.getId());
  }

  /**
   * Delete the feed associated to that OpenSearch
   */
  protected void deleteFeedOpensearch() {
    // TODO Auto-generated method stub
    this.storeFeed.delete(datasetId);
  }

  /**
   * Stops the SolrIndex
   * 
   * @param os
   *          the OpenSearch model object
   * @return A response
   */
  protected Response stopOsIndex(Opensearch os) {
    Response response;
    if (!"ACTIVE".equals(os.getStatus())) {
      response = new Response(false, "OPENSEARCH_INACTIVE");
    }
    else {
      Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/"
          + os.getId() + "/delete");
      ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
      objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
      reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
      org.restlet.Response responseSolr = null;
      try {
        responseSolr = getContext().getClientDispatcher().handle(reqPOST);

        if (responseSolr == null || Status.isError(responseSolr.getStatus().getCode())) {
          response = new Response(false, "OPENSEARCH_REFRESH_ERROR");
        }
        else {
          @SuppressWarnings("unchecked")
          XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) responseSolr.getEntity();
          Response resp = (Response) repr.getObject();
          if (resp.getSuccess()) {
            os.setStatus("INACTIVE");
            Opensearch osResult = store.update(os);
            response = new Response(true, osResult, Opensearch.class, "opensearch");
          }
          else {
            response = new Response(false, resp.getMessage());
          }
        }
      }
      finally {
        RIAPUtils.exhaust(responseSolr);
      }
    }
    return response;
  }

  /**
   * Refresh OpenSearch Index
   * 
   * @param os
   *          the OpenSearch model
   * @return a Response
   */
  protected Response refreshOsIndex(Opensearch os) {
    Response response;
    if ("ACTIVE".equals(os.getStatus())) {
      // clear the error msg
      os.setErrorMsg(null);
      // set the status to pending
      os.setStatus("PENDING");
      // set cancelled to false
      this.application.setCancelled(false);

      Opensearch osResult = store.update(os);
      // create a runnable task to refresh asynchronously
      OpensearchRefreshRunnable osRunnable = new OpensearchRefreshRunnable(os, store,
          getSitoolsSetting(Consts.APP_SOLR_URL), this.getContext(), this.application);
      // run the task
      application.getTaskService().execute(osRunnable);

      response = new Response(true, osResult, Opensearch.class, "opensearch");
    }
    else {
      response = new Response(false, "OPENSEARCH_INACTIVE");
    }
    return response;
  }

  /**
   * Get the DataSet model object using RIAP
   * 
   * @param id
   *          : the DataSet model object id
   * @return an DataSet model object corresponding to the given id null if there is no dataset object corresponding to
   *         the given id
   * 
   */
  protected DataSet getDataset(String id) {
    return RIAPUtils.getObject(id, getSitoolsSetting(Consts.APP_DATASETS_URL), getContext());
  }

  /**
   * Gets the definitions of the indexed columns. Loop through each column of the dataset and check if it has to be
   * indexed
   * 
   * @param ds
   *          the DataSet model object
   * @param os
   *          the OpenSearch model object
   * @return an ArrayList<Column> with all the columns to index
   */
  protected List<Column> getIndexedColumns(final DataSet ds, final Opensearch os) {
    // the list of indexed columns in the opensearch index
    List<Column> columnsIndexed = new ArrayList<Column>();
    // the list of columns of the dataset
    List<Column> columns = ds.getColumnModel();
    // the list of Ids of columns stored in the opensearch Model
    List<OpensearchColumn> columnsModel = os.getIndexedColumns();

    // some iterators for the loop
    Iterator<Column> it = columns.iterator();
    Iterator<OpensearchColumn> itColMod;
    Column col;
    // some boolean not to loop
    boolean allFound = false;
    boolean currentFound;
    // loop through the columns of the dataset to find the column model
    // corresponding to the opensearch definition
    while (it.hasNext() && !allFound) {
      currentFound = false;
      col = it.next();
      itColMod = columnsModel.iterator();
      while (itColMod.hasNext() && !currentFound) {
        OpensearchColumn colModel = itColMod.next();
        String id = colModel.getIdColumn();
        if (id.equals(col.getId())) {
          columnsIndexed.add(col);
          // if the numbers of columns are the same we have found all the
          // columns definitions we need
          if (columnsIndexed.size() == columnsModel.size()) {
            allFound = true;
          }
        }
      }
    }
    return columnsIndexed;
  }

  /**
   * Return the RssXSLTDTO model object
   * 
   * @param ds
   *          the datasetId
   * @param columns
   *          the columns definition
   * @param os
   *          OpenSearch
   * @return the RssXSLTDTO model object
   */
  protected RssXSLTDTO getRssXSLTDTO(final DataSet ds, final List<Column> columns, Opensearch os) {

    RssXSLTDTO rssXSLT = new RssXSLTDTO();

    Iterator<Column> itCol = columns.iterator();
    Column col;
    while (itCol.hasNext()) {
      col = itCol.next();
      setReturnedField(col.getColumnAlias(), os, rssXSLT, col);
    }
    String publicHostDomain = getOpenSearchApplication().getSettings().getPublicHostDomain();
    rssXSLT.setDatasetDescription(ds.getDescription());
    rssXSLT.setDatasetName(ds.getName());
    rssXSLT.setDatasetURI(publicHostDomain + ds.getSitoolsAttachementForUsers());
    rssXSLT.setIndexName(this.indexName);
    rssXSLT.setFeedUrl(publicHostDomain + ds.getSitoolsAttachementForUsers() + "/opensearch/search?q=");

    return rssXSLT;

  }

  /**
   * Sets the returned field with the corresponding field
   * 
   * @param field
   *          the field to set
   * @param os
   *          the OpenSearch object model
   * @param returnedField
   *          the returnedField model object
   * @param col
   *          the column
   */
  protected void setReturnedField(final String field, final Opensearch os, RssXSLTDTO returnedField, final Column col) {

    if (col.getId().equals(os.getDescriptionField())) {
      returnedField.setDescription(field);
    }
    if (col.getId().equals(os.getGuidField())) {
      returnedField.setGuid(field);
    }
    if (col.getId().equals(os.getLinkField())) {
      returnedField.setLink(field);
    }
    if (col.getId().equals(os.getPubDateField())) {
      returnedField.setPubDate(field);
    }
    if (col.getId().equals(os.getTitleField())) {
      returnedField.setTitle(field);
    }
    if (col.getId().equals(os.getUniqueKey())) {
      returnedField.setUniqueKey(field);
    }

  }

}
