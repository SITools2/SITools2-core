package fr.cnes.sitools.common.resource;

import java.util.ArrayList;
import java.util.Set;
import org.restlet.data.Parameter;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.util.Series;
import java.util.List;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.util.Property;

/**
 * Gets the Jetty properties
 * 
 * 
 * @author m.gond
 */
public class JettyPropertiesResource extends SitoolsResource {

  /** parent application */
  private SitoolsApplication application = null;

  @Override
  public void sitoolsDescribe() {
    setName("JettyPropertiesResource");
    setDescription("Simple utility class to expose jetty server properties");

  }

  /**
   * Gets the list of properties used by Jetty
   * 
   * @param variant
   *          the Variant needed
   * @return the representation of the Jetty properties in the specified variant
   */
  @Get
  public Representation getJettyProperties(Variant variant) {

    application = (SitoolsApplication) getApplication();

    Series<Parameter> params = application.getSettings().getComponent().getServers().get(0).getContext()
        .getParameters();

    List<Property> list = new ArrayList<Property>();
    Set<String> paramnames = params.getNames();
    for (String name : paramnames) {
      list.add(new Property(name, params.getFirst(name).getValue(), null));
    }

    Response response = new Response(true, list, Property.class, "property");

    return getRepresentation(response, variant);

  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the jetty properties");
    info.setIdentifier("retrieve_jetty_properties");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
