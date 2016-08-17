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
package fr.cnes.sitools.resources.tasks.test;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * TaskResource implementation for tests
 * 
 * 
 * @author m.gond
 */
public class MyTaskResourceImpl extends MyTaskResourceFacade {

  @Override
  public Representation testGet() {
    return handle("TestGet");
  }

  @Override
  public Representation testPost(Representation param, Variant variant) {
    return handle("TestPost");
  }

  @Override
  public Representation testGet(Variant variant) {
    return handle("TestGet");
  }

  @Override
  public Representation testPut(Representation param, Variant variant) {
    return handle("TestPut");
  }

  @Override
  public Representation testDelete(Variant variant) {
    return handle("TestDelete");
  }

  /**
   * Handle the call
   * 
   * @param result
   *          the result to send
   * @return a new representation
   */
  private Representation handle(String result) {
    boolean error = Boolean.parseBoolean(getParameterValue("error"));
    if (error) {
      String errorMsg = getParameterValue("error_message");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, errorMsg);
    }
    else {
      boolean async = Boolean.parseBoolean(getParameterValue("async"));
      if (async) {
        try {
          Thread.sleep(2000);
        }
        catch (InterruptedException e) {
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
      }
      return new StringRepresentation(result);
    }
  }

}
