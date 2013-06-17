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
package fr.cnes.sitools.datasource.common;

import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSourceFactory;

/**
 * A datasource Factory to get a {@link SitoolsDataSource} from its name
 * 
 * 
 * @author m.gond
 */
public final class SitoolsDataSourceFactory {

  /** Singleton instance */
  private static SitoolsDataSourceFactory instance = null;

  /**
   * Private constructor for utility class
   */
  private SitoolsDataSourceFactory() {
    super();
  }

  /**
   * Get an instance of SitoolsDatasource
   * 
   * @return an instance of SitoolsDatasource
   */
  public static synchronized SitoolsDataSourceFactory getInstance() {
    if (instance == null) {
      instance = new SitoolsDataSourceFactory();
    }
    return instance;
  }

  /**
   * Get the DataSource by name
   * 
   * @param dsName
   *          the identifier of the DataSource
   * @return SitoolsDataSource
   */
  public static SitoolsDataSource getDataSource(String dsName) {
    SitoolsDataSource datasource = SitoolsSQLDataSourceFactory.getDataSource(dsName);
    if (datasource != null) {
      return datasource;
    }
    else {
      return SitoolsMongoDBDataSourceFactory.getDataSource(dsName);
    }

  }

}
