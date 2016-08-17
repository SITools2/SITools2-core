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
package fr.cnes.sitools.tasks.business;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom log formatter for Order Task
 * 
 * 
 * @author m.gond
 */
class TaskLogFormatter extends Formatter {

  /**
   * Format the given log record and return the formatted string. The resulting formatted String will normally include a
   * localized and formated version of the LogRecord's message field. The Formatter.formatMessage convenience method can
   * (optionally) be used to localize and format the message field.
   * 
   * @param record
   *          the log record to be formatted.
   * @return the formatted log record
   */
  public String format(LogRecord record) {
    StringBuilder builder = new StringBuilder(1000);

    builder.append(formatMessage(record));
    builder.append("\n");
    return builder.toString();
  }

}
