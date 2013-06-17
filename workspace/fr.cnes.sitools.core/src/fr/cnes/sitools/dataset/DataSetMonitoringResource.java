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
package fr.cnes.sitools.dataset;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * Informations about exposition application of DataSet and its data.
 * Actions on the application.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 *         see RESTlet StatusService to re-route on availability page of the DataSet. 
 * 
 */
public final class DataSetMonitoringResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSetMonitoringResource");
    setDescription("Resource for returning status/logs of a DataSet Executing Application");
  }

  /**
   * Informations about exposition application of DataSet and its data.
   * @return Representation of the application status
   */
  @Get
  public Representation getDataSetApplicationStatus() {
    return new StringRepresentation(application.getName() + " status " + application.isStarted());
  }
  
  /**
   * Describe the GET method
   * @param info the WADL information
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("retrieve_dataset_status");
    info.setDocumentation("Method to retrieve the dataset staus");
    addStandardGetRequestInfo(info);
    addStandardSimpleResponseInfo(info);
  }

}
