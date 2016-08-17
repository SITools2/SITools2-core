    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.ArrayList;
import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authorizer;
import org.restlet.security.DelegatedAuthorizer;

/**
 * Authorizer to do AND combination of Authorizers
 * if all of the authorizers list is true the SitoolsOrAuthorizer return true.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class SitoolsAndAuthorizer extends DelegatedAuthorizer {

  /** serialVersionUID */
  private static final long serialVersionUID = 8506186219780621582L;
  
  /** inner list of SitoolsAuthorizer */
  private List<DelegatedAuthorizer> authorizers = new ArrayList<DelegatedAuthorizer>();

  /** 
   * Constructor with unlimited Authorizers
   * @param  authorizers Authorizer...
   */
  public SitoolsAndAuthorizer(Authorizer... authorizers) {
    for (int i = 0; i < authorizers.length; i++) {
      this.authorizers.add(new DelegatedAuthorizer(authorizers[i]));
    }
  }

  /** 
   * Constructor with ArrayList 
   * @param authorizers ArrayList<SitoolsAuthorizer>
   */
  public SitoolsAndAuthorizer(ArrayList<DelegatedAuthorizer> authorizers) {
    this.authorizers = authorizers;
  }

  /** 
   * Constructor with unlimited SitoolsAuthorizers
   * @param authorizers SitoolsAuthorizer...
   */
  public SitoolsAndAuthorizer(DelegatedAuthorizer... authorizers) {
    for (Authorizer authorizer : authorizers) {
      this.authorizers.add(new DelegatedAuthorizer(authorizer));
    }
  }

  /** 
   * Constructor with List 
   * @param authorizers List<Authorizer>
   */
  public SitoolsAndAuthorizer(List<Authorizer> authorizers) {
    for (Authorizer authorizer : authorizers) {
      this.authorizers.add(new DelegatedAuthorizer(authorizer));
    }
  }



  @Override
  public boolean authorize(Request request, Response response) {
    return authorize(request, response, 0);
  }

  /**
   * Private method to process authorizers list recursively 
   * @param request Request
   * @param response Response
   * @param i index of authorizer in list
   * @return boolean true if authorized.
   */
  private boolean authorize(Request request, Response response, int i) {
    if (i == (authorizers.size() - 1)) {
      return authorizers.get(i).authorize(request, response);
    }
    else {
      return authorizers.get(i).authorize(request, response) && authorize(request, response, i + 1);
    }
  }
}
