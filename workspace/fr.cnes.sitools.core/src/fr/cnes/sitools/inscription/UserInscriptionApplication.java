/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.inscription;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.security.captcha.CaptchaFilter;
import fr.cnes.sitools.security.captcha.CaptchaResource;

/**
 * Application for managing inscription Dependencies : UsersAndGroups for checking if a user exists and for create a new
 * one ?
 * 
 * TODO Constructor with all generic security configuration (Authenticator infos) Configure security application by
 * spring or from server main
 * 
 * @author AKKA
 * 
 */
public final class UserInscriptionApplication extends AbstractInscriptionApplication {

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  public UserInscriptionApplication(Context context) {
    super(context);
    setCategory(Category.USER);
  }

  @Override
  public void sitoolsDescribe() {
    setName("UserInscriptionApplication");
    setDescription("User inscription management on client side\n" +
    		"-> Administrator must have all authorizations" +
    		"-> Public user must have at least POST right in order to register");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());
    
    CaptchaFilter filter = new CaptchaFilter(getContext());
    filter.setNext(UserInscriptionResource.class);
    
    router.attachDefault(filter);
    
    router.attach("/captcha", CaptchaResource.class);

    return router;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Inscription application on client side for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
