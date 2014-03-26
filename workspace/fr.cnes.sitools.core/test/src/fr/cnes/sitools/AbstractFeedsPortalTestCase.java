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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.AbstractFeedsResource;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedCollectionModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.SitoolsFeedDateConverter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Graph Rest API
 * 
 * @since UserStory : ADM Graphs, Sprint : 8
 * 
 * @author m.gond (AKKA Technologies)
 */
public abstract class AbstractFeedsPortalTestCase extends AbstractSitoolsServerTestCase {

  /**
   * id of the project where the Feeds will be attached
   */
  private String dataId;
  /**
   * The base url
   */
  private String baseUrl;
  /**
   * The client baseUrl
   */
  private String clientBaseUrl;

  /**
   * The base url for datasets
   */
  private String baseDatasetUrl = super.getHostUrl() + AbstractSitoolsServerTestCase.SITOOLS_URL + "/datasets/%s"
      + SitoolsSettings.getInstance().getString(Consts.APP_FEEDS_URL);
  /**
   * DatasetId
   */
  private String datasetId = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /**
   * The base url for projects
   */
  private String baseProjectsUrl = super.getHostUrl() + AbstractSitoolsServerTestCase.SITOOLS_URL + "/projects/%s"
      + SitoolsSettings.getInstance().getString(Consts.APP_FEEDS_URL);

  /**
   * Project id
   */
  private String projectId = "350f9f7e-834f-4825-a218-03916c790e71";

  /**
   * Sets the dataId
   * 
   * @param dataId
   *          id of the project or dataset
   */
  protected void setDataId(String dataId) {
    this.dataId = dataId;
  }

  /**
   * Sets the base URL
   * 
   * @param baseUrl
   *          base url
   */
  protected void setbaseUrl(String baseUrl) {
    this.baseUrl = super.getHostUrl() + baseUrl;
  }

  /**
   * Sets the clientbaseUrl
   * 
   * @param clientbaseUrl
   *          clientbaseUrl url
   */
  protected void setClientbaseUrl(String clientbaseUrl) {
    this.clientBaseUrl = super.getHostUrl() + clientbaseUrl;
  }

  /**
   * Absolute URL for Feeds management
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return this.baseUrl + SitoolsSettings.getInstance().getString(Consts.APP_FEEDS_URL);
  }

  /**
   * Absolute URL for Feeds management
   * 
   * @return url
   */
  protected String getClientFeedUrl() {
    return this.clientBaseUrl + "/clientFeeds";
  }

  @Before
  @Override
  /**
   * Remove every feeds already on the store
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
    docAPI.setActive(false);

    // Map<String, Object> mapStore = settings.getStores();
    // FeedsStoreXML feedsStore = (FeedsStoreXML) mapStore.get(Consts.APP_STORE_FEED);
    //
    // List<FeedModel> list = feedsStore.getList();
    // for (Iterator<FeedModel> iterator = list.iterator(); iterator.hasNext();) {
    // feedsStore.delete(iterator.next().getId());
    // }
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws ParseException
   */
  @Test
  public void testCRUD() throws ParseException {
    docAPI.setActive(false);
    try {
      assertNone();
      // create feed for dataset
      FeedModel item = createObject(datasetId, "1000000");
      create(item, String.format(this.baseDatasetUrl, this.datasetId));
      // create feed for project
      FeedModel item2 = createObject(projectId, "1000001");
      create(item2, String.format(this.baseProjectsUrl, this.projectId));

      item.setVisible(false);
      item2.setVisible(false);

      FeedCollectionModel feedCollectionModel = new FeedCollectionModel();
      feedCollectionModel.getFeeds().add(item);
      feedCollectionModel.getFeeds().add(item2);

      update(feedCollectionModel);

      delete(item, String.format(this.baseDatasetUrl, this.datasetId));
      delete(item2, String.format(this.baseProjectsUrl, this.projectId));

      assertNone();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws ParseException
   */
  @Test
  public void testCRUD2docAPI() throws ParseException {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Feeds Collection for portal");
    docAPI
        .appendComment("Feeds cannot be created on a Portal but it is an agregation of feeds from dataset and project");
    docAPI.appendSubChapter("Getting Feeds list", "list");
    try {
      assertNone();
      docAPI.appendSubChapter("Creating a new Feed for dataset", "create");
      // create feed for dataset
      FeedModel item = createObject(datasetId, "1000000");
      create(item, String.format(this.baseDatasetUrl, this.datasetId));

      docAPI.appendSubChapter("Creating a new Feed for project", "project");
      // create feed for project
      FeedModel item2 = createObject(projectId, "1000001");
      create(item2, String.format(this.baseProjectsUrl, this.projectId));

      docAPI.appendSubChapter("Updating a list of Feeds", "updating");
      item.setVisible(false);
      item2.setVisible(false);

      FeedCollectionModel feedCollectionModel = new FeedCollectionModel();
      feedCollectionModel.getFeeds().add(item);
      feedCollectionModel.getFeeds().add(item2);

      update(feedCollectionModel);

      docAPI.appendSubChapter("Deleting a Feed for dataset", "deleting");
      delete(item, String.format(this.baseDatasetUrl, this.datasetId));
      docAPI.appendSubChapter("Deleting a Feed for project", "deleting");
      delete(item2, String.format(this.baseProjectsUrl, this.projectId));

      assertNone();
      docAPI.close();
      assertNone();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  /*
   * @Test public void testCRUDAndNotification() { docAPI.setActive(false); assertNone(); FeedModel item =
   * createObject(dataId, "1000000"); create(item); retrieve(item); getFeed(item); deleteData(); assertNone();
   * 
   * 
   * }
   */

  /**
   * Create an object for tests
   * 
   * @param id
   *          feed id
   * @param idProject
   *          project id
   * @return FeedModel
   * @throws ParseException
   */
  public FeedModel createObject(String idProject, String id) throws ParseException {

    FeedModel item = new FeedModel();

    item.setId(id);
    item.setParent(idProject);
    item.setName("name" + "_" + idProject);
    item.setTitle("title");
    item.setDescription("description");
    item.setFeedType("atom_1.0");

    FeedAuthorModel author = new FeedAuthorModel();
    author.setName("authorName");
    author.setEmail("authorEmail");

    item.setAuthor(author);
    item.setLink("http://link");
    item.setUri("http://uri");

    ArrayList<FeedEntryModel> entries = new ArrayList<FeedEntryModel>();

    FeedEntryModel entry1 = new FeedEntryModel();
    entry1.setTitle("title1");
    entry1.setDescription("description1");
    entry1.setUpdatedDate(DateUtils.parse("2013-01-01T00:00:00.000"));
    entry1.setPublishedDate(DateUtils.parse("2013-01-01T00:00:00.000"));
    entry1.setLink("link1");
    entries.add(entry1);

    FeedEntryModel entry2 = new FeedEntryModel();
    entry2.setTitle("title2");
    entry2.setDescription("description2");
    entry2.setUpdatedDate(DateUtils.parse("2013-02-01T00:00:00.000"));
    entry2.setPublishedDate(DateUtils.parse("2013-02-01T00:00:00.000"));
    entry2.setLink("link2");
    entries.add(entry2);

    FeedEntryModel entry3 = new FeedEntryModel();
    entry3.setTitle("title3");
    entry3.setDescription("description3");
    entry3.setUpdatedDate(DateUtils.parse("2013-03-01T00:00:00.000"));
    entry3.setPublishedDate(DateUtils.parse("2013-03-01T00:00:00.000"));
    entry3.setLink("link3");
    entries.add(entry3);

    item.setEntries(entries);

    return item;
  }

  /**
   * Invokes POST method for creating a feed associated with the Sitools portal.
   * 
   * @param item
   *          FeedModel
   * @param url
   *          String
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(FeedModel item, String url) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    // String url = String.format(getBaseUrl(), dataId);
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, FeedModel.class);
      assertTrue(response.getSuccess());
      FeedModel feedModel = (FeedModel) response.getItem();
      AbstractFeedsResource.sortEntries(item);
      assertFeedModel(feedModel, item);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param feedCollectionModel
   *          FeedModel
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(FeedCollectionModel feedCollectionModel) throws IOException {
    Representation rep = getRepresentation(feedCollectionModel, getMediaTest());
    String url = String.format(getBaseUrl(), dataId);
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FeedModel.class, true);
      assertTrue(response.getSuccess());

      assertEquals(new Integer(4), response.getTotal());

      /*
       * ArrayList<FeedModel> output = (ArrayList<FeedModel>) response.getItem(); assertNotVisible(output);
       */
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes DELETE method
   * 
   * @param item
   *          FeedModel
   * @param url
   *          String
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(FeedModel item, String url) throws IOException {

    ClientResource cr = new ClientResource(url + "/" + item.getId());
    docAPI.appendRequest(Method.DELETE, cr);

    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, FeedModel.class);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void assertNone() throws IOException {
    String url = String.format(getBaseUrl(), dataId);
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, FeedModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(2, response.getTotal().intValue());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * AssertEquals for FeedModel object
   * 
   * @param feedModel
   *          a feedModel
   * @param item
   *          another feedModel
   */
  private void assertFeedModel(FeedModel feedModel, FeedModel item) {
    // TODO Auto-generated method stub
    assertEquals(feedModel.getTitle(), item.getTitle());
    assertEquals(feedModel.getId(), item.getId());
    assertEquals(feedModel.getDescription(), item.getDescription());
    assertEquals(feedModel.getLink(), item.getLink());
    assertEquals(feedModel.getFeedType(), item.getFeedType());
    // assert author
    assertEquals(feedModel.getAuthor().getName(), item.getAuthor().getName());
    assertEquals(feedModel.getAuthor().getEmail(), item.getAuthor().getEmail());

    // assert entries
    assertEntries(feedModel.getEntries(), item.getEntries());
  }

  /**
   * AssertEquals for List of FeedEntryModel
   * 
   * @param entries
   *          a List of FeedEntryModel
   * @param entries2
   *          another List of FeedEntryModel
   */
  private void assertEntries(List<FeedEntryModel> entries, List<FeedEntryModel> entries2) {
    // TODO Auto-generated method stub

    assertEquals(entries.size(), entries2.size());
    Iterator<FeedEntryModel> iterator1 = entries.iterator();
    Iterator<FeedEntryModel> iterator2 = entries2.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      FeedEntryModel feedEntryModel1 = iterator1.next();
      FeedEntryModel feedEntryModel2 = iterator2.next();

      assertEquals(feedEntryModel1.getTitle(), feedEntryModel2.getTitle());
      assertEquals(feedEntryModel1.getDescription(), feedEntryModel2.getDescription());
      assertEquals(feedEntryModel1.getLink(), feedEntryModel2.getLink());
      assertEquals(feedEntryModel1.getUpdatedDate().toString(), feedEntryModel2.getUpdatedDate().toString());

    }
  }

  /**
   * Asserts all feeds are not visible.
   * 
   * @param feeds
   *          ArrayList<FeedModel>
   */
  private void assertNotVisible(ArrayList<FeedModel> feeds) {
    for (Iterator<FeedModel> iterator = feeds.iterator(); iterator.hasNext();) {
      FeedModel feedModel = iterator.next();
      assertFalse(feedModel.isVisible());
    }

  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION WRAPPING

  /**
   * REST API Response wrapper for single item expected.
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected in the item property of the Response object
   * @return Response the response.
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponse(media, representation, dataClass, false);
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("FeedModel", FeedModel.class);
      xstream.alias("FeedEntryModel", FeedEntryModel.class);
      xstream.alias("author", FeedAuthorModel.class);

      if (isArray) {
        // xstream.addImplicitCollection(Response.class, "data", dataClass);
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(FeedModel.class, "entries", FeedEntryModel.class);
        }

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(FeedModel.class, "entries", FeedEntryModel.class);
        }

        if (dataClass == FeedModel.class) {
          xstream.aliasField("FeedModel", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      // Convertisseur String // Date
      xstream.registerConverter(new SitoolsFeedDateConverter());

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          FeedModel
   * @param media
   *          MediaType
   * @return Representation
   */
  public static Representation getRepresentation(FeedModel response, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media);
      configure(xstream);

      // Convertisseur Date / TimeStamp
      xstream.registerConverter(new SitoolsFeedDateConverter());

      XstreamRepresentation<FeedModel> rep = new XstreamRepresentation<FeedModel>(media, response);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation

    }
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          FeedCollectionModel
   * @param media
   *          MediaType
   * @return Representation
   */
  public static Representation getRepresentation(FeedCollectionModel response, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media);
      configure(xstream);

      // Convertisseur Date / TimeStamp
      xstream.registerConverter(new SitoolsFeedDateConverter());

      XstreamRepresentation<FeedCollectionModel> rep = new XstreamRepresentation<FeedCollectionModel>(media, response);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation

    }
  }

  /**
   * Configures XStream mapping of a Response object with FeedModel content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("FeedModel", FeedModel.class);
    xstream.alias("FeedEntryModel", FeedEntryModel.class);
    xstream.alias("author", FeedAuthorModel.class);
    xstream.alias("FeedCollectionModel", FeedCollectionModel.class);
  }

}
