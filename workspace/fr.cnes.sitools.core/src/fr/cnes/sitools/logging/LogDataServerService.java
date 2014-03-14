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
package fr.cnes.sitools.logging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.engine.log.AccessLogFileHandler;
import org.restlet.engine.log.LogFilter;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;
import org.restlet.service.LogService;

import fr.cnes.sitools.util.logging.SitoolsLogFilter;

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
   * @param outputFile
   *          Path to store the log file
   * @param levelName
   *          level name of the log
   * @param logFormat
   *          logging template
   * @param logName
   *          logger name
   * @param enabled
   *          True to make enable the service. False to make disable the service
   * @see http://java.sun.com/javase/6/docs/api/java/util/logging/Level.html
   */
  public LogDataServerService(String logName, boolean enabled) {
    super(enabled);
    try {
//      if (logFormat != null && !logFormat.equals("")) {
//        this.setLogFormat(logFormat);
//      }
      this.setLoggerName(logName);

//      Level level = Level.parse(levelName);
//
//      AccessLogFileHandler accessLogFileHandler = new AccessLogFileHandler(outputFile, true);
//      accessLogFileHandler.setFormatter(new LogAccessFormatter());
//      accessLogFileHandler.setLevel(level);

      if ((logName != null) && !logName.equals("")) {
        logger = Engine.getLogger(logName);
      }
//      logger.setLevel(level);
//      logger.setUseParentHandlers(false);
//      logger.addHandler(accessLogFileHandler);
    }
    // catch (IOException ex) {
    // throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex);
    // }
    catch (SecurityException ex) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex);
    }
  }
}
