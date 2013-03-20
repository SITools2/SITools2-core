/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.userstorage;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * TODO
 * Resource for specific configuration of user storage (Example : default quota, multiple disk volumes ...)
 * User : ADMIN
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserStorageConfigurationResource extends AbstractUserStorageResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserStorageConfigurationResource");
    setDescription("Resource to get the list of user storages");
    setNegotiated(false);
  }

  /**
   * TODO
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
    //TODO
  }

  /**
   * TODO Release 2
   * 
   * Changement de quota par défaut à appliquer ... que faire ? Changement de repertoire root des répertoire
   * utilisateurs ... que faire ?
   * 
   * @param representation
   *          UserStorage representation
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
    //TODO
  }

}
