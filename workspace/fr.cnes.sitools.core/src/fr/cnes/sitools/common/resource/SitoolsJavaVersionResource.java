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
package fr.cnes.sitools.common.resource;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;

/**
 * Returns the Java version
 * 
 * 
 * @author m.gond
 */
public class SitoolsJavaVersionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("SitoolsJavaVersionResource");
    setDescription("Gets the java version used");
  }

  /**
   * Get the Java Version used
   * 
   * @param variant
   *          the variant needed
   * @return the Java version as a {@link StringRepresentation}
   */
  @Get
  public Representation getJavaVersion(Variant variant) {
    return new StringRepresentation(System.getProperty("java.version"));
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
