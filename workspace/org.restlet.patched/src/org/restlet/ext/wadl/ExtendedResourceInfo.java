 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import org.restlet.data.Method;
import org.restlet.representation.Variant;
import org.restlet.resource.Directory;
import org.restlet.resource.ServerResource;

/**
 * Extension of ResourceInfo in order to produce a contextual documentation of method for each url attachment of the
 * resource.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ExtendedResourceInfo extends ResourceInfo {

  /**
   * Returns a WADL description of the current resource.
   * 
   * @param applicationInfo
   *          The parent application.
   * @param resource
   *          The resource to describe.
   * @param path
   *          Path of the current resource.
   * @param info
   *          WADL description of the current resource to update.
   */
  @SuppressWarnings("deprecation")
  public static void describe(ApplicationInfo applicationInfo, ResourceInfo info, Object resource, String path) {
    if ((path != null) && path.startsWith("/")) {
      path = path.substring(1);
    }

    info.setPath(path);

    // Introspect the current resource to detect the allowed methods
    List<Method> methodsList = new ArrayList<Method>();

    if (resource instanceof ServerResource) {
      ((ServerResource) resource).updateAllowedMethods();
      methodsList.addAll(((ServerResource) resource).getAllowedMethods());

      if (resource instanceof ExtendedWadlServerResource) {
        info.setParameters(((WadlServerResource) resource).describeParameters());

        if (applicationInfo != null) {
          ((ExtendedWadlServerResource) resource).describe(applicationInfo, path);
        }
      }
      else if (resource instanceof WadlServerResource) {
        info.setParameters(((WadlServerResource) resource).describeParameters());

        if (applicationInfo != null) {
          ((WadlServerResource) resource).describe(applicationInfo);
        }
      }
    }
    else if (resource instanceof org.restlet.resource.Resource) {
      methodsList.addAll(((org.restlet.resource.Resource) resource).getAllowedMethods());

      if (resource instanceof WadlResource) {
        info.setParameters(((WadlResource) resource).getParametersInfo());
      }
    }
    else if (resource instanceof Directory) {
      Directory directory = (Directory) resource;
      methodsList.add(Method.GET);

      if (directory.isModifiable()) {
        methodsList.add(Method.DELETE);
        methodsList.add(Method.PUT);
      }
    }

    Method.sort(methodsList);

    // Update the resource info with the description of the allowed methods
    List<MethodInfo> methods = info.getMethods();
    MethodInfo methodInfo;

    for (Method method : methodsList) {
      methodInfo = new MethodInfo();
      methods.add(methodInfo);
      methodInfo.setName(method);

      if (resource instanceof ServerResource) {
        /** AKKA PATCH - case of ExtendedWadlServerResource */
        if (resource instanceof ExtendedWadlServerResource) {
          ExtendedWadlServerResource wsResource = (ExtendedWadlServerResource) resource;

          if (wsResource.canDescribe(method)) {

            wsResource.describeMethod(method, methodInfo, path);
          }
        }
        else if (resource instanceof WadlServerResource) {
          WadlServerResource wsResource = (WadlServerResource) resource;

          if (wsResource.canDescribe(method)) {
            wsResource.describeMethod(method, methodInfo);
          }
        }
        else {
          MethodInfo.describeAnnotations(methodInfo, (ServerResource) resource);
        }
      }
      else if (resource instanceof org.restlet.resource.Resource) {
        if (resource instanceof WadlResource) {
          WadlResource wsResource = (WadlResource) resource;

          if (wsResource.isDescribable(method)) {
            wsResource.describeMethod(method, methodInfo);
          }
        }
        else {
          // Can document the list of supported variants.
          if (Method.GET.equals(method)) {
            ResponseInfo responseInfo = null;

            for (Variant variant : ((org.restlet.resource.Resource) resource).getVariants()) {
              RepresentationInfo representationInfo = new RepresentationInfo();
              representationInfo.setMediaType(variant.getMediaType());

              if (responseInfo == null) {
                responseInfo = new ResponseInfo();
                methodInfo.getResponses().add(responseInfo);
              }

              responseInfo.getRepresentations().add(representationInfo);
            }
          }
        }
      }
    }

    // Document the resource
    String title = null;
    String textContent = null;

    if (resource instanceof WadlServerResource) {
      title = ((WadlServerResource) resource).getName();
      textContent = ((WadlServerResource) resource).getDescription();
      //SITOOLS2 patch to set an emtpy string if there is no description
      //It makes the wadl to crash is the string is null
	  if (textContent == null) {
		textContent = new String();
	  }
    }
    else if (resource instanceof WadlResource) {
      title = ((WadlResource) resource).getTitle();
    }

    if ((title != null) && !"".equals(title)) {
      DocumentationInfo doc = null;

      if (info.getDocumentations().isEmpty()) {
        doc = new DocumentationInfo();
        info.getDocumentations().add(doc);
      }
      else {
        info.getDocumentations().get(0);
      }

      doc.setTitle(title);
      doc.setTextContent(textContent);
    }
  }
}
