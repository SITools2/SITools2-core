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
package fr.cnes.sitools.dataset.database.common;

import fr.cnes.sitools.dataset.database.jdbc.RequestMysql;
import fr.cnes.sitools.dataset.database.jdbc.RequestPostgres;
import fr.cnes.sitools.dataset.database.jdbc.RequestSql;

/**
 * Class to create a request
 * 
 * @author AKKA
 */
public class RequestFactory {

  /**
   * Constructor
   */
  protected RequestFactory() {
    super();
  }

  /**
   * Get the SQL request
   * 
   * @param type
   *          the driver type
   * @return a SQL request
   */
  public static RequestSql getRequest(String type) {
    if (type.equals("org.postgresql.Driver")) {
      return new RequestPostgres();
    }
    else if (type.equals("org.gjt.mm.mysql.Driver")) {
      return new RequestMysql();

    }
    return new RequestPostgres();
  }

}
