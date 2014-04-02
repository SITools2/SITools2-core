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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.SitoolsFeedDateConverter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Graph Rest API
 * 
 * @since UserStory : ADM Graphs, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class FeedsNotificationProjectTestCase extends AbstractSitoolsServerTestCase {

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

  /**
   * initialization of global settings for test
   * 
   * @throws java.lang.Exception
   *           if problem.
   */
  @Before
  public void setUp() throws Exception {
    super.setUp();
    setbaseUrl(AbstractSitoolsServerTestCase.SITOOLS_URL + "/projects/%s");
    setClientbaseUrl(AbstractSitoolsServerTestCase.SITOOLS_URL + "/projects/%s");
    setDataId("350f9f7e-834f-4825-a218-03916c790e71");

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDjson() {
    setMediaTest(MediaType.APPLICATION_JSON);
    /*
     * assertNone(); FeedModel item = createObject(dataId, "1000000"); create(item); deleteData();
     */
    // assertNone();

  }

  /*
   * @Test public void testCRUDxml() { setMediaTest(MediaType.APPLICATION_XML); assertNone(); FeedModel item =
   * createObject(dataId, "1000000"); create(item); deleteData(); //assertNone();
   * 
   * }
   */

  /**
   * Delete the Data object associated to the Feed
   */
  private void deleteData() {
    String url = String.format(this.baseUrl, dataId);
    ClientResource cr = new ClientResource(url);

    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, FeedModel.class);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
    cr.release();

  }

  /**
   * Create an object for tests
   * 
   * @param id
   *          feed id
   * @param idProject
   *          project id
   * @return FeedModel
   */
  public FeedModel createObject(String idProject, String id) {

    FeedModel item = new FeedModel();

    item.setId(id);
    item.setParent(idProject);
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
    entry1.setUpdatedDate(new Date());
    entry1.setLink("link1");
    entries.add(entry1);

    FeedEntryModel entry2 = new FeedEntryModel();
    entry2.setTitle("title2");
    entry2.setDescription("description2");
    entry2.setUpdatedDate(new Date());
    entry2.setLink("link2");
    entries.add(entry2);

    FeedEntryModel entry3 = new FeedEntryModel();
    entry3.setTitle("title3");
    entry3.setDescription("description3");
    entry3.setUpdatedDate(new Date());
    entry3.setLink("link3");
    entries.add(entry3);

    item.setEntries(entries);

    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          FeedModel
   */
  public void create(FeedModel item) {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), dataId);
    ClientResource cr = new ClientResource(url);

    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, FeedModel.class);
    assertTrue(response.getSuccess());
    FeedModel feedModel = (FeedModel) response.getItem();

    assertFeedModel(feedModel, item);
    RIAPUtils.exhaust(result);
    cr.release();

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    String url = String.format(getBaseUrl(), dataId);
    ClientResource cr = new ClientResource(url);

    Representation result = cr.get(getMediaTest());

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, FeedModel.class, true);
    assertTrue(response.getSuccess());
    assertEquals(0, response.getTotal().intValue());
    
    RIAPUtils.exhaust(result);
    cr.release();

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
   * @return RepresentationFeedModel representation
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
  }

}
