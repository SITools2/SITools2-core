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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authorizer;

import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.plugins.filters.model.FilterParameter;

/**
 * Specific filter to control access on resources like DataStorage directories.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DataStorageAuthorizer extends Authorizer {
  /** The Context */
  private Context context;
  /** The filterId */
  private String filterId = null;
  /** The filterModel */
  private FilterModel filterModel = null;
  /** The authorize boolean */
  private boolean bauthorize = true;

  /**
   * Default Constructor
   * 
   * @param context
   *          the Context
   */
  public DataStorageAuthorizer(Context context) {
    this.context = context;

    filterId = (String) this.context.getAttributes().get("FILTER_ID");
    filterModel = (FilterModel) this.context.getAttributes().get("FILTER_MODEL");

    FilterParameter authorizeParameter = filterModel.getParameterByName("authorize");
    if (authorizeParameter != null) {
      try {
        bauthorize = Boolean.parseBoolean(authorizeParameter.getValue());
      }
      catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Override
  public boolean authorize(Request arg0, Response arg1) {
    String path = arg0.getResourceRef().getRelativePart();
    String user = (arg0.getClientInfo() != null && arg0.getClientInfo().getUser() != null) ? arg0.getClientInfo()
        .getUser().getIdentifier() : null;
    return auhtorize(path, user);
  }

  /**
   * Simple method to implement for specializing security access control on resource path
   * 
   * @param path
   *          relative path to the resource
   * @param user
   *          user identifier(login) null if not authenticated
   * @return boolean true if authorized
   */
  public boolean auhtorize(String path, String user) {
    return bauthorize;
  }

  /**
   * Gets the context value
   * 
   * @return the context
   */
  public Context getContext() {
    return context;
  }

  /**
   * Sets the value of context
   * 
   * @param context
   *          the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * Gets the filterId value
   * 
   * @return the filterId
   */
  public String getFilterId() {
    return filterId;
  }

  /**
   * Sets the value of filterId
   * 
   * @param filterId
   *          the filterId to set
   */
  public void setFilterId(String filterId) {
    this.filterId = filterId;
  }

  /**
   * Gets the filterModel value
   * 
   * @return the filterModel
   */
  public FilterModel getFilterModel() {
    return filterModel;
  }

  /**
   * Sets the value of filterModel
   * 
   * @param filterModel
   *          the filterModel to set
   */
  public void setFilterModel(FilterModel filterModel) {
    this.filterModel = filterModel;
  }

  /**
   * Gets the bauthorize value
   * 
   * @return the bauthorize
   */
  public boolean isBauthorize() {
    return bauthorize;
  }

  /**
   * Sets the value of bauthorize
   * 
   * @param bauthorize
   *          the bauthorize to set
   */
  public void setBauthorize(boolean bauthorize) {
    this.bauthorize = bauthorize;
  }

}
