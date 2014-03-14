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
package fr.cnes.sitools.filter.authorizer;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.plugins.filters.model.FilterParameter;
import fr.cnes.sitools.plugins.filters.model.FilterParameterType;

/**
 * Authorizer model
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DataStorageAuthorizerModel extends FilterModel {

  /**
   * Serializable
   */
  private static final long serialVersionUID = 7318492062720866483L;

  /**
   * Model for DataStorage (or application) Authorizer
   */
  public DataStorageAuthorizerModel() {
    super();
    setName("DataStorageAuthorizer");
    setDescription("Customizable datastorage directory authorizer");
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setClassName("fr.cnes.sitools.filter.authorizer.DataStorageAuthorizerModel");

    setFilterClassName("fr.cnes.sitools.filter.authorizer.DataStorageAuthorizer");

    /**
     * Parameter for the log directory
     */
    FilterParameter logDir = new FilterParameter("logdir", "Storage logging directory", FilterParameterType.PARAMETER_INTERN);
    addParam(logDir);

    /**
     * Parameter for authorize or block
     */
    FilterParameter authorize = new FilterParameter("authorize", "Authorize true|false", FilterParameterType.PARAMETER_INTERN);
    authorize.setValue("true"); // default true
    authorize.setValueType("xs:boolean");
    addParam(authorize);
  }
  
  @Override
  public Validator<FilterModel> getValidator() {
    // TODO validator for DataStorageAuthorizerModel
    return null;
  }
 
}
