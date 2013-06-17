     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.database.common;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.AbstractDataSetResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;

/**
 * Resource that performs a count on a dataset with some parameters Return only the number of records for this search
 * 
 * 
 * @author m.gond
 */
public class DataSetCountResource extends AbstractDataSetResource {

  /** The database request params util */
  private DataSetExplorerUtil datasetExplorerUtil = null;

  /** The parent application */
  private DataSetApplication application = null;

  /** databseRequest parameters */
  private DatabaseRequestParameters databaseParams;

  @Override
  public void sitoolsDescribe() {
    setNegotiated(false);
    setName("DataSetCountResource");
    setDescription("Return only the number of records for a particular search");

  }

  @Override
  public void doInit() {

    super.doInit();

    // parent application
    application = (DataSetApplication) getApplication();

    datasetExplorerUtil = new DataSetExplorerUtil(application, this.getRequest(), getContext());

    databaseParams = datasetExplorerUtil.getDatabaseParams();
  }

  /**
   * Get the count of a search
   * 
   * @param variant
   *          the Variant needed
   * @return the number of records for a particular search
   */
  @Get
  public Representation getCount(Variant variant) {
    return calcCount(variant);
  }

  /**
   * Get the count of a search
   * 
   * @param representation
   *          The Post representation
   * @param variant
   *          the Variant needed
   * @return the number of records for a particular search
   */
  @Post
  public Representation getCountFromFile(Representation representation, Variant variant) {
    return calcCount(variant);
  }

  /**
   * Calculate the count on a dataset and return a {@link Representation} with the specified {@link Variant}
   * 
   * @param variant
   *          the {@link Variant} of the {@link Representation} needed
   * @return the {@link Representation} containing the count of the dataset
   */
  private Representation calcCount(Variant variant) {
    // first check if the datasource is activated or not
    SitoolsDataSource datasource = databaseParams.getDb();
    if (datasource == null || !"ACTIVE".equals(datasource.getDsModel().getStatus())) {
      getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "Datasource not activated");
      return null;
    }
    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(databaseParams);
    try {
      int totalCount = databaseRequest.calculateTotalCountFromBase();
      Response response = new Response();
      response.setSuccess(true);
      response.setTotal(new Integer(totalCount));
      return getRepresentation(response, variant);
    }
    catch (SitoolsException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL information
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("retrieve_count_records");
    info.setDocumentation("Method to retrieve the count of records of a dataset");
    addStandardGetRequestInfo(info);
    DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
    DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
