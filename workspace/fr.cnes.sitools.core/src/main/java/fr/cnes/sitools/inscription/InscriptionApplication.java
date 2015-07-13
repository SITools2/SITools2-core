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
package fr.cnes.sitools.inscription;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;

/**
 * Application for managing inscription
 * 
 * Dependencies : UsersAndGroups for checking if a user exists and for create a new one ?
 * 
 * TODO Constructor with all generic security configuration (Authenticator informations) Configure security application by
 * spring or from server main
 * 
 * @author AKKA
 * 
 */
public final class InscriptionApplication extends AbstractInscriptionApplication {

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host context
   */
  public InscriptionApplication(Context context) {
    super(context);
    setCategory(Category.ADMIN);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("InscriptionApplication");
    setDescription("Management of user registrations.");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // No specific admin authentication here
    router.attachDefault(AdminInscriptionCollectionResource.class);
    router.attach("/{inscriptionId}", AdminInscriptionResource.class);

    return router;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Inscription management application for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
