    /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.engine.Engine;
import org.restlet.engine.log.LogFilter;
import org.restlet.engine.log.LogUtils;
import org.restlet.routing.Template;
import org.restlet.service.LogService;

import fr.cnes.sitools.common.application.ContextAttributes;

/**
 * Specific {@link LogFilter} to log request dedicated only to certain applications
 * 
 * 
 * @author m.gond
 */
public class SitoolsApplicationLogFilter extends LogFilter {
  /** The logger to log to */
  private Logger logger;

  /**
   * Constructor with Context and LogService
   * 
   * @param context
   *          the Context
   * @param logService
   *          the {@link LogService}
   */
  public SitoolsApplicationLogFilter(Context context, LogService logService) {
    super(context, logService);
    if (logService != null) {
      if (logService.getLoggerName() != null) {
        this.logger = Engine.getLogger(logService.getLoggerName());
      }
      else if ((context != null) && (context.getLogger().getParent() != null)) {
        this.logger = Engine.getLogger(context.getLogger().getParent().getName() + "."
            + LogUtils.getBestClassName(logService.getClass()));
      }
      else {
        this.logger = Engine.getLogger(LogUtils.getBestClassName(logService.getClass()));
      }
    }
  }

  @Override
  protected void afterHandle(Request request, Response response) {
    Application application = getApplication();
    if (application != null) {
      Context appContext = getApplication().getContext();
      if (appContext.getAttributes().get(ContextAttributes.LOG_TO_APP_LOGGER) != null
          && (Boolean) appContext.getAttributes().get(ContextAttributes.LOG_TO_APP_LOGGER)) {
        if (logger.isLoggable(Level.INFO)) {
          // Format the call into a log entry
          if (logService != null && logService.getLoggableTemplate() != null) {
            Template logTemplate = logService.getLoggableTemplate();
            logger.log(Level.INFO, logTemplate.format(request, response));
          }
        }
      }
      // else {
      // // super.afterHandle(request, response);
      // }
    }
    // else {
    // // super.afterHandle(request, response);
    // }

  }
}
