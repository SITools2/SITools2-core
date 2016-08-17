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
package fr.cnes.sitools.logging;

import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.service.LogService;

/**
 * Custom log service
 * 
 * @author Jean-Christophe Malapert, CNES Copyright (C) 2010.
 * @version 0.1
 * 
 * @see The GNU Public License (GPL)
 */
public class LogDataServerService extends LogService {

  /** Logger for DataServerService */
  private Logger logger = Engine.getLogger("fr.cnes.sitools");

  /**
   * Constructor
   * 
   * @param logName
   *          logger name
   * @param enabled
   *          True to make enable the service. False to make disable the service
   * @see http://java.sun.com/javase/6/docs/api/java/util/logging/Level.html
   */
  public LogDataServerService(String logName, boolean enabled) {
    super(enabled);
    try {
      this.setLoggerName(logName);

      if ((logName != null) && !logName.equals("")) {
        logger = Engine.getLogger(logName);
      }
    }
    catch (SecurityException ex) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex);
    }
  }
}
