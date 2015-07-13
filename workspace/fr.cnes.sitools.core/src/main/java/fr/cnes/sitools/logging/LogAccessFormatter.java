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

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.restlet.engine.util.DateUtils;

/**
 * This class defines a new log format.
 * 
 * @author Jean-Christophe Malapert, CNES Copyright (C) 2010.
 */
public class LogAccessFormatter extends Formatter {
  /**
   * Define the log format
   * 
   * @param record
   *          Format
   * @return Returns a format such as Date - record
   */
  @Override
  public String format(LogRecord record) {
    return (DateUtils.format(new Date(), DateUtils.FORMAT_RFC_3339.get(0)) + " - " + record.getMessage() + "\n");
  }

}
