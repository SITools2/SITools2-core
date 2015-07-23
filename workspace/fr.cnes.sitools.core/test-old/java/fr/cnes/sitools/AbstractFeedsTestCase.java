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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
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
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.FeedSource;
import fr.cnes.sitools.feeds.model.SitoolsFeedDateConverter;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Graph Rest API
 * 
 * @since UserStory : ADM Graphs, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@Ignore
public abstract class AbstractFeedsTestCase extends AbstractSitoolsServerTestCase {

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
   * ExternalUrl
   */
  private String externalUrl = "http://sitools.akka.eu/storage_test_odysseus/spw.rss";

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
      FeedModel item = createObject(dataId, "1000000");
      create(item);
      retrieve(item);
      getFeed(item);
      update(item);
      delete(item);
      assertNone();
    }
    catch (IOException e) {

      e.printStackTrace();
    }
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDExternalUrl() {
    docAPI.setActive(false);
    try {
      assertNone();
      FeedModel item = createObjectExternalUrl(dataId, "1000000", externalUrl);
      create(item);
      retrieve(item);
      getFeed(item);
      item.setExternalUrl(externalUrl + ".tests");
      update(item);
      delete(item);
      assertNone();
    }
    catch (IOException e) {

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
    docAPI.appendChapter("Manipulating Feeds Collection");
    docAPI.appendSubChapter("Get feeds list", "list");

    try {
      assertNone();
      FeedModel item = createObject(dataId, "1000000");

      docAPI.appendSubChapter("Creating a new Feed", "create");
      create(item);

      docAPI.appendChapter("Manipulating an existing Feed resource");

      docAPI.appendSubChapter("Retrieving a Feed", "retrieving");
      retrieve(item);
      if (getMediaTest().isCompatible(MediaType.APPLICATION_XML)) {
        docAPI.appendSubChapter("Retrieving a Feed in RSS or ATOM representation", "retrievingRss");
      }
      getFeed(item);

      docAPI.appendSubChapter("Updating a Feed", "updating");
      update(item);

      docAPI.appendSubChapter("Deleting a Feed", "deleting");
      delete(item);
      docAPI.close();
      assertNone();
    }
    catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws ParseException
   */
  @Test
  public void testSortEntries() throws ParseException {
    docAPI.setActive(false);

    FeedModel item = createObject(dataId, "1000000");
    AbstractFeedsResource.sortEntries(item);
    FeedModel itemEntriesSorted = createObjectEntriesSorted(dataId, "1000001");
    assertEntries(item.getEntries(), itemEntriesSorted.getEntries());

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
    item.setTitle("title");
    item.setName("name");
    item.setDescription("description");
    item.setFeedType("atom_1.0");
    item.setFeedSource(FeedSource.CLASSIC);

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
   * Create an object for tests
   * 
   * @param id
   *          feed id
   * @param idProject
   *          project id
   * @return FeedModel
   * @throws ParseException
   */
  public FeedModel createObjectEntriesSorted(String idProject, String id) throws ParseException {

    FeedModel item = new FeedModel();

    item.setId(id);
    item.setParent(idProject);
    item.setTitle("title");
    item.setName("name");
    item.setDescription("description");
    item.setFeedType("atom_1.0");
    item.setFeedSource(FeedSource.CLASSIC);

    FeedAuthorModel author = new FeedAuthorModel();
    author.setName("authorName");
    author.setEmail("authorEmail");

    item.setAuthor(author);
    item.setLink("http://link");
    item.setUri("http://uri");

    ArrayList<FeedEntryModel> entries = new ArrayList<FeedEntryModel>();

    FeedEntryModel entry3 = new FeedEntryModel();
    entry3.setTitle("title3");
    entry3.setDescription("description3");
    entry3.setUpdatedDate(DateUtils.parse("2013-03-01T00:00:00.000"));
    entry3.setPublishedDate(DateUtils.parse("2013-03-01T00:00:00.000"));
    entry3.setLink("link3");
    entries.add(entry3);

    FeedEntryModel entry2 = new FeedEntryModel();
    entry2.setTitle("title2");
    entry2.setDescription("description2");
    entry2.setUpdatedDate(DateUtils.parse("2013-02-01T00:00:00.000"));
    entry2.setPublishedDate(DateUtils.parse("2013-02-01T00:00:00.000"));
    entry2.setLink("link2");
    entries.add(entry2);

    FeedEntryModel entry1 = new FeedEntryModel();
    entry1.setTitle("title1");
    entry1.setDescription("description1");
    entry1.setUpdatedDate(DateUtils.parse("2013-01-01T00:00:00.000"));
    entry1.setPublishedDate(DateUtils.parse("2013-01-01T00:00:00.000"));
    entry1.setLink("link1");
    entries.add(entry1);

    item.setEntries(entries);

    return item;
  }

  private FeedModel createObjectExternalUrl(String idProject, String id, String externalUrl) {
    FeedModel item = new FeedModel();

    item.setId(id);
    item.setParent(idProject);
    item.setTitle("title");
    item.setName("name");
    item.setDescription("description");
    item.setFeedType("rss_2.0");
    item.setFeedSource(FeedSource.EXTERNAL);

    item.setExternalUrl(externalUrl);

    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          FeedModel
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(FeedModel item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), dataId);
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, FeedModel.class);
      assertTrue(response.getSuccess());
      FeedModel feedModel = (FeedModel) response.getItem();
      // sort entries
      AbstractFeedsResource.sortEntries(item);
      assertFeedModel(feedModel, item);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          FeedModel
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(FeedModel item) throws IOException {
    String url = String.format(getBaseUrl(), dataId) + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FeedModel.class);
      assertTrue(response.getSuccess());
      FeedModel feedModel = (FeedModel) response.getItem();
      assertFeedModel(feedModel, item);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          FeedModel
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(FeedModel item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), dataId) + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FeedModel.class);
      assertTrue(response.getSuccess());
      FeedModel feedModel = (FeedModel) response.getItem();
      assertFeedModel(feedModel, item);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          FeedModel
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFeed(FeedModel item) throws IOException {
    String url = getClientFeedUrl() + "/" + item.getName();
    ClientResource cr = new ClientResource(url);
    if (getMediaTest().isCompatible(MediaType.APPLICATION_XML)) {
      docAPI.appendRequest(Method.GET, cr);
    }

    Representation result = cr.get();
    if (getMediaTest().isCompatible(MediaType.APPLICATION_XML)) {
      if (!docAPI.appendResponse(result)) {
        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());
      }
    }
    else {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
    }
    System.out.println(result.getText());
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          Graph
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(FeedModel item) throws IOException {
    String url = String.format(getBaseUrl(), dataId) + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
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
      assertEquals(0, response.getTotal().intValue());
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

    assertEquals(feedModel.getTitle(), item.getTitle());
    assertEquals(feedModel.getId(), item.getId());
    assertEquals(feedModel.getDescription(), item.getDescription());
    assertEquals(feedModel.getLink(), item.getLink());
    assertEquals(feedModel.getFeedType(), item.getFeedType());
    assertEquals(feedModel.getFeedSource(), item.getFeedSource());
    if (FeedSource.CLASSIC.equals(item.getFeedSource())) {
      // assert author
      assertEquals(feedModel.getAuthor().getName(), item.getAuthor().getName());
      assertEquals(feedModel.getAuthor().getEmail(), item.getAuthor().getEmail());

      // assert entries
      assertEntries(feedModel.getEntries(), item.getEntries());
    }
    else if (FeedSource.EXTERNAL.equals(item.getFeedSource())) {
      assertEquals(feedModel.getExternalUrl(), item.getExternalUrl());
    }
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
    assertEquals(entries.size(), entries2.size());
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
      xstream.alias("FeedAuthorModel", FeedAuthorModel.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
        if (media.equals(MediaType.APPLICATION_JSON)) {
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
        return null;
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
      return null;

    }
  }

  /**
   * Configures XStream mapping for a Response object with a FeedModel content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("FeedModel", FeedModel.class);
    xstream.alias("FeedEntryModel", FeedEntryModel.class);
    xstream.alias("FeedAuthorModel", FeedAuthorModel.class);
  }

}
