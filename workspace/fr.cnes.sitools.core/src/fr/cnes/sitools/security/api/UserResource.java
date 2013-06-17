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
package fr.cnes.sitools.security.api;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * REST API for CRUD operations on a User resource
 * 
 * @author AKKA
 * 
 */
public interface UserResource {

  /**
   * Get XML representation
   * @return XML
   */
  @Get("xml")
  Representation getXML();

  /**
   * Get JSON representation
   * @return JSON
   */
  @Get("json")
  Representation getJSON();

  /**
   * Get class representation
   * @return class
   */
  @Get("class")
  Representation getObject();


  /**
   * update a user
   * @param representation the representation used
   * @param variant the variant used
   * @return a representation of the user
   */
  @Put
  Representation update(Representation representation, Variant variant);

  /**
   * delete a user
   * @param variant the variant used
   * @return a representation of the group user
   */
  @Delete
  Representation delete(Variant variant);

}
