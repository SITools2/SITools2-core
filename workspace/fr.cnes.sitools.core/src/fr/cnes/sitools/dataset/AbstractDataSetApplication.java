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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.wadl.ApplicationInfo;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.model.DataSet;

/**
 * Abstract application - Factors a DataSetStore and a datasetId
 * 
 * @author AKKA
 */
public abstract class AbstractDataSetApplication extends SitoolsParameterizedApplication {

  /**
   * DataSet identifier
   */
  protected String datasetId = null;

  /**
   * Store
   */
  protected SitoolsStore<DataSet> store = null;

  /**
   * Constructor
   * 
   * @param context
   *          RESTlet Application Context
   */
  @SuppressWarnings("unchecked")
  public AbstractDataSetApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<DataSet>) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public final String getDatasetId() {
    return datasetId;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<DataSet> getStore() {
    return store;
  }

  /**
   * Create and attach a new DataSetApplication
   * 
   * @param ds
   *          DataSet object
   */
  public abstract void attachDataSet(DataSet ds);

  /**
   * Detach the DataSetApplication corresponding with the DataSet given object
   * 
   * @param ds
   *          DataSet object
   */
  public abstract void detachDataSet(DataSet ds);

  /**
   * Detach the DataSetApplication corresponding with the DataSet given object
   * 
   * @param ds
   *          DataSet object
   */
  public abstract void detachDataSetDefinitif(DataSet ds);

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    return super.getApplicationInfo(request, response);
  }

  /**
   * Get Sitools settings for sub-classed dataset applications
   * 
   * @return Sitools settings
   * 
   *         public final SitoolsSettings getSettings() { return settings; }
   */

}
