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
package fr.cnes.sitools.dataset.database;

import fr.cnes.sitools.dataset.database.common.DefaultDatabaseRequest;
import fr.cnes.sitools.dataset.database.jdbc.SQLListIdDatabaseRequest;
import fr.cnes.sitools.dataset.database.jdbc.SQLRangeDatabaseRequest;
import fr.cnes.sitools.dataset.database.mongodb.MongoDBDatabaseRequest;
import fr.cnes.sitools.dataset.database.mongodb.MongoDBDistinctDatabaseRequest;
import fr.cnes.sitools.dataset.database.mongodb.MongoDBRangeDatabaseRequest;
import fr.cnes.sitools.datasource.common.DataSourceType;

/**
 * Factory to create DatabaseRequest
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public class DatabaseRequestFactory {
  /**
   * Constructor
   */
  protected DatabaseRequestFactory() {
    super();
  }

  /**
   * Get the SQL request
   * 
   * @param params
   *          the params for the request
   * @return a SQL request
   */
  public static DatabaseRequest getDatabaseRequest(DatabaseRequestParameters params) {

    // MONGO DB Database
    if (params.getDb().getDataSourceType().equals(DataSourceType.MONGODB)) {
      if (params.getRanges() != null) {
        return new MongoDBRangeDatabaseRequest(params);
      }
      else {
        if (params.getDistinct()) {
          return new MongoDBDistinctDatabaseRequest(params);
        }
        else {
          return new MongoDBDatabaseRequest(params);
        }
      }

    }
    else {
      // SQL DATABASE depending on the type of the request
      if (params.getIdList() != null) {
        return new SQLListIdDatabaseRequest(params, params.getIdList());
      }
      else if (params.getRanges() != null) {
        return new SQLRangeDatabaseRequest(params);
      }

      else {
        return new DefaultDatabaseRequest(params);
      }
    }
  }
}
