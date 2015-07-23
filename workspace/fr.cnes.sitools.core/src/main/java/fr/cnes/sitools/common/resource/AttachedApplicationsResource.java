package fr.cnes.sitools.common.resource;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Restlet;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.VirtualHost;
import org.restlet.util.RouteList;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;

/**
 * The Class AttachedApplicationsResource.
 * 
 * @author m.gond
 */
public class AttachedApplicationsResource extends SitoolsResource {

  /** The nb test max. */
  private int nbTestMax = 15;

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.resource.AbstractSitoolsResource#sitoolsDescribe()
   */
  @Override
  public void sitoolsDescribe() {
    setName("AttachedApplicationsResource");
    setDescription("Get all the applications \"physically\" attached");
  }

  /**
   * Gets the attached applications.
   * 
   * @param variant
   *          the variant
   * @return the attached applications
   */
  @Get
  public Representation getAttachedApplications(Variant variant) {
    List<VirtualHost> hosts = getSettings().getComponent().getHosts();
    VirtualHost host = hosts.get(0);
    Response response;
    if (host != null) {
      List<Resource> apps = new ArrayList<Resource>();
      RouteList routes = host.getRoutes();
      for (Route route : routes) {
        if (route instanceof TemplateRoute) {
          Restlet next = route.getNext();
          int nbAttachment = 0;
          while (nbAttachment < nbTestMax) {
            if (next instanceof Filter) {
              next = ((Filter) next).getNext();
            } else {
              break;
            }
            nbAttachment++;
          }

          Resource res = new Resource();
          res.setUrl(((TemplateRoute) route).getTemplate().getPattern());

          String className;
          if (this.getClass().isAnonymousClass()) {
            className = next.getClass().getSuperclass().getName();
          } else {
            className = next.getClass().getName();
          }
          res.setType(className);
          apps.add(res);
        }
      }

      response = new Response(true, apps, Resource.class, "applications");
      response.setTotal(apps.size());
    }
    else {
      response = new Response(false, "cannot find host");
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve all the applications \"physically\" attached");
    info.setIdentifier("AttachedApplicationsResource");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }
}
