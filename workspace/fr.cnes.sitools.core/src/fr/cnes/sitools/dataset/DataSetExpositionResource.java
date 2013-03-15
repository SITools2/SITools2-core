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

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.dto.DataSetExpositionDTO;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.Structure;

/**
 * DataSet resource with only Get operation
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DataSetExpositionResource extends AbstractDataSetResource {

  /** Dictionary id */
  private String dictionaryId;

  @Override
  public void sitoolsDescribe() {
    setName("DataSetResource");
    setDescription("Resource exposing the dataset description");
    setNegotiated(false);
  }

  @Override
  protected void doInit() {
    super.doInit();
    dictionaryId = (String) this.getRequest().getAttributes().get("dictionaryId");
  }

  /**
   * Get on DataSet
   * 
   * @param variant
   *          variant required
   * @return representation corresponding to the required variant
   */
  @Get
  public Representation retrieveDataSet(Variant variant) {
    return getDataSet(variant);
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL information
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("retrieve_dataset");
    info.setDocumentation("Method to retrieve the dataset with semantic definition.");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get DataSet
   * 
   * @param variant
   *          client preferred output representation
   * @return DataSet representation
   */
  private Representation getDataSet(Variant variant) {

    try {
      DataSetApplication datasetApp = (DataSetApplication) getApplication();
      DataSet dataset = datasetApp.getDataSet();
      Response response = null;
      if (dataset != null) {
        DataSetExpositionDTO dsExp;
        if (this.getReference().toString().contains("/mappings")) {
          // all the mappings
          if (dictionaryId == null || "".equals(datasetId)) {
            dsExp = getDsDTO(dataset, null, true);
          }
          else {
            // only a specified dictionary
            dsExp = getDsDTO(dataset, dictionaryId, false);
          }
        }
        else {
          // the default dictionary
          dsExp = getDsDTO(dataset, null, false);
        }
        response = new Response(true, dsExp, DataSetExpositionDTO.class, "dataset");
      }
      else {
        response = new Response(false, "NO_DATASET_FOUND");
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

  /**
   * Create a DataSetExpositionDTO from a DataSet model object
   * 
   * @param ds
   *          the DataSet model object
   * @param dictionaryId
   *          the dictionary to get the mapping from
   * @param allMapping
   *          true to add all the mappings
   * @return the DataSetExpositionDTO representing the given DataSet
   */
  private DataSetExpositionDTO getDsDTO(DataSet ds, String dictionaryId, boolean allMapping) {
    DataSetExpositionDTO dsExp = new DataSetExpositionDTO();

    dsExp.setId(ds.getId());
    dsExp.setName(ds.getName());
    dsExp.setColumnModel(ds.getColumnModel());
    dsExp.setSitoolsAttachementForUsers(ds.getSitoolsAttachementForUsers());
    dsExp.setStatus(ds.getStatus());
    dsExp.setDescriptionHTML(ds.getDescriptionHTML());
    dsExp.setDatasetView(ds.getDatasetView());
    dsExp.setDescription(ds.getDescription());
    dsExp.setDatasetViewConfig(ds.getDatasetViewConfig());
    dsExp.setImage(ds.getImage());

    // set the dictionary mapping
    DataSetApplication app = (DataSetApplication) getApplication();
    List<DictionaryMappingDTO> mappings = app.getDictionaryMappings();
    if (mappings != null) {
      for (DictionaryMappingDTO dictionaryMappingDTO : mappings) {
        // default case
        if (allMapping || (dictionaryId == null && dictionaryMappingDTO.isDefaultDico())
            || (dictionaryId != null && dictionaryId.equals(dictionaryMappingDTO.getDictionaryId()))) {
          dsExp.getDictionaryMappings().add(dictionaryMappingDTO);
          if (!allMapping) {
            break;
          }
        }
      }
    }

    return dsExp;

  }

  /**
   * Encode a response into a Representation according to the given media type.
   * 
   * @param response
   *          Response
   * @param media
   *          Response
   * @return Representation
   */
  public Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("dataset", DataSet.class);
    xstream.alias("column", Column.class);
    xstream.alias("structure", Structure.class);
    xstream.setMode(XStream.NO_REFERENCES);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
