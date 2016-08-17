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
package fr.cnes.sitools.resources.programs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read an inputStream and write it to a Logger
 * 
 * 
 * @author m.gond
 */
public class StreamLogGobbler extends Thread {
  /** The InputStream to read */
  private InputStream is;
  /** The logger to write to */
  private Logger logger;
  /** The Level to use when writing in the logger */
  private Level level;

  /**
   * Create a new {@link StreamLogGobbler}
   * 
   * @param is
   *          an {@link InputStream}
   * @param logger
   *          a {@link Logger}
   * @param level
   *          a {@link Level}
   */
  public StreamLogGobbler(final InputStream is, Logger logger, Level level) {
    this.is = is;
    this.logger = logger;
    this.level = level;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line = br.readLine();
      while (line != null) {
        logger.log(level, line);
        line = br.readLine();
      }
      br.close();
    }
    catch (IOException ioe) {
      logger.log(level, ioe.getMessage(), ioe);
    }
  }

}
