/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * <p/>
 * This file is part of SITools2.
 * <p/>
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.security.filter;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.server.Starter;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Filter;
import org.restlet.routing.Redirector;

import java.util.Locale;

/**
 * Specific filter to control user access on datastorage resources (directories, files...)
 *
 * @author b.fiorito (AKKA Technologies)
 *
 */
public class DataStorageAuthenticatorFilter extends Filter {

  /** The Context */
  private Context context;

  private SitoolsSettings settings;

  /**
   * Default Constructor
   *
   * @param context
   *          the Context
   */
  public DataStorageAuthenticatorFilter(Context context) {
    super(context);
    this.context = context;
    this.settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
  }

  @Override
  protected void afterHandle(Request request, Response response) {

    // 401 User not authenticated
    if (!request.getClientInfo().isAuthenticated() && response.getStatus().equals(Status.CLIENT_ERROR_FORBIDDEN)) {
      // redirect to login page
      String target = settings.getString(Consts.APP_URL) + "/loginPageRedirect/index.html?redirect=" + request.getResourceRef().getIdentifier();
      Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_TEMPORARY);
      redirector.handle(request, response);
    }
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

}
