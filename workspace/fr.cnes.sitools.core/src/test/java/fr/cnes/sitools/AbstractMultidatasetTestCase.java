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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.form.project.services.dto.DataSetQueryStatusDTO;
import fr.cnes.sitools.form.project.services.dto.DatasetQueryStatus;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.AbstractTaskResourceTestCase;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

@Ignore
public class AbstractMultidatasetTestCase extends AbstractTaskResourceTestCase {
  /** fuse dataset id */
  private String fuseDsId = "6a5a7a2a-f4c9-4c97-add6-eb60bd0b1c69";
  /** headers dataset id */
  private String headersDsId = "6cb316f7-a08f-4b69-a412-441e0e32a772";

  /** user login */
  private String userLogin = "admin";
  /** user password */
  private String password = "admin";
  /** Search service resource attachment */
  private String urlAttachResource = "/multids/search";
  /** Properties search service resource attachment */
  private String urlAttachResourceProperty = "/multids/propertySearch";
  /** Project id */
  private String projectId = "premier";
  /** Project Url */
  private String projectUrl = "/proj/premier";
  /** CollectionId */
  private String collectionId = "61fdf37c-09eb-4ea5-b67f-f33139757b76";
  /** DictionaryId */
  private String dictionaryId = "6caf5368-6bbd-49c7-be43-9ae95cbb5ff6";
  /** Search service resource class name */
  private String mutlidsSearchModelClassName = "fr.cnes.sitools.form.project.services.ServiceDatasetSearchResourceModel";
  /** Properties search service resource class name */
  private String mutlidsPropertySearchModelClassName = "fr.cnes.sitools.form.project.services.ServicePropertiesSearchResourceModel";

  /** Search service resource ResourceModel, used during test execution */
  private ResourceModel taskResourceSearch;
  /** Properties Search service resource ResourceModel, used during test execution */
  private ResourceModel taskResourcePropertySearch;

  /**
   * Get the base Url for the project with projectId identifier
   * 
   * @return the base Url for the project with projectId identifier
   */
  private String getBaseProjectUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL) + "/" + projectId;

  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() {
    taskResourceSearch = null;
    try {
      taskResourceSearch = createResourceModel(mutlidsSearchModelClassName, "1000", urlAttachResource);
      taskResourceSearch = fillMultidsSearchParameter(taskResourceSearch);
      taskResourceSearch.getParameterByName("nbDatasetsMax").setValue("2");
      create(taskResourceSearch, getBaseProjectUrl());

      taskResourcePropertySearch = createResourceModel(mutlidsPropertySearchModelClassName, "1000126",
          urlAttachResourceProperty);
      taskResourcePropertySearch = fillMultidsSearchParameter(taskResourcePropertySearch);
      create(taskResourcePropertySearch, getBaseProjectUrl());
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    if (taskResourceSearch != null) {
      delete(taskResourceSearch, getBaseProjectUrl());
    }
    if (taskResourcePropertySearch != null) {
      delete(taskResourcePropertySearch, getBaseProjectUrl());
    }
  }

  /**
   * <ul>
   * <li>Creates some datasets (int@8.xml, int@9.xml)</li>
   * <li>Add some dictionary mapping to those datasets</li>
   * <li>Join them into a collection (int@0.xml)</li>
   * <li>create a multidataset search resource on the project</li>
   * <li>Query the multidataset and wait for results</li>
   * </ul>
   * 
   * @throws InterruptedException
   *           if any thread has interrupted the current thread. The interrupted status of the current thread is cleared
   *           when this exception is thrown.
   */
  @Test
  public void testMultidataset() throws InterruptedException {

    // query the ressource
    String parameters = "c[0]=TEXTFIELD|TestDictionary,name|A0*";
    TaskModel taskModel = null;
    try {
      taskModel = queryMultidataset(parameters, false);

      taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);
      assertStatusSuccess(taskModel);

      assertMultidatasetResults(taskModel, 217, 40);

    }
    finally {
      if (taskModel != null) {
        deleteTask(userLogin, taskModel.getId(), password, false);
      }
    }
  }

  /**
   * <ul>
   * <li>Creates some datasets (int@8.xml, int@9.xml)</li>
   * <li>Add some dictionary mapping to those datasets</li>
   * <li>Join them into a collection (int@0.xml)</li>
   * <li>create a multidataset search resource on the project</li>
   * <li>Query the multidataset and wait for results</li>
   * </ul>
   * 
   * @throws InterruptedException
   *           if any thread has interrupted the current thread. The interrupted status of the current thread is cleared
   *           when this exception is thrown.
   */
  @Test
  public void testMultidatasetAsPublic() throws InterruptedException {

    // query the ressource
    String parameters = "c[0]=TEXTFIELD|TestDictionary,name|A0*";
    TaskModel taskModel = null;
    try {
      taskModel = queryMultidataset(parameters, true);

      taskModel = testGetAsyncTaskModel(taskModel, null, null, true);
      assertStatusSuccess(taskModel);

      assertMultidatasetResults(taskModel, 217, 40);

    }
    finally {
      if (taskModel != null) {
        deleteTask("public", taskModel.getId(), null, true);
      }
    }
  }

  /**
   * Test the multidataset service but expect an error on each dataset because a String filter cannot be applied on a
   * numeric column
   * 
   * @throws InterruptedException
   *           if any thread has interrupted the current thread. The interrupted status of the current thread is cleared
   *           when this exception is thrown.
   */
  @Test
  public void testMultidatasetWithError() throws InterruptedException {

    // query the ressource
    String parameters = "c[0]=TEXTFIELD|TestDictionary,x|A0*";
    TaskModel taskModel = null;
    try {
      taskModel = queryMultidataset(parameters, false);

      taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);
      assertStatusSuccess(taskModel);

      assertMutlidatasetResultsError(taskModel);

    }
    finally {
      if (taskModel != null) {
        deleteTask(userLogin, taskModel.getId(), password, false);
      }
    }
  }

  /**
   * Test the mutlidataset service but expect an error because the number of datasets asked is superior to maximum
   * allowed in the service
   * 
   * @throws InterruptedException
   *           if any thread has interrupted the current thread. The interrupted status of the current thread is cleared
   *           when this exception is thrown.
   */
  @Test
  public void testMultidatasetErrorToManyDatasets() throws InterruptedException {

    // query the ressource
    String parameters = "c[0]=TEXTFIELD|TestDictionary,name|A0*&datasetsList=" + fuseDsId + "|" + headersDsId
        + "|58912a26-5065-42f4-921c-54ea998037df";
    TaskModel taskModel = null;
    try {
      taskModel = queryMultidataset(parameters, false);

      taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);
      assertStatusError(taskModel);
    }
    finally {
      if (taskModel != null) {
        deleteTask(userLogin, taskModel.getId(), password, false);
      }
    }
  }

  /**
   * Test of the properties service
   */
  @Test
  public void testMultidatasetPropertySearch() {
    // test with no parameter => retrieve all datasets
    String parameters = "";
    queryMultidatasetProperties(parameters, 2);
    // test with common property and correct value (values are the same) => retrieve all datasets
    parameters = "?k[0]=TEXTFIELD|galaxy|m31";
    queryMultidatasetProperties(parameters, 2);
    // test with common property and correct value (values are not the same) => retrieve all datasets
    parameters = "?k[0]=NUMERIC_BETWEEN|dec|50|150";
    queryMultidatasetProperties(parameters, 2);
    // test with property only on 1 dataset, => retrieve 1 dataset
    parameters = "?k[0]=TEXTFIELD|satellite|fuse1";
    queryMultidatasetProperties(parameters, 1);
    // test with property on no dataset => retrieve 0 dataset
    parameters = "?k[0]=TEXTFIELD|test|fuse1";
    queryMultidatasetProperties(parameters, 0);
    // test with property on both dataset but with wrong query type => retrieve an error
    parameters = "?k[0]=NUMERIC_BETWEEN|dec|spot1|spot2";
    queryMultidatasetProperties(parameters, 0);

  }

  /**
   * Get a TaskModel at the given url Method ! Overriden to call the good getResponse method !
   * 
   * @param url
   *          the url
   * @param userId
   *          the User identifier
   * @param password
   *          the User password
   * @return the TaskModel
   */
  public TaskModel getTaskModelWithUrlParam(String url, String userId, String password, boolean asPublic) {
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());
      return (TaskModel) response.getItem();
    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  private ResourceModel fillMultidsSearchParameter(ResourceModel taskResource) {

    taskResource.getParameterByName("dictionary").setValue(dictionaryId);
    taskResource.getParameterByName("collection").setValue(collectionId);

    return taskResource;
  }

  private TaskModel queryMultidataset(String parameters, boolean asPublic) {
    Representation repr = new StringRepresentation("");
    String url = getHostUrl() + projectUrl + urlAttachResource + "?" + parameters;
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);
        cr.setChallengeResponse(chal);
      }
      result = cr.post(repr, getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());
      return (TaskModel) response.getItem();
    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }

  private void assertMultidatasetResults(TaskModel taskModel, int expectedHeadersResults, int expectedFuseResults) {
    assertNotNull(taskModel);
    List<Object> datasets = taskModel.getProperties();
    assertNotNull(datasets);
    assertEquals(2, datasets.size());
    for (Object object : datasets) {
      DataSetQueryStatusDTO desc = (DataSetQueryStatusDTO) object;
      assertEquals(DatasetQueryStatus.REQUEST_DONE, desc.getStatus());
      if (desc.getId().equals(fuseDsId)) {
        assertEquals(new Integer(expectedFuseResults), desc.getNbRecord());
      }
      if (desc.getId().equals(headersDsId)) {
        assertEquals(new Integer(expectedHeadersResults), desc.getNbRecord());
      }

    }
  }

  private void assertMutlidatasetResultsError(TaskModel taskModel) {
    assertNotNull(taskModel);
    List<Object> datasets = taskModel.getProperties();
    assertNotNull(datasets);
    assertEquals(2, datasets.size());
    for (Object object : datasets) {
      DataSetQueryStatusDTO desc = (DataSetQueryStatusDTO) object;
      assertEquals(DatasetQueryStatus.REQUEST_ERROR, desc.getStatus());
    }
  }

  /**
   * Query the multidataset properties search with parameters Assert that the number of dataset in result are
   * nbDsExpected
   * 
   * @param parameters
   *          the parameters of the query
   * @param nbDsExpected
   *          the number of dataset expected in result
   */
  private void queryMultidatasetProperties(String parameters, int nbDsExpected) {
    Representation result = null;
    String url = getHostUrl() + projectUrl + urlAttachResourceProperty + parameters;
    try {
      ClientResource cr = new ClientResource(url);
      result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Collection.class);
      assertTrue(response.getSuccess());

      assertNotNull(response.getItem());

      Collection collectionResult = (Collection) response.getItem();

      if (nbDsExpected == 0 && MediaType.APPLICATION_JSON.isCompatible(getMediaTest())) {
        assertNull(collectionResult.getDataSets());
      }
      else {
        assertNotNull(collectionResult.getDataSets());
        assertEquals(nbDsExpected, collectionResult.getDataSets().size());
      }

    }
    finally {
      RIAPUtils.exhaust(result);
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
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;

      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("resourcePlugin", ResourceModelDTO.class);
      xstream.alias("resourceParameter", ResourceParameter.class);
      xstream.omitField(ExtensionModel.class, "parametersMap");
      xstream.alias("TaskModel", TaskModel.class);
      xstream.alias("image", Resource.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (dataClass == ResourceModelDTO.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
      }

      if (dataClass == TaskModel.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(TaskModel.class, "properties", DataSetQueryStatusDTO.class);
      }

      // for Collection
      xstream.alias("collection", Collection.class);
      if (media.isCompatible(MediaType.APPLICATION_JSON) && dataClass == Collection.class) {
        xstream.addImplicitCollection(Collection.class, "dataSets", Resource.class);
        xstream.addImplicitCollection(Resource.class, "properties", Property.class);
      }

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
        }
        else {
          xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == ResourceModelDTO.class) {
          xstream.aliasField("resourcePlugin", Response.class, "item");
          xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
        }
        if (dataClass == TaskModel.class) {
          xstream.aliasField("TaskModel", Response.class, "item");
        }
        if (dataClass == Collection.class) {
          xstream.aliasField("collection", Response.class, "item");
        }

      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
        // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}
