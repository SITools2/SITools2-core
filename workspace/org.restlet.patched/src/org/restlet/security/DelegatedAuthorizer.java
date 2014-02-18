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
package org.restlet.security;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;

/**
 * FIXME RESTLET
 * 
 * Classe nécessairement dans le package org.restlet.security pour rendre publique la methode authorize et pour rendre
 * serializable un Authorizer
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DelegatedAuthorizer extends Authorizer implements Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = -9072105456458512655L;

  /** Delegated Restlet authorizer */
  private Authorizer authorizer = null;

  /** logger */
  private Logger logger = null;

  /**
   * Constructor Authorizer encapsulation
   * 
   * @param authorizer
   *          Restlet authorizer
   */
  public DelegatedAuthorizer(Authorizer authorizer) {
    this.authorizer = authorizer;
  }

  /**
   * Constructor
   */
  public DelegatedAuthorizer() {
    super();
  }

  /**
   * Constructor with Authorizer and Logger to log unsuccessful authorization
   * 
   * @param authorizer
   *          the authorizer
   * @param logger
   *          the logger
   */
  public DelegatedAuthorizer(Authorizer authorizer, Logger logger) {
    this(authorizer);
    setLogger(logger);
  }

  @Override
  public boolean authorize(Request request, Response response) {
    return authorizer.authorize(request, response);
  }

  /**
   * Gets the delegated authorizer
   * 
   * @return the authorizer
   */
  public Authorizer getAuthorizer() {
    return authorizer;
  }

  /**
   * Sets the delegated authorizer
   * 
   * @param authorizer
   *          the authorizer to set
   */
  public void setAuthorizer(Authorizer authorizer) {
    this.authorizer = authorizer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.Authorizer#unauthorized(org.restlet.Request, org.restlet.Response)
   */
  @Override
  protected int unauthorized(Request request, Response response) {
    if (this.logger != null) {
      this.logger.log(Level.INFO, "SECURTIY ACCESS ERROR : Request to : " + request.getResourceRef().getPath()
          + " forbidden, authorization failed");
    }
    return super.unauthorized(request, response);
  }

  protected void setLogger(Logger logger) {
    this.logger = logger;
  }

}
