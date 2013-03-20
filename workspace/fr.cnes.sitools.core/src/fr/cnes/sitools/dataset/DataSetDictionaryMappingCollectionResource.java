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
package fr.cnes.sitools.dataset;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;

/**
 * Resource for managing Dictionary mappings on a DataSet
 * 
 * 
 * @author m.gond (Akka Technologies)
 */
public class DataSetDictionaryMappingCollectionResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSetDictionaryMappingCollectionResource");
    setDescription("Resource for managing complete Dictionary mappings on a DataSet");
  }

  /**
   * Initiate the resource
   */
  @Override
  public void doInit() {
    super.doInit();
    this.setNegotiated(false);
  }

  /**
   * Get the complete Mapping
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveMappings(Variant variant) {
    try {
      Response response = null;
      if (datasetId != null) {
        DataSet dataset = store.retrieve(datasetId);
        if (dataset == null) {
          response = new Response(false, "DATASET_NOT_FOUND");
        }
        else {
          response = new Response(true, dataset.getDictionaryMappings(), DictionaryMapping.class, "dictionaryMapping");
          if (dataset.getDictionaryMappings() == null) {
            response.setTotal(0);
          }
          else {
            response.setTotal(dataset.getDictionaryMappings().size());
          }
        }
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the complete dictionaries mapping");
    info.setIdentifier("retrieve_complete_mapping");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
