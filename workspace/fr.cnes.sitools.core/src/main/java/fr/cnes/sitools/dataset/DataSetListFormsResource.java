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
package fr.cnes.sitools.dataset;

import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Return the list of forms for a dataset
 * 
 * 
 * @author m.gond
 */
public class DataSetListFormsResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSetListFormsResource");
    setDescription("Resource that return the list of forms for a given DataSet");
  }

  /**
   * Get the list of forms for the project
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of Forms
   */
  @Get
  public Representation getFormsList(Variant variant) {

    Representation rep = null;

    DataSetApplication datasetApp = (DataSetApplication) getApplication();
    DataSet dataset = datasetApp.getDataSet();

    List<FormDTO> formListOutput = getFormList(dataset.getId());

    Response response = new Response(true, formListOutput, FormDTO.class);
    rep = getRepresentation(response, variant);

    return rep;
  }

  /**
   * Get the list of forms for a dataset
   * 
   * @param id
   *          the id of the dataset
   * @return a Response containing the list of Forms
   */
  private List<FormDTO> getFormList(String id) {
    return RIAPUtils.getListOfObjects(application.getSettings().getString(Consts.APP_DATASETS_URL) + "/" + id
        + "/forms", getContext());
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of forms associated to the dataset.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
