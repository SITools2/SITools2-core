package fr.cnes.sitools.common.resource;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.util.Util;

/**
 * Simple utility class to expose sitools.properties
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class SitoolsSettingsResource extends AbstractSitoolsResource {

  /** parent application */
  private SitoolsApplication application = null;

  @Override
  protected void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (SitoolsApplication) getApplication();
  }

  @Override
  public void sitoolsDescribe() {
    setName("SitoolsSettingsResource");
    setDescription("Simple utility class to expose sitools.properties");
  }

  /**
   * Get a setting value for the PARAMETER request attribute
   * 
   * @return StringRepresentation of setting value
   */
  @Get
  public Representation getSettings() {
    String parameter = (String) getRequest().getAttributes().get("PARAMETER");
    if (Util.isNotEmpty(parameter)) {
      return new StringRepresentation(application.getSettings().getString(parameter));
    }
    else {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
  }

  
  // TODO PUT to dynamically change settings values ...

}
