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
package fr.cnes.sitools.order;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * Resource for specific configuration of order management.
 * 
 * TODO Should be implemented Release 2.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OrderConfigurationResource extends AbstractOrderResource {

  @Override
  public void sitoolsDescribe() {
    setName("OrderConfigurationResource");
    setDescription("Resource for configuring the order management");
    setNegotiated(true);
  }

  /**
   * TODO Release 2
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation getConfiguration(Variant variant) {
    return new EmptyRepresentation();
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the current configuration of orders (NOT IMPLEMENTED YET)");
    this.addStandardGetRequestInfo(info);
    this.addStandardSimpleResponseInfo(info);
  }

  /**
   * TODO Release 2
   * 
   * @param representation
   *          Order representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation setConfiguration(Representation representation, Variant variant) {
    return new EmptyRepresentation();
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify configuration of orders (NOT IMPLEMENTED YET)");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardSimpleResponseInfo(info);
  }
  
}
