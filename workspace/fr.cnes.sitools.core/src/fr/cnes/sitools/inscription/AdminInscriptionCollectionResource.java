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

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.security.SecurityUtil;

/**
 * Resource for managing inscriptions (Role Administrator)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AdminInscriptionCollectionResource extends InscriptionResource {

  @Override
  public void sitoolsDescribe() {
    setName("AdminInscriptionCollectionResource");
    setDescription("Resource for managing inscriptions (CRUD &amp; validate)");
  }

  /**
   * Update / Validate existing inscription
   * 
   * @param representation
   *          Inscription representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newInscription(Representation representation, Variant variant) {
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

      // Business service

      // CRYPTAGE DU MOT DE PASSE SI NECESSAIRE
      SecurityUtil.encodeUserInscriptionPassword(getInscriptionApplication().getSettings(), inscriptionInput);

      Inscription inscriptionOutput = getStore().create(inscriptionInput);

      // Response
      Response response = new Response(true, inscriptionOutput, Inscription.class, "inscription");
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
    info.setDocumentation("Method to create a new inscription.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all inscriptions
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveInscription(Variant variant) {
    try {

      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<Inscription> inscriptions = getStore().getList(filter);
      int total = inscriptions.size();
      inscriptions = getStore().getPage(filter, inscriptions);
      Response response = new Response(true, inscriptions, Inscription.class, "inscriptions");
      response.setTotal(total);
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
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve all inscription(s).");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
