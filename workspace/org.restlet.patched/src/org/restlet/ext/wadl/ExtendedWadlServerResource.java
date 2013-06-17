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

import org.restlet.data.Method;

/**
 * Extension of Restlet class to produce documentation of resource by the attachment path in an application
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ExtendedWadlServerResource extends WadlServerResource {

  /**
   * Returns a WADL description of the current resource.
   * 
   * @param path
   *          Path of the current resource.
   * @param info
   *          WADL description of the current resource to update.
   */
  public void describe(String path, ResourceInfo info) {
    ExtendedResourceInfo.describe(null, info, this, path);
  }

  /**
   * AKKA PATCH
   * 
   * @param method
   *          Method
   * @param info
   *          MethodInfo
   * @param path
   *          url attachment of the resource
   * @see #describeMethod(Method method, MethodInfo info)
   */
  protected void describeMethod(Method method, MethodInfo info, String path) {
    info.setName(method);

    if (Method.GET.equals(method)) {
      describeGet(info, path);
    }
    else if (Method.POST.equals(method)) {
      describePost(info, path);
    }
    else if (Method.PUT.equals(method)) {
      describePut(info, path);
    }
    else if (Method.DELETE.equals(method)) {
      describeDelete(info, path);
    }
    else if (Method.OPTIONS.equals(method)) {
      describeOptions(info, path);
    }
  }

  /**
   * AKKA PATCH To permit a path contextual description of a resource if it is attached several times
   * 
   * @param applicationInfo
   *          ApplicationInfo
   * @param path
   *          url attachement
   * @see #describeMethod(ApplicationInfo applicationInfo)
   */
  protected void describe(ApplicationInfo applicationInfo, String path) {
    describe(applicationInfo);
  }

  /**
   * AKKA PATCH Describes the DELETE method.
   * 
   * @param info
   *          The method description to update.
   * @param path
   *          url attachement
   * @see #describeDelete(MethodInfo info, String path)
   */
  protected void describeDelete(MethodInfo info, String path) {
    describeDelete(info);
  }

  /**
   * AKKA PATCH
   * 
   * Describes the GET method.<br>
   * By default, it describes the response with the available variants based on the {@link #getVariants()} method. Thus
   * in the majority of cases, the method of the super class must be called when overridden.
   * 
   * @param info
   *          The method description to update.
   * @param path
   *          url attachement
   * @see #describeGet(MethodInfo info)
   */
  protected void describeGet(MethodInfo info, String path) {
    describeGet(info);
  }

  /**
   * AKKA PATCH Describes the OPTIONS method.<br>
   * By default it describes the response with the available variants based on the {@link #getWadlVariants()} method.
   * 
   * @param info
   *          The method description to update.
   * @param path
   *          url attachement
   * @see #describeOptions(MethodInfo info)
   */
  protected void describeOptions(MethodInfo info, String path) {
    describeOptions(info);
  }

  /**
   * AKKA PATCH Describes the POST method.
   * 
   * @param info
   *          The method description to update.
   * @param path
   *          url attachement
   * @see #describePost(MethodInfo info)
   */
  protected void describePost(MethodInfo info, String path) {
    describePost(info);
  }

  /**
   * AKKA PATCH Describes the PUT method.
   * 
   * @param info
   *          The method description to update.
   * @param path
   *          url attachement
   * @see #describePut(MethodInfo info)
   */
  protected void describePut(MethodInfo info, String path) {
    describePut(info);
  }

}
