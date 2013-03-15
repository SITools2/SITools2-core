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
package fr.cnes.sitools.dataset.converter;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;

/**
 * Activate or disactivate a Converter
 * 
 * 
 * @author m.gond
 */
public final class ConverterActivationResource extends AbstractConverterResource {

  /** converterChained identifier parameter */
  private String converterId = null;

  /** store */
  private SitoolsStore<ConverterChainedModel> store = null;

  /**
   * Initiate the resource
   */
  public void doInit() {
    super.doInit();
    converterId = (String) this.getRequest().getAttributes().get("converterId");

    store = ((ConverterApplication) getApplication()).getStore();
  }

  @Override
  public void sitoolsDescribe() {
    setName("ConverterActivationResource");
    setDescription("Resource to modify the status of a Converter.");
  }

  /**
   * Actions on PUT
   * 
   * @param representation
   *          could be null.
   * @param variant
   *          MediaType of response
   * @return Representation response
   */
  @Put
  public Representation validate(Representation representation, Variant variant) {
    Response response = null;
    Representation rep = null;
    try {
      do {

        if (getDatasetId() == null) {
          response = new Response(false, "FILTER_CHAINED_NOT_FOUND");
          break;
        }
        ConverterChainedModel converterChainedModel = getStore().retrieve(getDatasetId());

        if (converterChainedModel == null) {
          response = new Response(false, "FILTER_CHAINED_IS_NULL");
          break;
        }

        ConverterModel converter = getConverterModel(converterChainedModel, converterId);
        if (converter == null) {
          response = new Response(false, "FILTER_NOT_FOUND");
          break;
        }

        if (this.getReference().toString().endsWith("start")) {
          if ("ACTIVE".equals(converter.getStatus())) {
            response = new Response(true, "converter.update.blocked");
            break;
          }
          else {
            converter.setStatus("ACTIVE");
            getStore().update(converterChainedModel);
            // fillParameters(converter);
            ConverterModel converterOut = getConverterModel(converterChainedModel, converterId);

            ConverterModelDTO converterOutDTO = getConverterModelDTO(converterOut);
            response = new Response(true, converterOutDTO, ConverterModelDTO.class, "converter");
            response.setMessage("converter.start.success");
          }
        }

        if (this.getReference().toString().endsWith("stop")) {
          if ("INACTIVE".equals(converter.getStatus())) {
            response = new Response(true, "converter.stop.blocked");
            break;
          }
          else {
            converter.setStatus("INACTIVE");
            getStore().update(converterChainedModel);
            ConverterModel converterOut = getConverterModel(converterChainedModel, converterId);
            ConverterModelDTO converterOutDTO = getConverterModelDTO(converterOut);
            response = new Response(true, converterOutDTO, ConverterModelDTO.class, "converter");
            response.setMessage("converter.stop.success");
          }

        }
      } while (false);

      // Response
      if (response == null) {
        response = new Response(false, "converter.action.error");
      }
    }
    finally {
      rep = getRepresentation(response, variant);
    }
    return rep;
  }

  /**
   * Describe the PUT method
   * 
   * @param info
   *          WADL method information
   * @param path
   *          url attachment in application
   */
  public void describePut(MethodInfo info, String path) {
    if (path.endsWith("start")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Activate the Converter from a ConverterChained attached to a Dataset making it available for the Records API.");
    }
    else if (path.endsWith("stop")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Disactivate the Converter from a ConverterChained attached to a Dataset making it unavailable for DataSetApplication API users.");
    }
    else {
      info.setDocumentation("Method to activate/stop a Converter.");
    }
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<ConverterChainedModel> getStore() {
    return store;
  }

}
