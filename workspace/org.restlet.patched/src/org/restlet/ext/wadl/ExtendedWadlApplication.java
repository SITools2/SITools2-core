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
package org.restlet.ext.wadl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;

/**
 * WadlApplication extension to produce contextual resource information based on the url attachment
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ExtendedWadlApplication extends WadlApplication {

  /**
   * Default constructor
   */
  public ExtendedWadlApplication() {
    super();
  }

  /**
   * Constructor
   * 
   * @param context
   *          Context
   * @param wadl
   *          Representation
   */
  public ExtendedWadlApplication(Context context, Representation wadl) {
    super(context, wadl);
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructor
   * 
   * @param context
   *          Context
   */
  public ExtendedWadlApplication(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructor
   * 
   * @param wadl
   *          Representation
   */
  public ExtendedWadlApplication(Representation wadl) {
    super(wadl);
    // TODO Auto-generated constructor stub
  }

  /**
   * Returns a WADL description of the current application. By default, this method discovers all the resources attached
   * to this application. It can be overridden to add documentation, list of representations, etc.
   * 
   * @param request
   *          The current request.
   * @param response
   *          The current response.
   * @return An application description.
   */
  @Override
  protected ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo applicationInfo = new ApplicationInfo();
    applicationInfo.getResources().setBaseRef(new Reference(request.getResourceRef().getBaseRef()) {

      private String decoded = Reference.decode(this.toString());

      @Override
      public String toString() {
        return (null != decoded) ? decoded : super.toString();
      }

    });
    applicationInfo.getResources().setResources(
        this.getResourceInfos(applicationInfo, this.getFirstRouter(getInboundRoot()), request, response));
    return applicationInfo;
  }

  /**
   * Returns the first router available.
   * 
   * @param current
   *          The current Restlet to inspect.
   * @return The first router available.
   */
  private Router getFirstRouter(Restlet current) {
    Router result = getRouter();

    if (result == null) {
      if (current instanceof Router) {
        result = (Router) current;
      }
      else if (current instanceof Filter) {
        result = getFirstRouter(((Filter) current).getNext());
      }
    }

    return result;
  }

  /**
   * Completes the data available about a given Filter instance.
   * 
   * @param applicationInfo
   *          The parent application.
   * @param filter
   *          The Filter instance to document.
   * @param path
   *          The base path.
   * @param request
   *          The current request.
   * @param response
   *          The current response.
   * @return The resource description.
   */
  private ResourceInfo getResourceInfo(ApplicationInfo applicationInfo, Filter filter, String path, Request request,
      Response response) {
    return getResourceInfo(applicationInfo, filter.getNext(), path, request, response);
  }

  /**
   * Completes the data available about a given Finder instance.
   * 
   * @param applicationInfo
   *          The parent application.
   * @param finder
   *          The Finder instance to document.
   * @param request
   *          The current request.
   * @param response
   *          The current response.
   * @param path
   *          url attachment of the finder in application
   * @return ResourceInfo
   */
  private ResourceInfo getResourceInfo(ApplicationInfo applicationInfo, Finder finder, String path, Request request,
      Response response) {
    ResourceInfo result = null;
    Object resource = null;

    // Save the current application
    Application.setCurrent(this);

    if (finder instanceof Directory) {
      resource = finder;
    }
    else {
      // The handler instance targeted by this finder.
      resource = finder.findTarget(request, response);

      if (resource == null) {
        ServerResource sr = finder.find(request, response);

        if (sr != null) {
          
          // AKKA PATCH - jp.boignard@akka.eu
          sr.init(getContext(path, sr), request, response);
          sr.updateAllowedMethods();
          resource = sr;
        }
      }
    }

    if (resource != null) {
      result = new ResourceInfo();
      ExtendedResourceInfo.describe(applicationInfo, result, resource, path);
    }

    return result;
  }

  protected Context getContext(String path, ServerResource sr) {
    return getContext();
  }
  
  /**
   * Completes the data available about a given Restlet instance.
   * 
   * @param applicationInfo
   *          The parent application.
   * @param restlet
   *          The Restlet instance to document.
   * @param request
   *          The current request.
   * @param response
   *          The current response.
   * @param path
   *          url attachment of the restlet in application
   * @return ResourceInfo documentation of the restlet
   */
  private ResourceInfo getResourceInfo(ApplicationInfo applicationInfo, Restlet restlet, String path, Request request,
      Response response) {
    ResourceInfo result = null;

    if (restlet instanceof WadlDescribable) {
      result = ((WadlDescribable) restlet).getResourceInfo(applicationInfo);
      result.setPath(path);
    }
    else if (restlet instanceof Finder) {
      result = getResourceInfo(applicationInfo, (Finder) restlet, path, request, response);
    }
    else if (restlet instanceof Router) {
      result = new ResourceInfo();
      result.setPath(path);
      result.setChildResources(getResourceInfos(applicationInfo, (Router) restlet, request, response));
    }
    else if (restlet instanceof Filter) {
      result = getResourceInfo(applicationInfo, (Filter) restlet, path, request, response);
    }

    return result;
  }

  /**
   * Returns the WADL data about the given Route instance.
   * 
   * @param applicationInfo
   *          The parent application.
   * @param route
   *          The Route instance to document.
   * @param basePath
   *          The base path.
   * @param request
   *          The current request.
   * @param response
   *          The current response.
   * @return The WADL data about the given Route instance.
   */
  private ResourceInfo getResourceInfo(ApplicationInfo applicationInfo, TemplateRoute route, String basePath, Request request,
      Response response) {
    String path = route.getTemplate().getPattern();

    // WADL requires resource paths to be relative to parent path
    if (path.startsWith("/") && basePath.endsWith("/")) {
      path = path.substring(1);
    }

    ResourceInfo result = getResourceInfo(applicationInfo, route.getNext(), path, request, response);
    return result;
  }

  /**
   * Completes the list of ResourceInfo instances for the given Router instance.
   * 
   * @param applicationInfo
   *          The parent application.
   * @param router
   *          The router to document.
   * @param request
   *          The current request.
   * @param response
   *          The current response.
   * @return The list of ResourceInfo instances to complete.
   */
  private List<ResourceInfo> getResourceInfos(ApplicationInfo applicationInfo, Router router, Request request,
      Response response) {
    List<ResourceInfo> result = new ArrayList<ResourceInfo>();

    for (final TemplateRoute route : router.getRoutes()) {
      ResourceInfo resourceInfo = getResourceInfo(applicationInfo, route, "/", request, response);

      if (resourceInfo != null) {
        result.add(resourceInfo);
      }
    }

    if (router.getDefaultRoute() != null) {
      ResourceInfo resourceInfo = getResourceInfo(applicationInfo, router.getDefaultRoute(), "/", request, response);
      if (resourceInfo != null) {
        result.add(resourceInfo);
      }
    }

    return result;
  }

}
