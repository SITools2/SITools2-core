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
package fr.cnes.sitools.utils;

import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.AbstractSitoolsTestCase;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedModelDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
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
    try {
      if (!media.isCompatible(mediaTest) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(mediaTest)) {
        Response response = rep.getObject("response");
        return response;
      }
      else {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}
