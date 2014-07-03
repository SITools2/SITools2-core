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
package fr.cnes.sitools.utils;

import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.AbstractSitoolsTestCase;
import fr.cnes.sitools.common.SitoolsCommonDateConverter;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.model.Url;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedModelDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.dto.ColumnConceptMappingDTO;
import fr.cnes.sitools.dataset.dto.DataSetExpositionDTO;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.dataset.model.structure.StructureNodeComplete;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.dto.ResourcePluginDescriptionDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.project.model.MinimalProjectPriorityDTO;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectModule;
import fr.cnes.sitools.project.model.ProjectPriorityDTO;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskStatus;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

public class GetResponseUtils {

  /**
   * REST API Response Representation wrapper for simple Response
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, MediaType mediaTest) {
    return getResponse(media, representation, mediaTest, null);
  }

  /**
   * REST API Response Representation wrapper for simple Response
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param mediaTest
   *          The MediaType used in the test
   * @param fieldToOmit
   *          The field in the response that has to be omited during deserialization
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, MediaType mediaTest,
      String fieldToOmit) {
    try {
      if (!media.isCompatible(mediaTest) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.aliasField("data", Response.class, "data");
      if (fieldToOmit != null) {
        xstream.omitField(Response.class, fieldToOmit);
      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(mediaTest)) {
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
  public static Response getResponseConverter(MediaType media, Representation representation, Class<?> dataClass,
      MediaType mediaTest) {
    return getResponseConverter(media, representation, dataClass, mediaTest, false);
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
  public static Response getResponseConverter(MediaType media, Representation representation, Class<?> dataClass,
      MediaType mediaTest, boolean isArray) {
    try {
      if (!media.isCompatible(mediaTest) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      if (dataClass == ConverterChainedModelDTO.class) {
        xstream.alias("converterChainedModel", ConverterChainedModelDTO.class);
        xstream.alias("converterModel", ConverterModelDTO.class);
      }
      xstream.alias("converterParameter", ConverterParameter.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (isArray) {
        if (dataClass == ConverterChainedModelDTO.class) {
          xstream.addImplicitCollection(ConverterChainedModelDTO.class, "data", dataClass);
        }
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          if (dataClass == ConverterChainedModelDTO.class) {
            xstream.addImplicitCollection(ConverterChainedModelDTO.class, "converters", ConverterModelDTO.class);
          }
          xstream.addImplicitCollection(ConverterModelDTO.class, "parameters", ConverterParameter.class);
        }

        if (dataClass == ConverterChainedModelDTO.class) {
          xstream.aliasField("converterChainedModel", Response.class, "item");
        }
        if (dataClass == ConverterModelDTO.class) {
          xstream.aliasField("converter", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(mediaTest)) {
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

  // ------------------------------------------------------------
  // DICTIONARY MAPPING

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
  public static Response getResponseDicoMapping(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseDicoMapping(media, representation, dataClass, false);
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
  public static Response getResponseDicoMapping(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      xstream.alias("dictionaryMapping", DictionaryMapping.class);
      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(DictionaryMapping.class, "mapping", "mapping", ColumnConceptMapping.class);
      }

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == DictionaryMapping.class) {
          xstream.aliasField("dictionaryMapping", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON) || media.isCompatible(MediaType.APPLICATION_XML)) {
        Response response = rep.getObject("response");
        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // DATASETS

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
  public static Response getResponseDataset(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseDataset(media, representation, dataClass, false);
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
  public static Response getResponseDataset(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.registerConverter(new SitoolsCommonDateConverter());

      if (dataClass == DataSet.class) {
        xstream.alias("dataset", DataSet.class);
        xstream.alias("datasource", JDBCDataSource.class);
        xstream.alias("column", Column.class);
        xstream.alias("structure", Structure.class);

        if (media.isCompatible(MediaType.APPLICATION_JSON)) {

          xstream.addImplicitCollection(DataSet.class, "columnModel", Column.class);
          xstream.addImplicitCollection(DataSet.class, "structures", Structure.class);
          xstream.addImplicitCollection(DataSet.class, "predicat", Predicat.class);

          xstream.addImplicitCollection(DataSet.class, "dictionaryMappings", DictionaryMapping.class);
          xstream.addImplicitCollection(DictionaryMapping.class, "mapping", ColumnConceptMapping.class);
          xstream.addImplicitCollection(SitoolsStructure.class, "nodeList", StructureNodeComplete.class);
          xstream.addImplicitCollection(StructureNodeComplete.class, "children", StructureNodeComplete.class);

          xstream.addImplicitCollection(DataSet.class, "properties", SitoolsProperty.class);
          xstream.addImplicitCollection(DataSet.class, "datasetViewConfig", Property.class);
        }
      }

      if (dataClass == DataSetExpositionDTO.class) {
        xstream.alias("dataset", DataSetExpositionDTO.class);
        xstream.alias("column", Column.class);
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(DataSetExpositionDTO.class, "columnModel", "columnModel", Column.class);

          xstream.addImplicitCollection(DataSetExpositionDTO.class, "dictionaryMappings", "dictionaryMappings",
              DictionaryMappingDTO.class);
          xstream
              .addImplicitCollection(DictionaryMappingDTO.class, "mapping", "mapping", ColumnConceptMappingDTO.class);

          xstream.addImplicitCollection(ConceptTemplate.class, "properties", "properties", Property.class);
        }
      }

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == DataSet.class) {
          xstream.aliasField("dataset", Response.class, "item");
        }
        if (dataClass == DataSetExpositionDTO.class) {
          xstream.aliasField("dataset", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON) || media.isCompatible(MediaType.APPLICATION_XML)) {
        Response response = rep.getObject("response");
        // Response response = rep.getObject();

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // GUI SERVICE PLUGIN MODEL

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
  public static Response getResponseGuiServicePlugin(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseGuiServicePlugin(media, representation, dataClass, false);
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
  public static Response getResponseGuiServicePlugin(MediaType media, Representation representation,
      Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("guiServicePlugin", GuiServicePluginModel.class);

      if (media.equals(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(Dependencies.class, "js", Url.class);
        xstream.addImplicitCollection(Dependencies.class, "css", Url.class);
      }

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        xstream.aliasField("guiServicePlugin", Response.class, "item");
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON) || media.isCompatible(MediaType.APPLICATION_XML)) {
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

  // ------------------------------------------------------------
  // RESOURCE MODEL

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
  public static Response getResponseResource(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseResource(media, representation, dataClass, false);
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
  public static Response getResponseResource(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
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
      xstream.alias("ResourcePluginDescriptionDTO", ResourcePluginDescriptionDTO.class);
      xstream.omitField(ExtensionModel.class, "parametersMap");

      xstream.alias("TaskModel", TaskModel.class);
      xstream.alias("image", Resource.class);
      xstream.alias("status", TaskStatus.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (dataClass == ResourceModelDTO.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
      }

      if (dataClass == TaskModel.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(TaskModel.class, "properties", Object.class);

      }

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          if (dataClass == ResourceModelDTO.class) {
            xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
          }
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
      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON) || media.isCompatible(MediaType.APPLICATION_XML)) {
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

  // ------------------------------------------------------------
  // RESOURCE MODEL

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
  public static Response getResponseResponseModel(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseResponseModel(media, representation, dataClass, false);
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
  public static Response getResponseResponseModel(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
        else {
          // xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON) || media.isCompatible(MediaType.APPLICATION_XML)) {
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

  // ------------------------------------------------------------
  // USER OR GROUP MODEL

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @return Response
   */
  public static Response getResponseUserOrGroup(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseUserOrGroup(media, representation, dataClass, false);
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
  public static Response getResponseUserOrGroup(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("user", User.class);
      xstream.alias("group", Group.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == User.class) {
          xstream.aliasField("user", Response.class, "item");
        }
        if (dataClass == Group.class) {
          xstream.aliasField("group", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      Response response = rep.getObject("response");
      // TODO MEMO usage of SitoolsXStreamRepresentation.getObject("response") instead of standard signature Response
      // response = rep.getObject();

      return response;
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // ROLE MODEL

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
  public static Response getResponseRole(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseRole(media, representation, dataClass, false);
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
  public static Response getResponseRole(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("role", Role.class);
      // xstream.alias("dataset", Resource.class);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Role.class, "users", "users", Resource.class);
          xstream.addImplicitCollection(Role.class, "groups", "groups", Resource.class);
        }

        xstream.aliasField("role", Response.class, "item");
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      Response response = rep.getObject("response");

      return response;
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // USER BLACKLIST MODEL

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
  public static Response getResponseUserBlacklist(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseUserBlacklist(media, representation, dataClass, false);
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
  public static Response getResponseUserBlacklist(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.registerConverter(new SitoolsCommonDateConverter());
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("userBlackListModel", UserBlackListModel.class);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);

      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      Response response = rep.getObject("response");

      return response;
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // PROJECT MODEL

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
  public static Response getResponseProject(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseProject(media, representation, dataClass, false);
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
  public static Response getResponseProject(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("project", Project.class);
      xstream.alias("dataset", Resource.class);
      // xstream.alias("dataset", Resource.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
          xstream.addImplicitCollection(Project.class, "modules", ProjectModule.class);
          xstream.aliasField("dataSets", Project.class, "dataSets");
        }

        if (dataClass == Project.class) {
          xstream.aliasField("project", Response.class, "item");
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      Response response = rep.getObject("response");

      return response;

    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // PROJECT PRIORITY DTO MODEL

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
  public static Response getResponseProjectPriorityDTO(MediaType media, Representation representation,
      Class<?> dataClass) {
    return getResponseProjectPriorityDTO(media, representation, dataClass, false);
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
  public static Response getResponseProjectPriorityDTO(MediaType media, Representation representation,
      Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("minimalProjectPriorityList", MinimalProjectPriorityDTO.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(MinimalProjectPriorityDTO.class, "minimalProjectPriorityList",
              ProjectPriorityDTO.class);
        }

      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      Response response = rep.getObject("response");

      return response;

    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // ResourceAuthorization Model

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
  public static Response getResponseResourceAuthorization(MediaType media, Representation representation,
      Class<?> dataClass) {
    return getResponseResourceAuthorization(media, representation, dataClass, false);
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
  public static Response getResponseResourceAuthorization(MediaType media, Representation representation,
      Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("authorization", ResourceAuthorization.class);
      xstream.alias("resourceAuthorization", ResourceAuthorization.class);
      xstream.alias("authorize", RoleAndMethodsAuthorization.class);

      // Parce que les annotations ne sont apparemment prises en compte
      xstream.omitField(Response.class, "itemName");
      xstream.omitField(Response.class, "itemClass");

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(ResourceAuthorization.class, "authorizations",
              RoleAndMethodsAuthorization.class);
        }

        if (dataClass == ResourceAuthorization.class) {
          xstream.aliasField("authorization", Response.class, "item");
        }
      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      Response response = rep.getObject("response");

      return response;
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}
