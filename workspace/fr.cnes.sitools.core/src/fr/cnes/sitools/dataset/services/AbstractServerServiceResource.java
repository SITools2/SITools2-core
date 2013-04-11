package fr.cnes.sitools.dataset.services;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

public abstract class AbstractServerServiceResource extends AbstractServiceResource {

  /**
   * Get a FilterModelDTO from a Representation
   * 
   * @param representation
   *          the {@link Representation}
   * @return a FilterModelDTO
   * @throws IOException
   *           if there is an error while parsing the java object
   */
  public ResourceModelDTO getObjectResourceModel(Representation representation) throws IOException {
    ResourceModelDTO resourceInputDTO = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<ResourceModelDTO> obj = (ObjectRepresentation<ResourceModelDTO>) representation;
      resourceInputDTO = obj.getObject();
    }
    else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      XstreamRepresentation<ResourceModelDTO> xst = new XstreamRepresentation<ResourceModelDTO>(representation);
      xst.getXstream().alias("resourcePlugin", ResourceModelDTO.class);
      resourceInputDTO = xst.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      JacksonRepresentation<ResourceModelDTO> json = new JacksonRepresentation<ResourceModelDTO>(representation,
          ResourceModelDTO.class);
      resourceInputDTO = json.getObject();
    }
    return resourceInputDTO;
  }

  /**
   * Return the url of the resource plugin application
   * 
   * @return the url of the resource plugin application
   */
  public String getResourcesUrl() {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    return settings.getString(Consts.APP_APPLICATIONS_URL) + "/" + getParentId()
        + settings.getString(Consts.APP_RESOURCES_URL);
  }

  /**
   * Persist a given T object to the given url
   * 
   * @param object
   *          the object to persist
   * @param url
   *          the url
   * @param context
   *          the {@link Context}
   * @param method TODO
   * @return the persisted object
   */
  public Response handleResourceModelCall(ResourceModelDTO object, String url, Context context, Method method) {
    Representation entity = new ObjectRepresentation<ResourceModelDTO>(object);
    return RIAPUtils.handleParseResponse(url, entity, method, MediaType.APPLICATION_JAVA_OBJECT, context);
  }
  
  
  
}
