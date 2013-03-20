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
package fr.cnes.sitools.resources.tasks.test;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.tasks.TaskUtils;

/**
 * Task resource Facade used for tests
 * 
 * 
 * @author m.gond
 */
public class MyTaskResourceFacade extends SitoolsParameterizedResource {

  @Override
  public void sitoolsDescribe() {
    setName(this.getClass().getName());
    setDescription("MyTaskResourceFacade");
    setNegotiated(false);
  }

  /**
   * Get method for tests
   * 
   * @return a Representation
   */
  @Get
  public Representation testGet() {
    return TaskUtils.execute(this, null);
  }

  /**
   * Get method for tests
   * 
   * @param variant
   *          the Variant
   * @return a Representation
   */
  @Get
  public Representation testGet(Variant variant) {
    return TaskUtils.execute(this, variant);
  }

  /**
   * Post method for tests
   * 
   * @param variant
   *          the Variant
   * @param param
   *          the Representation entity in entry
   * @return a Representation
   */
  @Post
  public Representation testPost(Representation param, Variant variant) {
    return TaskUtils.execute(this, variant);
  }

  /**
   * Put method for tests
   * 
   * @param variant
   *          the Variant
   * @param param
   *          the Representation entity in entry
   * @return a Representation
   */
  @Put
  public Representation testPut(Representation param, Variant variant) {
    return TaskUtils.execute(this, variant);
  }

  /**
   * Delete method for tests
   * 
   * @param variant
   *          the Variant
   * 
   * @return a Representation
   */
  @Delete
  public Representation testDelete(Variant variant) {
    return TaskUtils.execute(this, variant);
  }

}
