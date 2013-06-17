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
package fr.cnes.sitools.inscription;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.security.SecurityUtil;

/**
 * Resource for users to subscribe
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserInscriptionResource extends InscriptionResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserInscriptionResource");
    setDescription("Resource for user inscription (register)");
  }

  /**
   * Handle POST method for registering a new User Inscription.
   * 
   * @param representation
   *          Representation
   * @param variant
   *          client preferred media type
   * @return Inscription
   */
  @Override
  @Post
  public Representation post(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "INSCRIPTION_REPRESENTATION_REQUIRED");
    }
    try {
      Inscription inscriptionInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the inscription bean
        inscriptionInput = new XstreamRepresentation<Inscription>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        inscriptionInput = new JacksonRepresentation<Inscription>(representation, Inscription.class).getObject();
      }

      // Response
      Response response = null;

      if (!Inscription.isValid(inscriptionInput)) {
        response = new Response(false, "INSCRIPTION.INVALID");
      }
      else {

        // CRYPTAGE DU MOT DE PASSE SI NECESSAIRE
        SecurityUtil.encodeUserInscriptionPassword(getSitoolsApplication().getSettings(), inscriptionInput);

        Inscription inscriptionOutput = getStore().create(inscriptionInput);
        response = new Response(true, inscriptionOutput, Inscription.class, "inscription");
      }

      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new registration request on client side");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
