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
package fr.cnes.sitools.security.authorization.business;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authorizer;
import org.restlet.security.DelegatedAuthorizer;

/**
 * Combination of RoleAuthorizers and MethodAuthorizers built on the Sitools security configuration defined for a
 * specific application.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class SitoolsApplicationAuthorizer extends Authorizer {

  /** inner authorizer list */
  private DelegatedAuthorizer[] liste = null;

  /**
   * Constructor with unlimited list of SitoolsAuthorizer
   * 
   * @param liste
   *          SitoolsAuthorizer
   */
  public SitoolsApplicationAuthorizer(DelegatedAuthorizer... liste) {
    super();
    this.liste = liste;
  }

  /**
   * Constructor with list of SitoolsAuthorizer
   * 
   * @param liste
   *          SitoolsAuthorizer
   */
  public SitoolsApplicationAuthorizer(List<DelegatedAuthorizer> liste) {
    super();
    DelegatedAuthorizer[] inner = new DelegatedAuthorizer[liste.size()];
    this.liste = liste.toArray(inner);
  }

  @Override
  public boolean authorize(Request request, Response response) {
    if (liste != null) {
      for (DelegatedAuthorizer element : liste) {
        boolean result = element.authorize(request, response);
        if (result) {
          return true;
        }
      }
    }
    return false;
  }

}
